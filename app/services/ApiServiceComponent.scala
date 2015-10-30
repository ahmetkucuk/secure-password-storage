package services

import dao.DaoComponent
import third.webcore.dao.WebCoreDaoComponent
import third.webcore.services.{UserServiceComponentImpl, UserServiceComponent}
import utils.AddSecureTextRequest
import scala.concurrent.Future
import third.webcore.models.User

/**
 * Created by ahmetkucuk on 03/10/15.
 */
trait ApiServiceComponent {

  val apiService: ApiService

  trait ApiService {
    def addSecureText(addSecureTextRequest: AddSecureTextRequest, email: String): Future[Boolean]
  }

}

trait ApiServiceComponentImpl extends ApiServiceComponent {

  this: DaoComponent with WebCoreDaoComponent =>

  val apiService = new ApiServiceImpl

  class ApiServiceImpl extends ApiService {

    override def addSecureText(addTextRequest:AddSecureTextRequest, email: String): Future[Boolean] = {
      apiDao.addNewText(addTextRequest.text, email)
    }
  }

}
