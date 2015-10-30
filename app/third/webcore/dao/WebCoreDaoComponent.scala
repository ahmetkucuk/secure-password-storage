package third.webcore.dao

import dao.{SQLOps, SQL}
import play.api.Logger
import play.api.Logger
import third.webcore.models.{SessionStatus, User}
import utils.Constants

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 * Created by ahmetkucuk on 07/03/15.
 */

trait WebCoreDaoComponent {

  val userDao: UserDao
  val sessionDao: SessionDao

  trait UserDao {

    def getUserByEmail(email: String): Future[Option[User]]
    def getUsers(): Future[List[User]]
    def getUserByEmailAndPasswordHash(username: String, passwordHash: String): Future[Option[User]]
    def register(user: User): Future[Boolean]
    def updateUserByEmail(email: String, user: User): Future[Boolean]
    def delete(email: String): Future[Boolean]
  }

  trait SessionDao {
    def createNewSession(session:SessionStatus): Future[Boolean]
    def getSession(email:String, sessionId: String): Future[Option[SessionStatus]]
    def getSessionByEmail(email:String): Future[Option[SessionStatus]]
    def updateSessionByEmail(email:String, session:SessionStatus): Future[Boolean]
    def removeSessionByEmail(email:String): Future[Boolean]
  }
}

trait WebCoreDaoComponentImpl extends WebCoreDaoComponent with SQLOps {

  val userDao: UserDao = new UserDaoImpl
  val sessionDao: SessionDao = new SessionDaoImpl

  class UserDaoImpl extends UserDao {

    val ds = SQL.ds

    def updateUserByEmail(email: String, user: User) = {
//
//      Logger.debug(s"[DaoComponent-updateUserByEmail]: Executing with email: $email")
//      val future = userCollection.update(BSONDocument("email" -> email), user)
//      future.map(lastError => !lastError.inError)
      Future.successful(true)
    }


    override def getUsers(): Future[List[User]] = Future {
      executeQueryForList[User](ds, Constants.UserQueries.ALL)
    }


    def register(user: User): Future[Boolean] = Future {
      val command: String = String.format(Constants.UserQueries.INSERT, user.email, user.name, user.phone, user.password, user.role)
      executeCommand(ds, command)
    }

    def getUserByEmail(email: String) = Future {
      val queryString = String.format(Constants.UserQueries.SELECT_BY_EMAIL, email)
      executeQueryForList[User](ds, queryString).headOption
    }

    override def getUserByEmailAndPasswordHash(email:String, passwordHash: String) = Future {
      val queryString = String.format(Constants.UserQueries.SELECT_BY_EMAIL_AND_PASSWORD, email, passwordHash)
      executeQueryForList[User](ds, queryString).headOption
    }

    def delete(email: String): Future[Boolean] = {
      Future.successful(false)
    }
  }

  class SessionDaoImpl extends SessionDao with SQLOps {

    val ds = SQL.ds

    def createNewSession(session: SessionStatus): Future[Boolean] = Future {
      val createSession = String.format(Constants.SessionQueries.INSERT, session.email, session.sessionId, session.validUntil: java.lang.Long)
      executeCommand(ds, createSession)
    }

    def getSession(email:String, sessionId: String): Future[Option[SessionStatus]] = Future {
      val queryString = String.format(Constants.SessionQueries.SELECT_BY_EMAIL_AND_SID, email, sessionId)
      executeQueryForList[SessionStatus](ds, queryString).headOption
    }

    def getSessionByEmail(email:String): Future[Option[SessionStatus]] = Future {
      val queryString = String.format(Constants.SessionQueries.SELECT_BY_EMAIL, email)
      executeQueryForList[SessionStatus](ds, queryString).headOption
    }

    def updateSessionByEmail(email:String, session:SessionStatus): Future[Boolean] = Future {
      val queryString = String.format(Constants.SessionQueries.UPDATE_BY_EMAIL, session.validUntil: java.lang.Long, session.sessionId, email)
      executeCommand(ds, queryString)
    }

    def removeSessionByEmail(email:String): Future[Boolean] = Future {
      val queryString = String.format(Constants.SessionQueries.DELETE_BY_EMAIL, email)
      executeCommand(ds, queryString)
    }

  }
}
