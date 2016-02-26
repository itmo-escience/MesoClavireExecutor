package itmo.escience.entities

import itmo.escience.entities.listeners.{AppStatusUpdateListener, TaskStatusUpdateListener}
import itmo.escience.entities.status.{TaskStatus, AppStatus}

trait IClavireAppController {
  def addTaskStatusUpdatedListener(listener:TaskStatusUpdateListener)
  def addAppStatusUpdatedListener(listener:AppStatusUpdateListener)
  def appStatus(app:IClavireAppController):AppStatus
  def workflow():Workflow
  def taskStatus(task: Task):TaskStatus
  def getResult():java.util.List[FileDescription]
  def task(taskId:Long):Task
}
