package itmo.escience.entitiesImpl

import java.util
import java.util.concurrent.atomic.AtomicInteger

import itmo.escience.entities._
import itmo.escience.entities.errors.ClavireException
import itmo.escience.entities.listeners.{TaskStatusUpdateListener, AppStatusUpdateListener}
import itmo.escience.entities.status.{AppCreated, TaskDefined, AppStatus, TaskStatus}
import org.apache.mesos.Protos._
import org.apache.mesos.{Protos, SchedulerDriver, Scheduler}
import org.slf4j.LoggerFactory
import scala.collection.JavaConversions._

class ClavireApp(val Id:Long, wf: Workflow) extends IClavireAppController with IClavireAppScheduler with Scheduler{

  private val _taskStatusUpdateListeners = new util.HashSet[TaskStatusUpdateListener]()
  private val _appStatusUpdateListeners = new util.HashSet[AppStatusUpdateListener]()
  private val _tasks:util.HashMap[Long, Task] = new util.HashMap[Long, Task]()

  for(desc <- wf.Tasks){
    _tasks.put(desc.TaskId, createTask(desc))
  }

  private var _appStatus = AppCreated(Id)

  private val logger = LoggerFactory.getLogger("common")
  private val numOfAllTasks = 1
  private val taskIDGenerator = new AtomicInteger()
  // TODO: add correct command to launch the "head" of an app
  private val command = "java -cp /SNCrawler/target/SNCrawler-1.0.jar:/SNCrawler/target/lib/* crawler.akka.MasterActorRunner slave1"
  private var numOfFinishedTasks = 0
  private var isRun = false

  override def addTaskStatusUpdatedListener(listener: TaskStatusUpdateListener): Unit = {
    _taskStatusUpdateListeners.synchronized{
      if (_taskStatusUpdateListeners.contains(listener))
        throw new ClavireException("Cannot add listener second time")
      _taskStatusUpdateListeners.add(listener)
    }
  }

  override def addAppStatusUpdatedListener(listener: AppStatusUpdateListener): Unit = {
    _appStatusUpdateListeners.synchronized{
      if (_appStatusUpdateListeners.contains(listener))
        throw new ClavireException("Cannot add listener second time")
      _appStatusUpdateListeners.add(listener)
    }
  }

  override def appStatus(app: IClavireAppController): AppStatus = _appStatus

  override def workflow(): Workflow = wf

  override def taskStatus(task: Task): TaskStatus = _tasks.get(task.WfId).State

  override def getResult(): util.List[FileDescription] = ???

  override def task(taskId: Long): Task = _tasks.get(taskId)

  private def createTask(description: TaskDescription) = {
    new Task( description.TaskId, description.WfId,
      description.UserId, description.UserCert,
      description.Priority,
      description.LaunchMode,
      description.Package,
      description.Method,
      description.InputFiles,
      description.OutputFiles,
      description.Params,
      description.ExecParams,
      OutputParams = new util.HashMap[String, String](),
      Estimations = new util.HashMap[String, Double](),
      null,
      TaskDefined(description.TaskId),
      null
    )
  }

  override def offerRescinded(schedulerDriver: SchedulerDriver, offerID: OfferID): Unit = {}

  override def disconnected(schedulerDriver: SchedulerDriver): Unit = {}

  override def reregistered(schedulerDriver: SchedulerDriver, masterInfo: MasterInfo): Unit = {}

  override def slaveLost(schedulerDriver: SchedulerDriver, slaveID: SlaveID): Unit = {}

  override def error(schedulerDriver: SchedulerDriver, s: String): Unit = {}

  override def frameworkMessage(schedulerDriver: SchedulerDriver, executorID: ExecutorID, slaveID: SlaveID, bytes: Array[Byte]): Unit = {}

  override def registered(schedulerDriver: SchedulerDriver, frameworkID: FrameworkID, masterInfo: MasterInfo): Unit = {}

  override def executorLost(schedulerDriver: SchedulerDriver, executorID: ExecutorID, slaveID: SlaveID, i: Int): Unit = {}

  override def statusUpdate(schedulerDriver: SchedulerDriver, taskStatus: Protos.TaskStatus): Unit = {
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

  override def resourceOffers(schedulerDriver: SchedulerDriver, list: util.List[Offer]): Unit = {
    // look if there are tasks ready to execute
    // for each task:
    //find resource (better to say find an offer) which the most suitable for that task (or min-min comes here)



  }

}
