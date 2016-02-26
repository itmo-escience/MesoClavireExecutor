package itmo.escience.entities

class Resource(val Id:Long,
               val path:String,
               var cpu:java.util.List[Long],
               var memory:Long)
