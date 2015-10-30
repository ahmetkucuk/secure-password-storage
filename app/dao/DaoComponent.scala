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
    def listTextOf(email: String): Future[List[SecureText]]
  }

}

trait DaoComponentImpl extends DaoComponent {

  val apiDao = new ApiDaoImpl

  class ApiDaoImpl extends ApiDao with SQLOps {

    val ds = SQL.ds


    def addNewText(text: String, email: String): Future[Boolean] = Future {
      val query = String.format(Constants.TextQueries.INSERT, text, email)
      executeCommand(ds, query)
    }

    def listTextOf(email: String): Future[List[SecureText]] = Future {
      val query = String.format(Constants.TextQueries.SELECT_BY_EMAIL, email)
      executeQueryForList[SecureText](ds, query)
    }

  }

}
