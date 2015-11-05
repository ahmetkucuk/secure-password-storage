package services

import dao.DaoComponent
import models.SecureText
import play.api.Logger
import third.webcore.dao.WebCoreDaoComponent
import third.webcore.services.{UserServiceComponentImpl, UserServiceComponent}
import utils.AddSecureTextRequest
import scala.concurrent.{Await, Future}
import third.webcore.models.User
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
 * Created by ahmetkucuk on 03/10/15.
 */
trait ApiServiceComponent {

  val apiService: ApiService

  trait ApiService {
    def addSecureText(addSecureTextRequest: AddSecureTextRequest, email: String): Future[Boolean]
    def getSecureTexts(email: String): Future[List[SecureText]]
    def getEncryptedTexts(email: String): Future[List[SecureText]]
  }

}

trait ApiServiceComponentImpl extends ApiServiceComponent {

  this: DaoComponent with WebCoreDaoComponent =>

  val apiService = new ApiServiceImpl

  class ApiServiceImpl extends ApiService {

    override def addSecureText(addTextRequest:AddSecureTextRequest, email: String): Future[Boolean] = {
      apiDao.addNewText(addTextRequest.text, email)
    }
    override def getSecureTexts(email: String): Future[List[SecureText]] = Future {
      apiDao.listTextOf(email)
    }
    override def getEncryptedTexts(email: String): Future[List[SecureText]] = Future {
      val secret = "0123456789012345";

      val list = apiDao.listTextOf(email)
      list.foreach(st => {
        Logger.debug(st.text)
        })
      list
    }
  }

}
