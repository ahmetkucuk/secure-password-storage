package models

import java.sql.ResultSet

import dao.SqlMappable
import play.api.libs.json.{Json, JsObject, JsValue}
import third.webcore.models.{RegisterRequest, SessionStatus}
import utils.AddSecureTextRequest

/**
 * Created by ahmetkucuk on 30/10/15.
 */
case class SecureText(id: Int, text: String, email: String) {
  //pass_id INT NOT NULL AUTO_INCREMENT, text VARCHAR, u_email INT, FOREIGN KEY (u_email) REFERENCES USER(email))")

  def this(request: AddSecureTextRequest, email: String) = this(-1, request.text, email)

  def toJson(): JsValue = {
    JsObject(Seq("id" -> Json.toJson(id),
      "email" -> Json.toJson(email),
      "text" -> Json.toJson(email)
    ))
  }
}
object SecureText {
  implicit val map = new SqlMappable[SecureText] {
    def map(r: ResultSet) = new SecureText(r.getInt("pass_id"), r.getString("text"), r.getString("email"))
  }
}
