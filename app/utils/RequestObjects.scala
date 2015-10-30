package utils

import play.api.libs.json.Json

/**
 * Created by ahmetkucuk on 30/10/15.
 */


object AddSecureTextRequest {

  implicit val request = Json.format[AddSecureTextRequest]
}

case class AddSecureTextRequest(text: String)
