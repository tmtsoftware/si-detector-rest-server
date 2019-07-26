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

    // set a parameter value
    case POST(p"/setParameterValue" ? q_o"obsId=$maybeObsId" & q"displayName=$displayName" & q"valStr=$valStr") =>
      controller.setParameterValue(maybeObsId.map(ObsId(_)), displayName, valStr)

    // send parameters
    case POST(p"/sendParameters" ? q_o"obsId=$maybeObsId" ) =>
      controller.sendParameters(maybeObsId.map(ObsId(_)))

    // issue command
    case POST(p"/issueCommand" ? q_o"obsId=$maybeObsId" & q"postName=$postName" & q"argStr=$argStr") =>
      controller.issueCommand(maybeObsId.map(ObsId(_)), postName, argStr)


  }
}

object GsRouter {

}


/*






  case "getImageSize":

  cameraHandleParam = controlCommand.paramSet().find(x -> x.keyName().equals("cameraHandle")).get();

  cameraHandle = (Integer)cameraHandleParam.jGet(0).get();

  try {

  ImageSizeInfo imageSizeInfo = SpectralInstrumentsApi.getImageSize(cameraHandle);
  // create a result using imageSizeInfo

  Key<Integer> serLenKey = JKeyType.IntKey().make("serLen");
  Key<Integer> parLenKey = JKeyType.IntKey().make("parLen");
  Key<Integer> is16Key = JKeyType.IntKey().make("is16");
  Key<Integer> nSerCCDKey = JKeyType.IntKey().make("nSerCCD");
  Key<Integer> nParCCDKey = JKeyType.IntKey().make("nParCCD");
  Key<Integer> nSerSectKey = JKeyType.IntKey().make("nSerSect");
  Key<Integer> nParSectKey = JKeyType.IntKey().make("nParSect");


  Result r4 = new Result(prefix)
  .add(serLenKey.set(imageSizeInfo.getSerLen()))
  .add(parLenKey.set(imageSizeInfo.getParLen()))
  .add(is16Key.set(imageSizeInfo.getIs16()))
  .add(nSerCCDKey.set(imageSizeInfo.getnSerCCD()))
  .add(nParCCDKey.set(imageSizeInfo.getnParCCD()))
  .add(nSerSectKey.set(imageSizeInfo.getnSerSect()))
  .add(nParSectKey.set(imageSizeInfo.getnParSect()));

  return new CommandResponse.CompletedWithResult(controlCommand.runId(), r4);

} catch (Exception e) {
  return new CommandResponse.Error(controlCommand.runId(), e.getMessage());
}


  case "prepareAcquisition":

  cameraHandleParam = controlCommand.paramSet().find(x -> x.keyName().equals("cameraHandle")).get();
  cameraHandle = (Integer)cameraHandleParam.jGet(0).get();

  Parameter serLenParam = controlCommand.paramSet().find(x -> x.keyName().equals("serLen")).get();
  int serLen = (Integer)serLenParam.jGet(0).get();

  Parameter parLenParam = controlCommand.paramSet().find(x -> x.keyName().equals("parLen")).get();
  int parLen = (Integer)parLenParam.jGet(0).get();

  Parameter is16Param = controlCommand.paramSet().find(x -> x.keyName().equals("is16")).get();
  int is16 = (Integer)serLenParam.jGet(0).get();


  try {
  SpectralInstrumentsApi.prepareAcquisition(cameraHandle, serLen, parLen, is16);
  return new CommandResponse.Completed(controlCommand.runId());
} catch (Exception e) {
  return new CommandResponse.Error(controlCommand.runId(), e.getMessage());
}


  case "issueCommand":

  cameraHandleParam = controlCommand.paramSet().find(x -> x.keyName().equals("cameraHandle")).get();
  cameraHandle = (Integer)cameraHandleParam.jGet(0).get();

  Parameter postNameParam = controlCommand.paramSet().find(x -> x.keyName().equals("postName")).get();
  String postName = (String)postNameParam.jGet(0).get();

  Parameter argStrParam = controlCommand.paramSet().find(x -> x.keyName().equals("argStr")).get();
  String argStr = (String)argStrParam.jGet(0).get();


  try {
  String commandResponse = SpectralInstrumentsApi.issueCommand(cameraHandle, postName, argStr);

  Key<String> issueCommandResponseKey = JKeyType.StringKey().make("issueCommandResponse");
  Result r4 = new Result(prefix).add(issueCommandResponseKey.set(commandResponse));

  return new CommandResponse.CompletedWithResult(controlCommand.runId(), r4);


} catch (Exception e) {
  return new CommandResponse.Error(controlCommand.runId(), e.getMessage());
}


  case "getAcqStatus":

  cameraHandleParam = controlCommand.paramSet().find(x -> x.keyName().equals("cameraHandle")).get();
  cameraHandle = (Integer)cameraHandleParam.jGet(0).get();


  try {
  AcqStatusInfo acqStatusInfo = SpectralInstrumentsApi.getAcqStatus(cameraHandle);

  Key<Integer> percentReadKey = JKeyType.IntKey().make("percentRead");
  Key<Integer> currentFrameKey = JKeyType.IntKey().make("currentFrame");
  Key<Integer> bufferFlagsKey = JKeyType.IntKey().make("bufferFlags");

  Result r4 = new Result(prefix)
  .add(percentReadKey.set(acqStatusInfo.getPercentRead()))
  .add(currentFrameKey.set(acqStatusInfo.getCurrentFrame()))
  .add(bufferFlagsKey.set(acqStatusInfo.getBufferFlags()));

  return new CommandResponse.CompletedWithResult(controlCommand.runId(), r4);


} catch (Exception e) {
  return new CommandResponse.Error(controlCommand.runId(), e.getMessage());
}


  case "endAcq":

  cameraHandleParam = controlCommand.paramSet().find(x -> x.keyName().equals("cameraHandle")).get();
  cameraHandle = (Integer)cameraHandleParam.jGet(0).get();

  Parameter forceAbortParam = controlCommand.paramSet().find(x -> x.keyName().equals("forceAbort")).get();
  boolean forceAbort = (Boolean)forceAbortParam.jGet(0).get();

  serLenParam = controlCommand.paramSet().find(x -> x.keyName().equals("serLen")).get();
  serLen = (Integer)serLenParam.jGet(0).get();

  parLenParam = controlCommand.paramSet().find(x -> x.keyName().equals("parLen")).get();
  parLen = (Integer)parLenParam.jGet(0).get();

  // the image buffer and arrayLen are not passed in, as these are handled differently, using the VBDS
  int arrayLen = serLen * parLen;
  int[] imageBuffer = new int[arrayLen];


  try {
  SpectralInstrumentsApi.endAcq(cameraHandle, forceAbort, imageBuffer, arrayLen);

  // TODO: do something using VBDS


  return new CommandResponse.Completed(controlCommand.runId());
} catch (Exception e) {
  return new CommandResponse.Error(controlCommand.runId(), e.getMessage());
}
*/
