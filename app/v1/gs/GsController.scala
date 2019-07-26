package v1.gs

import akka.Done
import akka.util.Timeout
import csw.client.utils.Extensions.FutureExt
import csw.command.api.scaladsl.CommandService
import csw.command.client.CommandServiceFactory
import csw.framework.CswClientWiring
import csw.framework.commons.CoordinatedShutdownReasons.ApplicationFinishedReason
import csw.location.api.models.ComponentType.{Assembly, HCD}
import csw.location.api.models.Connection.AkkaConnection
import csw.location.api.models.{AkkaLocation, ComponentId, ComponentType}
import csw.params.commands.{CommandName, CommandResponse, Setup}
import csw.params.core.generics.{Key, KeyType, Parameter}
import csw.params.core.models.{ObsId, Prefix}
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}



/**
  * Takes HTTP requests and produces JSON.
  */
class GsController @Inject()(cc: GsControllerComponents)
  extends GsBaseController(cc) {

  //  private val logger = Logger(getClass)


  lazy val clientWiring = new CswClientWiring
  import clientWiring._
  import wiring._
  import actorRuntime._

  def assemblyCommandService(assemblyName: String): CommandService = createCommandService(getAkkaLocation(assemblyName, Assembly))

  def hcdCommandService(hcdName: String): CommandService = createCommandService(getAkkaLocation(hcdName, HCD))

  def shutdown(): Done = wiring.actorRuntime.shutdown(ApplicationFinishedReason).await()

  private def getAkkaLocation(name: String, cType: ComponentType): AkkaLocation = {
    val maybeLocation = locationService.resolve(AkkaConnection(ComponentId(name, cType)), timeout).await()
    maybeLocation.getOrElse(throw new RuntimeException(s"Location not found for component: name:[$name] type:[${cType.name}]"))
  }

  private def createCommandService: AkkaLocation ⇒ CommandService = CommandServiceFactory.make


  private val source:Prefix = Prefix("web-server")
  private val maybeObsId = None


  println("ABOUT TO GET HCD COMMAND")
  private val hcdCommand = hcdCommandService("JSidetectorHcd")



  //LoggingSystemFactory.start("GalilHcdClientApp", "0.1", host, system)

  // Keys

  val displayNameKey: Key[String] = KeyType.StringKey.make("displayName")
  val valStrKey: Key[String] = KeyType.StringKey.make("valStr")
  val postNameKey: Key[String] = KeyType.StringKey.make("postName")
  val argStrKey: Key[String] = KeyType.StringKey.make("argStr")

  // HCD Commands

  def getStatusMetaData(): Action[AnyContent] = GsAction.async { implicit request =>
    implicit val ec: ExecutionContext = actorSystem.executionContext
    getStatusMetaDataFromHcd().map { response => {

      response match {
        case resp: CommandResponse.CompletedWithResult => {

          Ok(Json.toJson(resp.result));
        }
        case _ =>
          Ok(Json.toJson(response.toString))
      }

    }

    }
  }

  def getParameterMetaData(): Action[AnyContent] = GsAction.async { implicit request =>
    implicit val ec: ExecutionContext = actorSystem.executionContext
    getParameterMetaDataFromHcd().map { response => {

      response match {
        case resp: CommandResponse.CompletedWithResult => {

          Ok(Json.toJson(resp.result));
        }
        case _ =>
          Ok(Json.toJson(response.toString))
      }

    }

    }
  }

  def getCommandMetaData(): Action[AnyContent] = GsAction.async { implicit request =>
    implicit val ec: ExecutionContext = actorSystem.executionContext
    getCommandMetaDataFromHcd().map { response => {

      response match {
        case resp: CommandResponse.CompletedWithResult => {

          Ok(Json.toJson(resp.result));
        }
        case _ =>
          Ok(Json.toJson(response.toString))
      }

    }

    }
  }

  def setParameterValue(obsId: Option[ObsId],  displayName: String, valStr: String): Action[AnyContent] = GsAction.async { implicit request =>
    implicit val ec: ExecutionContext = actorSystem.executionContext
    setParameterValueFromHcd(displayName, valStr).map { response => {

      response match {
        case resp: CommandResponse.CompletedWithResult => {

          Ok(Json.toJson(resp.result));
        }
        case _ =>
          Ok(Json.toJson(response.toString))
      }

    }
    }
  }

  def sendParameters(obsId: Option[ObsId]): Action[AnyContent] = GsAction.async { implicit request =>
    implicit val ec: ExecutionContext = actorSystem.executionContext
    sendParametersFromHcd().map { response => {

      response match {
        case resp: CommandResponse.CompletedWithResult => {

          Ok(Json.toJson(resp.result));
        }
        case _ =>
          Ok(Json.toJson(response.toString))
      }

    }
    }
  }

  def issueCommand(obsId: Option[ObsId], postName: String, argStr: String): Action[AnyContent] = GsAction.async { implicit request =>
    implicit val ec: ExecutionContext = actorSystem.executionContext
    issueCommandFromHcd(postName, argStr).map { response => {

      response match {
        case resp: CommandResponse.CompletedWithResult => {

          Ok(Json.toJson(resp.result));
        }
        case _ =>
          Ok(Json.toJson(response.toString))
      }

    }
    }
  }



  def getStatusMetaDataFromHcd(): Future[CommandResponse] = {

    import scala.concurrent.duration._
    implicit val timeout: Timeout = Timeout(3.seconds)
    val setup = Setup(source, CommandName("getStatusMetaData"), maybeObsId)

    hcdCommand.submit(setup)
  }

  def getParameterMetaDataFromHcd(): Future[CommandResponse] = {

    import scala.concurrent.duration._
    implicit val timeout: Timeout = Timeout(3.seconds)
    val setup = Setup(source, CommandName("getParameterMetaData"), maybeObsId)

    hcdCommand.submit(setup)
  }

  def getCommandMetaDataFromHcd(): Future[CommandResponse] = {

    import scala.concurrent.duration._
    implicit val timeout: Timeout = Timeout(3.seconds)
    val setup = Setup(source, CommandName("getCommandMetaData"), maybeObsId)

    hcdCommand.submit(setup)
  }

  def setParameterValueFromHcd(displayName: String, valStr: String): Future[CommandResponse] = {

    import scala.concurrent.duration._
    implicit val timeout: Timeout = Timeout(3.seconds)

    val displayNameParam: Parameter[String] = displayNameKey.set(displayName)
    val valStrParam: Parameter[String] = valStrKey.set(valStr)


    val setup = Setup(source, CommandName("setParameterValue"), maybeObsId).add(displayNameParam).add(valStrParam)

    hcdCommand.submit(setup)
  }

  def sendParametersFromHcd(): Future[CommandResponse] = {

    import scala.concurrent.duration._
    implicit val timeout: Timeout = Timeout(3.seconds)



    val setup = Setup(source, CommandName("sendParameters"), maybeObsId)

    hcdCommand.submit(setup)
  }

  def issueCommandFromHcd(postName: String, argStr: String): Future[CommandResponse] = {

    import scala.concurrent.duration._
    implicit val timeout: Timeout = Timeout(3.seconds)


    val postNameParam: Parameter[String] = postNameKey.set(postName)
    val argStrParam: Parameter[String] = argStrKey.set(argStr)


    val setup = Setup(source, CommandName("issueCommand"), maybeObsId).add(postNameParam).add(argStrParam)

    hcdCommand.submit(setup)
  }

}
