package itmo.escience.entities

class TaskDescription(var TaskId:Long,
                      var WfId:String,
                      var UserId:String,
                      var UserCert:String,
                      var Priority:TaskPriority,
                      var LaunchMode:TaskLaunchMode,
                      var Package:String,
                      var Method:String,
                      var InputFiles:Array[TaskFileDescription],
                      var OutputFiles:Array[TaskFileDescription],
                      var Params:java.util.Map[String, String],
                      var ExecParams:java.util.Map[String, String]) {

}

class TaskFileDescription (var StorageId:String,
                           var FileName:String,
                           var SlotName:String)

class BriefTaskInfo(var TaskId:Long,
                    var WfId:String,
                    var UserId:String,
                    var Package:String,
                    var ResourceName:String,
                    var State:TaskState)

sealed class TaskLaunchMode
final case class Auto extends TaskLaunchMode
final case class Manual extends TaskLaunchMode

sealed class TaskPriority
final case class Normal extends TaskPriority
final case class Urgent extends TaskPriority

sealed class TaskState
final case class Defined extends TaskState
final case class ReadyToExecute extends TaskState
final case class Scheduled extends TaskState
final case class Started extends TaskState
final case class Aborted extends TaskState
final case class Completed extends TaskState
final case class Failed extends TaskState

