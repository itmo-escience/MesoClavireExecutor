package itmo.escience.entities

import java.util


class Workflow(val WorkflowId:String,
               val Tasks: java.util.List[TaskDescription],
               val Dependencies:java.util.List[TaskDependency]){

  def this(WorkflowId:String) = this(WorkflowId,
    new util.ArrayList[TaskDescription](),
    new util.ArrayList[TaskDependency]())

  def addTask(task:TaskDescription) = {
    Tasks.add(task)
  }

  def addDependency(dependency:TaskDependency) = {
    Dependencies.add(dependency)
  }

}
