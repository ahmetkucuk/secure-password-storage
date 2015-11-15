package dao

import java.sql.{Statement, ResultSet}
import play.api.Logger
import play.api.db.DB
import play.api.Play.current
import third.webcore.models.{SessionStatus, User}
import utils.Constants

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

/**
 * Created by ahmetkucuk on 03/10/15.
 */

object SQL {
  val ds = DB.getConnection()

  def initiateDB(): Unit = {

    val stmt = ds.createStatement()
    stmt.execute("DROP TABLE IF EXISTS USER;CREATE TABLE USER(email VARCHAR, name VARCHAR, phone VARCHAR, password VARCHAR, role VARCHAR, PRIMARY KEY(email))")
    stmt.execute("DROP TABLE IF EXISTS TEXT;CREATE TABLE TEXT(pass_id INT NOT NULL AUTO_INCREMENT, text VARCHAR, email VARCHAR, FOREIGN KEY (email) REFERENCES USER(email))")
    stmt.execute("DROP TABLE IF EXISTS SESSION;CREATE TABLE SESSION(email VARCHAR, session_id VARCHAR, valid_until LONG)")

    stmt.execute("INSERT INTO USER(email, name, password) VALUES('ahmetkucuk92@gmail.com', 'Ahmet', '2d42f7c204976b136cb6ac5d505e9b24f5671f2e')")
    stmt.execute("INSERT INTO USER(email, name) VALUES('one@gmail.com', 'One')")
    stmt.execute("INSERT INTO USER(email, name) VALUES('two@gmail.com', 'Two')")
    stmt.execute("INSERT INTO USER(email, name) VALUES('three@gmail.com', 'Three')")
    stmt.execute("INSERT INTO USER(email, name) VALUES('four@gmail.com', 'Four')")


    stmt.execute("INSERT INTO TEXT(text, email) VALUES('encrypted 1', 'ahmetkucuk92@gmail.com')")
    stmt.execute("INSERT INTO TEXT(text, email) VALUES('encrypted 2', 'ahmetkucuk92@gmail.com')")
    stmt.execute("INSERT INTO TEXT(text, email) VALUES('encrypted 3', 'ahmetkucuk92@gmail.com')")
  }

}

trait SQLOps {

  def executeCommand(connection: java.sql.Connection, command: String): Boolean = {
    def func(): Unit = {
      val stmt = connection.createStatement()
      stmt.execute(command)
      stmt.close()
    }
    exceptionCatcher(func)._1
  }

//  def executeQuery[T](connection: java.sql.Connection, query: String): Option[T] = {
//    var stmt: Statement = null
//    try {
//      stmt = connection.createStatement()
//      val resultSet = stmt.executeQuery(query)
//      if(resultSet.next()) {
//        val session = implicitly[SqlMappable[T]].map(resultSet)
//        Some(session)
//      } else {
//        None
//      }
//    } catch {
//      case e:Exception =>
//        e.printStackTrace()
//        None
//    } finally {
//      if(stmt != null)
//        stmt.close()
//    }
//  }

  def executeQueryForList[T : SqlMappable](connection: java.sql.Connection, query: String): List[T] = {
    var stmt: Statement = null
    val result: ListBuffer[T] = new ListBuffer[T]
    try {
      stmt = connection.createStatement()

      val resultSet = stmt.executeQuery(query)

      while(resultSet.next()) {
        var mappedObject = implicitly[SqlMappable[T]].map(resultSet)
        if(mappedObject != null)
          result += mappedObject
      }
      result.toList
    } catch {
      case e:Exception =>
        e.printStackTrace()
        result.toList
    } finally {
      if(stmt != null)
        stmt.close()
    }
  }

  def mapQueryResultToSession(r: ResultSet): SessionStatus = {
    new SessionStatus(r.getString("email"), r.getString("session_id"), r.getLong("valid_until"))

  }

  def exceptionCatcher(func: () => Any): (Boolean, Any) = {
    try {
      (true, func())
    } catch {
      case e: Exception =>
        e.printStackTrace()
        (false, None)
    }
  }

}

