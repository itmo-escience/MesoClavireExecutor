package itmo.escience.entities.status

import itmo.escience.entities.handles.TaskId

sealed class TaskExecutionStatus
case class TaskExecutionStagingIn(id:TaskId) extends TaskExecutionStatus
case class TaskExecutionRunning(id:TaskId) extends TaskExecutionStatus
case class TaskExecutionStagingOut(id:TaskId) extends TaskExecutionStatus
case class TaskExecutionFinished(id:TaskId) extends TaskExecutionStatus
case class TaskExecutionFailed(id:TaskId) extends TaskExecutionStatus
