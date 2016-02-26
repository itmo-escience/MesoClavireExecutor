package itmo.escience.entities.listeners

import itmo.escience.entities.status.TaskExecutionStatus

trait TaskExecutionStatusUpdateListener {
  def taskExecutionStatusUpdated(status:TaskExecutionStatus)
}
