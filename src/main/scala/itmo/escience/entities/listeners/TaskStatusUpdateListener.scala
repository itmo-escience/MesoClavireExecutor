package itmo.escience.entities.listeners

import itmo.escience.entities.status.TaskStatus

trait TaskStatusUpdateListener {
  def taskStatusUpdated(status:TaskStatus)
}
