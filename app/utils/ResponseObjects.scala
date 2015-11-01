package utils

import models.SecureText
import play.api.libs.json.{JsArray, JsObject}
import third.webcore.models._

/**
 * Created by ahmetkucuk on 01/11/15.
 */
case class ResponseListText(result: ResponseBase, data:List[SecureText]) {
  def toJson: JsObject = {
    JsObject(Seq(WebCoreConstants.RESULT -> result.toJson, WebCoreConstants.DATA -> JsObject(Seq("textList" -> JsArray(data.map( text => text.toJson))))))
  }
}
object ResponseListText
