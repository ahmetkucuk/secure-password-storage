package controllers

import play.Logger
import third.webcore.dao.WebCoreDaoComponentImpl
import third.webcore.models.{RegisterRequest, ResponseUser, ResponseListUser, ResponseBase}
import dao.{DaoComponentImpl, DaoComponent}
import play.api._
import play.api.db.DB
import play.api.Play.current
import play.api.libs.json.Json
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import services.{ApiServiceComponentImpl, ApiServiceComponent}
import third.webcore.services.{SessionServiceComponentImpl, AuthenticationHelper, UserServiceComponentImpl}
import utils.{ResponseListTextWithSecret, ResponseListText, AddSecureTextRequest}
import views.html
import org.apache.commons.codec.binary.Base64

import scala.concurrent.Future
import scala.util.{Failure, Success}

class APIController extends Controller with DaoComponentImpl with ApiServiceComponentImpl with WebCoreDaoComponentImpl with SessionServiceComponentImpl with AuthenticationHelper{

  def addSecureText = Authenticated(parse.json) { (request, email, _) =>

    request.body.validate[AddSecureTextRequest].fold (
      valid = { request =>
        apiService.addSecureText(request, email).map{ result =>
          if(result) {
            Ok(ResponseBase.success().toJson)
          } else {
            Ok(ResponseBase.error().toJson)
          }
        }
      },
      invalid = {e => Logger.error(s"[APIController-AddSecureText] $e");
        Future.successful(Ok(ResponseBase.error().toResultJson))
      }
    )

  }

  def getSecureTexts = Authenticated { (request, email, _) =>
    apiService.getSecureTexts(email).map(texts => Ok(ResponseListText(ResponseBase.success(), texts).toJson))
  }

  def sendSecret = Authenticated { (request, email, _) =>
    apiService.getEncryptedTexts(email).map(texts => Ok(ResponseListTextWithSecret(ResponseBase.success(), texts._2, texts._1).toJson))
  }

}

//object Application extends Application with DaoComponentImpl with UserServiceComponentImpl
