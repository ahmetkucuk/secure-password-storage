package services

import dao.DaoComponent
import models.SecureText
import play.api.Logger
import play.api.libs.ws.WS
import third.webcore.dao.WebCoreDaoComponent
import third.webcore.services.{UserServiceComponentImpl, UserServiceComponent}
import utils.{RandomKeyGenerator, AddSecureTextRequest}
import scala.concurrent.{Await, Future}
import play.api.Play.current
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
    def getEncryptedTexts(email: String): Future[(String, List[SecureText])]
  }

}

trait ApiServiceComponentImpl extends ApiServiceComponent with RandomKeyGenerator {

  this: DaoComponent with WebCoreDaoComponent =>

  val apiService = new ApiServiceImpl

  val textMagicUrl: String = "https://www.textmagic.com/app/api?username=****&password=*****t&cmd=send&text=%s&phone=1%s&unicode=0";
  val messageText: String = "%s use this key to get password. Secure Pass.";

  class ApiServiceImpl extends ApiService {

    override def addSecureText(addTextRequest:AddSecureTextRequest, email: String): Future[Boolean] = {
      apiDao.addNewText(addTextRequest.text, email)
    }
    override def getSecureTexts(email: String): Future[List[SecureText]] = Future {
      val secret = randomAlphanumericString(16);
      val list = apiDao.listTextOf(email);
      list.foreach(st => { st.encryptWith(secret) })
      list
    }

    override def getEncryptedTexts(email: String): Future[(String, List[SecureText])] = Future {
      val secret = randomAlphanumericString(16);
      val (secretPart1, secretPart2) = secret.splitAt(8);

      userDao.getUserByEmail(email).map({
          case Some(user) =>
            sendSecret(user.phone, secretPart2);
          case _ =>
            Logger.debug("Could not find user: " + email);
      })

      val list = apiDao.listTextOf(email)
      list.foreach(st => { st.encryptWith(secret) })
      (secretPart1, list)
    }

    def sendSecret(number:String, secret: String): Unit = {

      val url: String = String.format(textMagicUrl, String.format(messageText, secret), number)
      Logger.debug(url)
      WS.url(url).get().map { response => Logger.debug("SMS is sended to " + number + " secret was " + secret + " response: " + response.body);}
    }
  }

}
