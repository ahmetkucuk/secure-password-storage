package controllers

import third.webcore.dao.WebCoreDaoComponentImpl
import third.webcore.models.{ResponseUser, ResponseListUser, ResponseBase}
import dao.{DaoComponentImpl, DaoComponent}
import play.api._
import play.api.db.DB
import play.api.Play.current
import play.api.libs.json.Json
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import services.{ApiServiceComponentImpl, ApiServiceComponent}
import third.webcore.services.{SessionServiceComponentImpl, AuthenticationHelper, UserServiceComponentImpl}
import utils.AddSecureTextRequest
import views.html
import org.apache.commons.codec.binary.Base64

import scala.concurrent.Future
import scala.util.{Failure, Success}

class APIController extends Controller with DaoComponentImpl with ApiServiceComponentImpl with WebCoreDaoComponentImpl with SessionServiceComponentImpl with AuthenticationHelper{

  def addSecureText = Authenticated(parse.json) { (request, email, _) =>

    Logger.debug("email: " + email)

    def encodeBase64(bytes: Array[Byte]) = Base64.encodeBase64String(bytes)
    def decodeBase64(text: String) = Base64.decodeBase64(text)

    val encrypted = encodeBase64(crypto.AES.encrypt("deneyelim", "0123456789012345"))
    Logger.debug("Encrypted: " + encrypted)
    val decrypted = crypto.AES.decrypt(decodeBase64(encrypted), "0123456789012345")
    Logger.debug("Decrypted: " + decrypted)

    apiService.addSecureText(new AddSecureTextRequest("asdf"), email).map(r => Ok(String.valueOf(r)))
  }

}

//object Application extends Application with DaoComponentImpl with UserServiceComponentImpl
