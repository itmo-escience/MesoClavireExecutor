package itmo.escience.appcluster

import akka.actor._
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._


class MasterActor extends Actor with ActorLogging {

  throw new NotImplementedError()

  val cluster = Cluster(context.system)
  override def preStart() {
    cluster.subscribe(self, InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
    log.info("MasterActor preStart " + self)
  }

  override def postStop() {
    cluster.unsubscribe(self)
  }

  override def receive = {

    case UnreachableMember(member) =>
      log.info(s"[Listener] node is unreachable: $member")


    case MemberRemoved(member, prevStatus) =>
      log.info(s"[Listener] node is removed: $member after $prevStatus")


    case ev: MemberEvent =>
      log.info(s"[Listener] event: $ev")
  }

}
