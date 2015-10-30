package utils

/**
 * Created by ahmetkucuk on 03/10/15.
 */
object Constants {

  object UserQueries {
    val INSERT: String = "INSERT INTO USER(email, name, phone, password, role) VALUES('%s', '%s', '%s', '%s', '%s')"
    val ALL: String = "SELECT * FROM USER"
    val SELECT_BY_ID: String = "SELECT * FROM USER WHERE id = %d"
    val SELECT_BY_EMAIL: String = "SELECT * FROM USER WHERE email = '%s'"
    val SELECT_BY_EMAIL_AND_PASSWORD: String = "SELECT * FROM USER WHERE email = '%s' AND password = '%s'"
  }

  object TextQueries {
    val INSERT: String = "INSERT INTO SECURE_TEXT(text, email) VALUES('%s', '%s')"
    val ALL: String = "SELECT * FROM SECURE_TEXT"
    val SELECT_BY_EMAIL: String = "SELECT * FROM SECURE_TEXT WHERE email = '%s"
  }

  object SessionQueries {
    val INSERT: String = "INSERT INTO SESSION(email, session_id, valid_until) VALUES('%s', '%s', %d)"
    val SELECT_BY_EMAIL_AND_SID: String = "SELECT * FROM SESSION WHERE email = '%s' AND session_id = '%s'"
    val SELECT_BY_EMAIL: String = "SELECT * FROM SESSION WHERE email = '%s'"
    val UPDATE_BY_EMAIL: String = "UPDATE SESSION SET valid_until = %d, session_id = '%s' WHERE email = '%s'"
    val DELETE_BY_EMAIL: String = "DELETE FROM SESSION WHERE email='%s'"
  }






  val ERROR: String = "ERROR"
  val SUCCESS: String = "SUCCESS"
  val SESSION_PERIOD_IN_MILLIS: Long = 900000
  val EMAIL: String = "email"
  val SESSION_ID: String = "sessionId"
  val ROLE: String = "role"
  val BOOK_ID: String = "bookId"
  val ID: String = "id"

  val STATUS: String = "status"
  val STATUS_CODE: String = "status_code"
  val MSG: String = "msg"
  val RESULT: String = "result"
  val DATA: String = "data"

  val SUCCESS_CODE: Int = 1
  val DATABASE_ERROR_CODE: Int = 2
  val INTERNAL_SERVER_ERROR_CODE: Int = 3
  val SESSION_ERROR_CODE: Int = 4
  val GENERAL_ERROR_CODE: Int = 5

  val ROLE_ADMIN: String = "ADMIN"
  val ROLE_USER: String = "USER"


  val PATH_ADMIN: String = "/admin"


}
