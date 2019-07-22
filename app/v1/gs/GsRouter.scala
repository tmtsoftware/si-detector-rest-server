package v1.gs

import javax.inject.Inject
import csw.params.core.models.ObsId
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

/**
  * Routes and URLs.
  */
class GsRouter @Inject()(controller: GsController) extends SimpleRouter {
  val prefix = "/v1/gs"

  override def routes: Routes = {

    // HCD commands

    case GET(p"/getStatusMetaData") =>
      controller.getStatusMetaData()

    case GET(p"/getParameterMetaData") =>
      controller.getParameterMetaData()

    case GET(p"/getCommandMetaData") =>
      controller.getCommandMetaData()



  }
}

object GsRouter {

}
