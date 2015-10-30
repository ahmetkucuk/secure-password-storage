package third.webcore.models

import java.sql.ResultSet

import dao.SqlMappable


/**
 * Created by ahmetkucuk on 8/6/14.
 */
case class SessionStatus(val email: String, val sessionId: String, val validUntil: Long) {

  def isValid = System.currentTimeMillis() <= validUntil

}

object SessionStatus {
  implicit val map = new SqlMappable[SessionStatus] {
    def map(r: ResultSet) = new SessionStatus(r.getString("email"), r.getString("session_id"), r.getLong("valid_until"))
  }
}

