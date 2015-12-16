package itmo.escience.appcluster

object Configuration {
  def getConfig(masterIp: String, myIp: String, role: String): String = {
    val port = if(role.equals("MasterActor")) 2551 else 0
    s"""
    akka {
      actor {
        provider = "akka.cluster.ClusterActorRefProvider"
      }

      remote {
        netty.tcp {
          hostname = "$myIp"
          port = $port
        }
      }

      cluster {
        roles = [$role]
        seed-nodes = [
          "akka.tcp://cluster@$masterIp:2551"
          ]

       auto-down-unreachable-after = 1s
      }
    }
                               """
  }
}
