package itmo.escience.entities

/**
 * Created by user on 19.01.2016.
 */
trait IClavireAppScheduler {
  def schedule(workflow:Workflow, resource:java.util.List[Resource])
}
