package itmo.escience.entities.status

import itmo.escience.entities.errors.ClavireException
import itmo.escience.entities.handles.TaskId

sealed class TaskStatus
case class TaskDefined(id: Long) extends TaskStatus
case class TaskReady(id: Long) extends TaskStatus
case class TaskRunning(id: Long) extends TaskStatus
case class TaskFinished(id: Long) extends TaskStatus
case class TaskFailed(id: Long, error: ClavireException) extends TaskStatus


