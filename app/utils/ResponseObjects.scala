package utils

import models.SecureText
import play.api.libs.json.{Json, JsArray, JsObject}
import third.webcore.models._

/**
 * Created by ahmetkucuk on 01/11/15.
 */
case class ResponseListText(result: ResponseBase, data:List[SecureText]) {
  def toJson: JsObject = {
    result.toJson.as[JsObject] ++ JsObject(Seq(WebCoreConstants.DATA -> JsObject(Seq("textList" -> JsArray(data.map( text => text.toJson))))))
  }
}
object ResponseListText

case class ResponseListTextWithSecret(result: ResponseBase, data:List[SecureText], secret: String) {
  def toJson: JsObject = {
    JsObject(Seq("secret" -> Json.toJson(secret))) ++ result.toJson.as[JsObject] ++ JsObject(Seq(WebCoreConstants.DATA -> JsObject(Seq("textList" -> JsArray(data.map( text => text.toJson))))))
  }
}
object ResponseListTextWithSecret
