package itmo.escience.entitiesImpl

import java.util
import itmo.escience.entities.errors.ClavireException

import scala.collection.JavaConversions._

import itmo.escience.entities._

final class ExecutorBroker extends IExecutionBroker{

  private val lockObject:AnyRef = new Object()

  private val lockUpdate:AnyRef = new Object()

  private var currentCounter:Long = 0

  private val _apps: java.util.HashMap[String, IClavireAppController] = new util.HashMap[String, IClavireAppController]()

  private val _masterApp: MasterApp = new MasterApp()

  private val _taskToAppMapping: java.util.HashMap[Long, IClavireAppController] = new util.HashMap[Long, IClavireAppController]()

  def Workflows = _apps.values().map(app => app.workflow()).toList

  override def DefineTask(task: TaskDescription): Unit = {
    if (!_apps.contains(task.WfId)){
      val app = _masterApp.runApp(new Workflow(task.WfId))
      _apps.put(task.WfId, app)
    }

    val app = _apps.get(task.WfId)
    app.workflow().addTask(task)
    _taskToAppMapping.put(task.TaskId, app)
  }

  override def GetNewTaskId(): Long = {
    var id:Long = 0
    lockObject.synchronized {
      currentCounter += 1
      id = currentCounter
    }
    id
  }

  override def DefineDependencies(dependencies: Seq[TaskDependency]): Unit = {
    val not_all_workflows_created = dependencies.exists(d => !_apps.contains(d.WfId))
    if (not_all_workflows_created){
      throw new ClavireException("There are some dependencies which try to be defined before their workflows")
    }

    for(dependency <- dependencies) {
      val app = _apps.get(dependency.WfId)
      app.workflow().addDependency(dependency)
    }

  }

  override def Update(): Unit = {
    // do nothing.
    // this method is necessary to provide consistance interface for CLAVIRE frontend part
    // action of update is not neccessary in MesoClavireExecutor
  }

  override def GetInfo(taskId: Long): Task = _taskToAppMapping.get(taskId).task(taskId)

  override def Abort(taskId: Seq[Long]): Unit = {
    _taskToAppMapping.get(taskId)
  }

  override def GetBriefTaskList(): Seq[BriefTaskInfo] = {
    _taskToAppMapping.map({case (taskId, app) =>
        val task = app.task(taskId)
        createBriefTaskInfo(task)
    }).toSeq
  }

  override def Execute(taskIds: Seq[Long]): Unit = {
    // do nothing.
    // this method is necessary to provide consistance interface for CLAVIRE frontend part
    // actual run of tasks already happened in DefineTask methods
  }

  private def createBriefTaskInfo(task:Task) = {
    new BriefTaskInfo(task.TaskId,
      task.WfId,
      task.UserId,
      task.Package,
      task.AssignedResource.ResourceName,
      task.State)
  }
}
