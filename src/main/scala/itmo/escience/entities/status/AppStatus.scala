package itmo.escience.entities.status

import java.util.Date

import itmo.escience.entities.errors.ClavireException
import itmo.escience.entities.handles.AppId

/**
 * Created by user on 19.01.2016.
 */
// TODO: should AppID be Long, special handle or app itself?
sealed class AppStatus
case class AppCreated(id:Long) extends AppStatus
case class AppStarted(id:Long) extends AppStatus
case class AppFinished(id:Long) extends AppStatus
case class AppFailed(id:Long, error:ClavireException) extends AppStatus

