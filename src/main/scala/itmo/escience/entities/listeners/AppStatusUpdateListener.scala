package itmo.escience.entities.listeners

import itmo.escience.entities.status.AppStatus

trait AppStatusUpdateListener {
  def statusUpdated(status:AppStatus):Unit
}
