package third.webcore.controllers

import play.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import play.i18n.Messages
import third.webcore.dao.WebCoreDaoComponentImpl
import third.webcore.models._
import third.webcore.services._
import utils.WebCoreConstants

import scala.concurrent.Future

/**
 * Created by ahmetkucuk on 8/6/14.
 */
class UserController extends Controller
with UserServiceComponentImpl
with WebCoreDaoComponentImpl
with SessionServiceComponentImpl with AuthenticationHelper {

  def get(email: String) = Authenticated {

    val result = userService.getUserByEmail(email)
    result.map {
      case None =>
        Ok(ResponseBase.error(Messages.get("user_not_found")).toResultJson)
      case Some(user) =>
        Ok(ResponseUser(ResponseBase.success(), user).toJson)
    }
  }

  def getUser = Authenticated(parse.json) { (request, email, _) =>

      userService.getUserByEmail(email).map {
        case None =>
          Ok(ResponseBase.error(Messages.get("user_not_found")).toResultJson)
        case Some(user) =>
          Ok(ResponseUser(ResponseBase.success(), user).toJson)
      }
  }

  def index = Authenticated {
      userService.getUsers().map{users =>
        Ok(ResponseListUser(ResponseBase.success(), users).toJson)
      }
  }

  def login() = Action.async(parse.json) { request =>

      request.body.validate[LoginRequest].fold (
        valid = { loginRequest =>
          userService.login(loginRequest.email, loginRequest.password).map{
            case Some((sessionId, user)) =>
              Logger.info("Login Success With email: " + loginRequest.email + " password: " + loginRequest.password)
//              if(user.role.equalsIgnoreCase(WebCoreConstants.ROLE_ADMIN)) {
//                Ok(ResponseUser(ResponseBase.success(), user).toJson).withSession(WebCoreConstants.SESSION_ID -> sessionId, WebCoreConstants.EMAIL -> user.email, WebCoreConstants.ROLE -> WebCoreConstants.ROLE_ADMIN)
//              } else {
                Ok(ResponseUser(ResponseBase.success(), user).toJson).withSession(WebCoreConstants.SESSION_ID -> sessionId, WebCoreConstants.EMAIL -> user.email, WebCoreConstants.ROLE -> WebCoreConstants.ROLE_USER)
//              }
            case None =>
              Logger.info("Login Fail with email: " + loginRequest.email + " password: " + loginRequest.password)
              Ok(ResponseBase.error().toResultJson)
          }
        },
        invalid = { e =>
          Logger.error(s"[UserController-login] error: $e")
          Future.successful(Ok(ResponseBase.error().toResultJson))
        }
      )
  }

  def register() = Action.async(parse.json) { request =>

      request.body.validate[RegisterRequest].fold (
        valid = { registerRequest =>
          userService.register(registerRequest).map{ internalResponse =>
            Ok(ResponseBase.response(internalResponse))
          }
        },
        invalid = {e => Logger.error(s"[UserController-register] $e");
          Future.successful(Ok(ResponseBase.error().toResultJson))
        }
      )
  }

  def changeUserRole() = Authenticated(parse.json) { (request, _, _) =>

      request.body.validate[ChangeUserRoleRequest].fold(
        valid = {changeUserRoleRequest =>
          userService.changeUserRole(changeUserRoleRequest).map { result =>
            Ok(ResponseBase.response(result))
          }
        },
        invalid = {e => Logger.error(s"[changeUserRole] error: $e")
          Future.successful(Ok(ResponseBase.error(Messages.get("json_field_error")).toResultJson))
        }
      )
  }

  def logout() = Authenticated { (request, email, _) =>
    Future.successful(Ok(ResponseBase.success().toJson).withNewSession)
  }

  def changePassword() = Authenticated(parse.json) { (request, _, _) =>

      request.body.validate[ChangePasswordRequest].fold(
        valid = {changePasswordRequest =>
          val email: String = request.session.get("email").get
          userService.changeUserPassword(email, changePasswordRequest).map(internalResponse =>
            Ok(internalResponse.getResponse())
          )
        },
        invalid = {e => Logger.debug(s"[ChangePassword] error: $e")
          Future.successful(Ok(ResponseBase.error(Messages.get("json_field_error")).toResultJson))
        }
      )
  }

  def forgotPassword = Action.async(parse.json) { request =>

      request.body.\("email").asOpt[String] match {
        case Some(email) =>
          userService.forgotPassword(email).map( internalResponse => Ok(internalResponse.getResponse()))
        case _ =>
          Future.successful(Ok(ResponseBase.error(Messages.get("json_field_error")).toResultJson))
      }
  }

  def renewPassword = Action.async(parse.json) { request =>

      request.body.validate[RenewPasswordRequest].fold(

        valid = {renewPasswordRequest =>
          userService.renewForgotPassword(renewPasswordRequest).map(internalResponse => Ok(internalResponse.getResponse()))
        },
        invalid = {e => Logger.error(s"[UserController - RenewPassword] $e")
          Future.successful(Ok(ResponseBase.error(Messages.get("json_field_error")).toResultJson))
        }
      )
  }

  def deleteUser(email: String) = Authenticated { (request, _, _) =>
      if(request.session.get("role").get.equalsIgnoreCase(WebCoreConstants.ROLE_ADMIN)) {
        userService.deleteUser(email).map(result =>
          Ok(ResponseBase.response(result))
        )
      } else {
        Future.successful(Ok(ResponseBase.error(Messages.get("remove_user_permission_denied")).toResultJson))
      }
  }
}