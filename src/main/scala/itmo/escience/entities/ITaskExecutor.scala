package itmo.escience.entities

import itmo.escience.entities.listeners.TaskStatusUpdateListener
import itmo.escience.entities.status.TaskStatus

/**
 * Created by user on 19.01.2016.
 */
trait ITaskExecutor {

  def addTaskStatusUpdatedListener(listener:TaskStatusUpdateListener)
  def executeTask(task: TaskDescription)
  def taskStatuses(tasks:java.util.List[TaskDescription]): java.util.List[(TaskDescription, TaskStatus)]

}
