package third.webcore.models

import play.api.Logger
import play.api.libs.json.{Json, JsArray, JsObject, JsValue}
import utils.WebCoreConstants
import views.html.defaultpages.error_Scope0.error

/**
 * Created by ahmetkucuk on 07/03/15.
 */
class WebCoreResponseObjects {

}


case class ResponseListUser(result: ResponseBase, data:List[User]) {
  def toJson: JsObject = {
    result.toJson.as[JsObject] ++ JsObject(Seq(WebCoreConstants.DATA -> JsObject(Seq("userList" -> JsArray(data.map( user => user.toJson))))))
  }
}
object ResponseListUser


case class ResponseUser(result: ResponseBase, data:User) {
  def toJson: JsObject = {
    result.toJson.as[JsObject] ++ JsObject(Seq(WebCoreConstants.DATA -> data.toJson))
  }
}
object ResponseUser


case class InternalResponse(status: Boolean, msg: String) {

  def getResponse(): JsValue = {
    if(status) {
      ResponseBase.success(msg).toResultJson
    } else {
      ResponseBase.error(msg).toResultJson
    }
  }
}

case class ResponseBase(status: String, statusCode: Int, msg: String) {

  def toJson: JsValue = {
    JsObject(Seq(WebCoreConstants.STATUS -> Json.toJson(status), WebCoreConstants.STATUS_CODE -> Json.toJson(statusCode), WebCoreConstants.MSG -> Json.toJson(msg)))
  }

  def toResultJson: JsValue = {
    JsObject(Seq(WebCoreConstants.RESULT -> (JsObject(Seq(WebCoreConstants.STATUS -> Json.toJson(status), WebCoreConstants.STATUS_CODE-> Json.toJson(statusCode), WebCoreConstants.MSG -> Json.toJson(msg))))))
  }
}

object ResponseBase {

  implicit val responseBaseFormat = Json.format[ResponseBase]

  def success(): ResponseBase = {
    ResponseBase(WebCoreConstants.SUCCESS, WebCoreConstants.SUCCESS_CODE, "success")
  }

  def success(msg: String): ResponseBase = {
    ResponseBase(WebCoreConstants.SUCCESS, WebCoreConstants.SUCCESS_CODE, msg)
  }


  def error(message: String): ResponseBase = {
    ResponseBase(WebCoreConstants.ERROR, WebCoreConstants.GENERAL_ERROR_CODE, message)
  }


  def error(): ResponseBase = {
    ResponseBase(WebCoreConstants.ERROR, WebCoreConstants.GENERAL_ERROR_CODE, "error")
  }

  def sessionError(): ResponseBase = {
    ResponseBase(WebCoreConstants.ERROR, WebCoreConstants.SESSION_ERROR_CODE, "Session Error")
  }

  def response(result:Boolean): JsValue = {
    if(result) {
      ResponseBase.success().toResultJson
    } else {
      ResponseBase.error().toResultJson
    }
  }

  def response(internalResponse: InternalResponse): JsValue = {
    if(internalResponse.status) {
      ResponseBase.success(internalResponse.msg).toJson
    } else {
      ResponseBase.error(internalResponse.msg).toJson
    }
  }

};


object InternalResponse {

  def response(isError: Boolean, error: String): InternalResponse = {
    if(isError) {
      Logger.error("[Internal Response Error: " + error)
      InternalResponse(false, error)
    } else {
      InternalResponse(true, error)
    }
  }

  def response(result: Boolean): InternalResponse = {
    if(result) {
      InternalResponse(true, "")
    } else {
      InternalResponse(false, "")
    }
  }

}
