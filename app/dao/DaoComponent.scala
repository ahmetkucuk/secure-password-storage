package dao

import java.sql.{Statement, ResultSet}

import models.SecureText
import play.Logger

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import third.webcore.models.User
import utils.Constants
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by ahmetkucuk on 03/10/15.
 */
trait DaoComponent {

  val apiDao: ApiDao

  trait ApiDao {

    def addNewText(text: String, email: String): Future[Boolean]
    def listTextOf(email: String): List[SecureText]
  }

}

trait DaoComponentImpl extends DaoComponent {

  val apiDao = new ApiDaoImpl

  class ApiDaoImpl extends ApiDao with SQLOps {

    val ds = SQL.ds

    def addNewText(text: String, email: String): Future[Boolean] = Future {
      val query = String.format(Constants.TextQueries.INSERT, crypto.AES.encrypt(text, Constants.APPLICATION_SECRET), email)
      executeCommand(ds, query)
    }

    def listTextOf(email: String): List[SecureText] = {
      val query = String.format(Constants.TextQueries.SELECT_BY_EMAIL, email)
      val list = executeQueryForList[SecureText](ds, query)
      list.foreach(st => st.decryptWith(Constants.APPLICATION_SECRET))
      list
    }

  }

}
