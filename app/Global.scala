import dao.{SQL}
import play.api._
import play.api.db.DB
import play.api.Play.current

/**
 * Created by ahmetkucuk on 28/09/15.
 */
object Global extends GlobalSettings{

  override def onStart(app: Application) {
    Logger.info("Application has started")
    SQL.initiateDB()
  }

}
