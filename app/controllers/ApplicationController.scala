package controllers

import dao.DaoComponentImpl
import play.api.libs.json.JsValue
import play.api.mvc.{Action, Controller}
import services.ApiServiceComponentImpl

/**
 * Created by ahmetkucuk on 17/10/15.
 */

class ApplicationController extends Controller{

  def home = Action {
    Ok(views.html.home())
  }

  def error = Action {
    Ok("Not able to show this page")
  }

}
