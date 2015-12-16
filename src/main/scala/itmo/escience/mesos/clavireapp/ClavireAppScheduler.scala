package itmo.escience.mesos.clavireapp

import java.util
import java.util.concurrent.atomic.AtomicInteger

import org.apache.mesos.Protos._
import org.apache.mesos.{Protos, Scheduler, SchedulerDriver}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._


/**
 * Created by Max Petrov on 12.10.15.
 */
class ClavireAppScheduler extends Scheduler {
  val logger = LoggerFactory.getLogger("ClavireAppScheduler")

  val numOfAllTasks = 1
  val taskIDGenerator = new AtomicInteger()
  var numOfFinishedTasks = 0


  override def offerRescinded(schedulerDriver: SchedulerDriver, offerID: OfferID): Unit = {}

  override def disconnected(schedulerDriver: SchedulerDriver): Unit = {}

  override def reregistered(schedulerDriver: SchedulerDriver, masterInfo: MasterInfo): Unit = {}

  override def slaveLost(schedulerDriver: SchedulerDriver, slaveID: SlaveID): Unit = {}

  override def error(schedulerDriver: SchedulerDriver, s: String): Unit = {}

  override def statusUpdate(schedulerDriver: SchedulerDriver, taskStatus: TaskStatus): Unit = {

    logger.info(s"statusUpdate() task ${taskStatus.getTaskId.getValue} is in state ${taskStatus.getState}")

    if (taskStatus.getState.equals(TaskState.TASK_FINISHED)) {
      numOfFinishedTasks = numOfFinishedTasks + 1
      val executorResult = taskStatus.getData.toStringUtf8
      logger.info(s"executorResult = $executorResult")

    }

    if (numOfFinishedTasks >= numOfAllTasks) {
      logger.info("All tasks finished")
      schedulerDriver.stop()
    }
  }

  override def frameworkMessage(schedulerDriver: SchedulerDriver, executorID: ExecutorID, slaveID: SlaveID, bytes: Array[Byte]): Unit = {}

  val cpus1 = Resource.newBuilder.
    setType(org.apache.mesos.Protos.Value.Type.SCALAR)
    .setName("cpus")
    .setScalar(org.apache.mesos.Protos.Value.Scalar.newBuilder.setValue(1.0))
    .setRole("*")
    .build
  val mem1 = Resource.newBuilder.
    setType(org.apache.mesos.Protos.Value.Type.SCALAR)
    .setName("mem")
    .setScalar(org.apache.mesos.Protos.Value.Scalar.newBuilder.setValue(128.0))
    .setRole("*")
    .build

  val masterActorRunner = TaskInfo.newBuilder
    .setName("masterActorRunner")
    .setTaskId(Protos.TaskID.newBuilder().setValue("masterActorRunner"))
    .addResources(cpus1).addResources(mem1)
    .setCommand(CommandInfo.newBuilder().setValue("java -cp /SNCrawler/target/SNCrawler-1.0.jar:/SNCrawler/target/lib/* crawler.akka.MasterActorRunner slave1"))

  val twitterStreamActorRunner = TaskInfo.newBuilder
    .setName("twitterStreamActorRunner")
    .setTaskId(Protos.TaskID.newBuilder().setValue("twitterStreamActorRunner"))
    .addResources(cpus1).addResources(mem1)
    .setCommand(CommandInfo.newBuilder().setValue("java -cp /SNCrawler/target/SNCrawler-1.0.jar:/SNCrawler/target/lib/* crawler.akka.TwitterStreamActorRunner slave1 slave2"))

  val followerFinderActorRunner = TaskInfo.newBuilder
    .setName("followerFinderActorRunner")
    .setTaskId(Protos.TaskID.newBuilder().setValue("followerFinderActorRunner"))
    .addResources(cpus1).addResources(mem1)
    .setCommand(CommandInfo.newBuilder().setValue("java -cp /SNCrawler/target/SNCrawler-1.0.jar:/SNCrawler/target/lib/* crawler.akka.FollowerFinderActorRunner slave1 slave3"))

  var isRun = false

  override def resourceOffers(schedulerDriver: SchedulerDriver, offers: util.List[Offer]): Unit = {

    // modes of functioning
    // 1. Run long-living executors (with one long-lived task - ???) than run tasks with this executors
    //    - run master on the same node where scheduler lives
    //    - each executor runs a slave (or is a slave)
    //    - master and slaves forms individual cluster for the app
    //    - app is started to execute on existed slaves (slave spawn individual tasks or containers)
    //      - when new resources appear, it can be incorporated into the cluster and the master can reschedule the process
    //      - when resources goes down (by any reason - task or node is it) and the master can reschedule the process
    // 2. Run many mesos tasks
    //  Simple Logic (min-min like)
    //    1. take a bunch of offers
    //    2. find minimal task by resource requirements or processing time
    //    3. find the most suitable offer for that task
    //    4. run task on that offer
    //  Either there is an option to reserve resources for some time (- ???)

    logger.info("resourceOffers() with {} offers", offers.size())
    if(!isRun) {
      logger.info("resourceOffers =", offers.asScala.map(_.getHostname).mkString(""))
      val slave1Offer = offers.asScala.find(_.getHostname.startsWith("slave1"))
      val slave2Offer = offers.asScala.find(_.getHostname.startsWith("slave2"))
      val slave3Offer = offers.asScala.find(_.getHostname.startsWith("slave3"))

      schedulerDriver.launchTasks(slave1Offer.get.getId, util.Arrays.asList(masterActorRunner.setSlaveId(slave1Offer.get.getSlaveId).build()))
      schedulerDriver.launchTasks(slave2Offer.get.getId, util.Arrays.asList(twitterStreamActorRunner.setSlaveId(slave2Offer.get.getSlaveId).build()))
      schedulerDriver.launchTasks(slave3Offer.get.getId,util.Arrays.asList(followerFinderActorRunner.setSlaveId(slave3Offer.get.getSlaveId).build()))

      isRun = true
    }
  }


  override def registered(schedulerDriver: SchedulerDriver, frameworkID: FrameworkID, masterInfo: MasterInfo): Unit = {}

  override def executorLost(schedulerDriver: SchedulerDriver, executorID: ExecutorID, slaveID: SlaveID, i: Int): Unit = {}
}
