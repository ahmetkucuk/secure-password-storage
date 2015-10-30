package third.webcore.models

import java.sql.ResultSet

import dao.SqlMappable
import play.api.libs.json._

/**
 * Created by ahmetkucuk on 04/08/14.
 */

case class User(email: String, name: String, phone: String, password: String, role: String) {

  def this(registerRequest: RegisterRequest, passwordHash: String, role: String) = this(registerRequest.email, registerRequest.name, registerRequest.phone, passwordHash, role)

  def toJson(): JsValue = {
    JsObject(Seq("email" -> Json.toJson(email),
      "name" -> Json.toJson(name),
      "phone" -> Json.toJson(phone)
    ))
  }
}

object User {
  implicit val map = new SqlMappable[User] {
    def map(r: ResultSet) = new User(r.getString("email"), r.getString("name"), r.getString("phone"), r.getString("password"), r.getString("role"))
  }
}

