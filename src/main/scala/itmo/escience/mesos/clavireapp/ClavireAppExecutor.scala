package itmo.escience.mesos.clavireapp

import com.google.protobuf.ByteString
import org.apache.mesos.Protos._
import org.apache.mesos.{MesosExecutorDriver, Executor, ExecutorDriver}

object ClavireAppExecutor {

  def main(args: Array[String]) {
    new MesosExecutorDriver(new ClavireAppExecutor(args)).run()
  }
}

class ClavireAppExecutor(args: Array[String]) extends Executor {
  override def shutdown(driver: ExecutorDriver): Unit = {}

  override def disconnected(driver: ExecutorDriver): Unit = {}

  override def killTask(driver: ExecutorDriver, taskId: TaskID): Unit = {}

  override def reregistered(driver: ExecutorDriver, slaveInfo: SlaveInfo): Unit = {}

  override def error(driver: ExecutorDriver, message: String): Unit = {}

  override def frameworkMessage(driver: ExecutorDriver, data: Array[Byte]): Unit = {}

  override def registered(driver: ExecutorDriver, executorInfo: ExecutorInfo, frameworkInfo: FrameworkInfo, slaveInfo: SlaveInfo): Unit = {}

  override def launchTask(driver: ExecutorDriver, task: TaskInfo): Unit = {
    new Thread(new Runnable {
      override def run(): Unit = {
        driver.sendStatusUpdate(TaskStatus.newBuilder().setTaskId(task.getTaskId).setState(TaskState.TASK_RUNNING).build())

        println(s"ClavireApp task ${task.getTaskId} is running")
        println("Got fom master " + args.mkString)

        val data = ByteString.copyFrom(s"result from task ${task.getTaskId.getValue}".getBytes)
        driver.sendStatusUpdate(TaskStatus.newBuilder().setTaskId(task.getTaskId).setState(TaskState.TASK_FINISHED).setData(data).build())

      }
    }).start()

  }
}
