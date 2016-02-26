package itmo.escience.entities

import java.io.File
import java.nio.file.Path

import scala.concurrent.Future

trait IDataManager {
  def downloadToFile(fileDescription: FileDescription, path: String): Future[File]
  def uploadFile(pathFrom:String, pathTo:String): FileDescription
}
