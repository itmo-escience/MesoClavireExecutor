package itmo.escience.entities

trait IAppsMasterController {
  def runApp(wf:Workflow):IClavireAppController
}
