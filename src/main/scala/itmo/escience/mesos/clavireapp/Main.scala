package itmo.escience.mesos.clavireapp

import org.apache.mesos.MesosSchedulerDriver
import org.apache.mesos.Protos.FrameworkInfo

object Main {

  def main(args: Array[String]) {
    println("Starting ClavireApp " + args(0))

    val framework = FrameworkInfo.newBuilder.
      setName("ClavireApp").
      setUser("").
      setRole("*").
      setCheckpoint(false).
      setFailoverTimeout(0.0d).
      build()

    val scheduler = new ClavireAppScheduler
    val masterUrl: String = args(0) + ":5050"

    new MesosSchedulerDriver(scheduler, framework, masterUrl).run()
  }
}
