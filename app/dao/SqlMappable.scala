package dao

import java.sql.ResultSet

/**
 * Created by ahmetkucuk on 27/10/15.
 */
trait SqlMappable[T] {
  def map(rs: ResultSet): T
}
