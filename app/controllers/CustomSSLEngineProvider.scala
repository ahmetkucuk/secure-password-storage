package controllers


import javax.net.ssl._
import play.core.ApplicationProvider
import play.server.api._
/**
  * Created by ahmetkucuk on 01/12/15.
  */
class CustomSSLEngineProvider(appProvider: ApplicationProvider) extends SSLEngineProvider {

  override def createSSLEngine(): SSLEngine = {
    // change it to your custom implementation
    SSLContext.getDefault.createSSLEngine
  }

}
