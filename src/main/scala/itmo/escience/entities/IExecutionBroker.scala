package itmo.escience.entities

trait IExecutionBroker {

  def DefineTask(task:TaskDescription)

  def DefineDependencies(dependencies:Seq[TaskDependency])

  def Execute(taskIds:Seq[Long])

  def Abort(taskId:Seq[Long])

  def GetInfo(taskId:Long):Task

  def GetBriefTaskList():Seq[BriefTaskInfo]

  def GetNewTaskId():Long

  def Update()
}
