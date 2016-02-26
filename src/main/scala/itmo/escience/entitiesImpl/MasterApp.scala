package itmo.escience.entitiesImpl

import java.util
import java.util.concurrent.atomic.AtomicInteger

import itmo.escience.entities._
import org.apache.mesos.Protos._
import org.apache.mesos.{Protos, SchedulerDriver, Scheduler}
import org.slf4j.LoggerFactory
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

class MasterApp extends IAppsMasterController with Scheduler {

  private var idCounter:Long = 0

  override def runApp(wf: Workflow): IClavireAppController = {
    val id = generateId()
    new ClavireApp(id, wf)
  }

  private def generateId():Long = {
    idCounter += 1
    idCounter
  }


  private val logger = LoggerFactory.getLogger("common")
  private val numOfAllTasks = 1
  private val taskIDGenerator = new AtomicInteger()
  // TODO: add correct command to launch the "head" of an app
  private val command = "java -cp /SNCrawler/target/SNCrawler-1.0.jar:/SNCrawler/target/lib/* crawler.akka.MasterActorRunner slave1"
  private var numOfFinishedTasks = 0
  private var isRun = false


  private val cpus_app = Resource.newBuilder.
    setType(org.apache.mesos.Protos.Value.Type.SCALAR)
    .setName("cpus")
    .setScalar(org.apache.mesos.Protos.Value.Scalar.newBuilder.setValue(1.0))
    .setRole("*")
    .build

  private val mem_app = Resource.newBuilder.
    setType(org.apache.mesos.Protos.Value.Type.SCALAR)
    .setName("mem")
    .setScalar(org.apache.mesos.Protos.Value.Scalar.newBuilder.setValue(512.0))
    .setRole("*")
    .build


  override def offerRescinded(schedulerDriver: SchedulerDriver, offerID: OfferID): Unit = {}
  override def disconnected(schedulerDriver: SchedulerDriver): Unit = {}
  override def reregistered(schedulerDriver: SchedulerDriver, masterInfo: MasterInfo): Unit = {}
  override def slaveLost(schedulerDriver: SchedulerDriver, slaveID: SlaveID): Unit = {}
  override def error(schedulerDriver: SchedulerDriver, s: String): Unit = {}
  override def frameworkMessage(schedulerDriver: SchedulerDriver, executorID: ExecutorID, slaveID: SlaveID,
                                bytes: Array[Byte]): Unit = {}

  override def registered(schedulerDriver: SchedulerDriver, frameworkID: FrameworkID, masterInfo: MasterInfo): Unit = {}
  override def executorLost(schedulerDriver: SchedulerDriver, executorID: ExecutorID, slaveID: SlaveID, i: Int): Unit = {}

  override def statusUpdate(schedulerDriver: SchedulerDriver, taskStatus: TaskStatus): Unit = {

    logger.info(s"statusUpdate() task ${taskStatus.getTaskId.getValue} is in state ${taskStatus.getState}")

    // TODO: remake it
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

  override def resourceOffers(schedulerDriver: SchedulerDriver, offers: util.List[Offer]): Unit = {
    logger.info("resourceOffers() with {} offers", offers.size())
    if(!isRun) {
      logger.info("resourceOffers =", offers.map(_.getHostname).mkString(""))
      val slave1Offer = offers.find(offer => {

        val memory = offer.getResourcesList().find(_.getName() == "memory")
        val cpu = offer.getResourcesList().find(_.getName() == "cpu")

        memory.forall(_.getScalar().getValue >= mem_app.getScalar().getValue )&&
          cpu.forall(_.getScalar().getValue >= cpus_app.getScalar().getValue )
      })

      val task = generateAppRunnerTask()

      schedulerDriver.launchTasks(List(slave1Offer.get.getId).asJavaCollection,
        util.Arrays.asList(task.setSlaveId(slave1Offer.get.getSlaveId).build()))

      isRun = true
    }
  }

  private def generateAppRunnerTask() = {
    TaskInfo.newBuilder
      .setName("appRunner")
      .setTaskId(Protos.TaskID.newBuilder().setValue("appRunner"))
      .addResources(cpus_app).addResources(mem_app)
      .setCommand(CommandInfo.newBuilder().setValue(command))
  }
}
