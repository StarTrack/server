package services

import scala.concurrent.Future
import org.joda.time.DateTime

import play.api.Logger
import play.api.libs.json._
import play.api.libs.ws.WS
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._

object OuiFmRadio extends Radio {
  val name = "ouifm"

  def currentTrack(): Future[Track] = {
    val now = DateTime.now()
    WS.url(onAir(now)).get().map { response =>
      extractCurrentTrack(response.json)
    }
  }

  def onAir(dt: DateTime): String = {
    s"https://www.ouifm.fr/onair.json?_=${dt.getMillis}"
  }

  /*
    {"rock":[
      {
        "artist":"THE ROLLING STONES",
        "title":"MISS YOU",
        "img":"https:\/\/www.ouifm.fr\/wp-content\/uploads\/artistes\/the-rolling-stones.jpg",
        "url":"http:\/\/www.ouifm.fr\/artistes\/the-rolling-stones",
        "ts":1417169488
      }
    ]}
  */
  private def extractCurrentTrack(json: JsValue): Track = {
    val current = (json \ "rock").apply(0)
    Logger.debug(s"parsing json from ouifm into track: $current")
    val title = (current \ "title").as[String]
    val artist = (current \ "title").asOpt[String]
    val endTime = (current \ "ts").as[Long]
    val startTime = DateTime.now().getMillis
    Track(startTime, endTime, title, artist)
  }

}
