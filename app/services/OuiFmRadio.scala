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
    WS.url(onAir()).get().map { response =>
      extractCurrentTrack(response.json)
    }
  }

  def onAir(dt: Option[DateTime] = None): String = {
    "http://www.ouifm.fr/onair.json" + dt.map(t => "?_=" + t.getMillis).getOrElse("")
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
    val artist = (current \ "artist").asOpt[String]
    val endTime = (current \ "ts").as[Long]
    val startTime = DateTime.now().getMillis
    Track(startTime, endTime, title, artist)
  }

}
