package itmo.escience.entities

import itmo.escience.entities.status.TaskStatus

final class Task(TaskId:Long,
           WfId:String,
           UserId:String,
           UserCert:String,
           Priority:TaskPriority,
           LaunchMode:TaskLaunchMode,
           Package:String,
           Method:String,
           InputFiles:Array[TaskFileDescription],
           OutputFiles:Array[TaskFileDescription],
           Params:java.util.Map[String, String],
           ExecParams:java.util.Map[String, String],
            var OutputParams:java.util.Map[String,String],
            var Estimations:java.util.Map[String, Double],
            var CurrentSchedule:TaskSchedule,
            val State:TaskStatus,
            val AssignedResource:ResourceTotals) extends TaskDescription(TaskId, WfId, UserId, UserCert, Priority,
                                                                  LaunchMode, Package, Method, InputFiles, OutputFiles,
                                                                  Params, ExecParams)

final class ResourceTotals(var ResourceName:String,
                           var ResourceDescription:String,
                           var Location:String,
                           var ProviderName:String,
                           var NodesTotal:Int,
                           var SupportedArchitectures: Seq[String])