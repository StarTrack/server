package services

import scala.concurrent.Future
import org.joda.time.DateTime

import play.api.Logger
import play.api.libs.json._
import play.api.libs.ws.WS
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._

object NovaRadio extends Radio {
  val name = "nova"

  def currentTrack(): Future[Track] = {
    WS.url(onAir).get().map(r => extractCurrentTrack(r.json))
  }

  def onAir: String = {
    "http://www.novaplanet.com/radionova/ontheair"
  }

  private val titleRx = """<div class="title">(.*)<\/div>""".r
  private val artistRx = """<div class="artist">(.*)<\/div>""".r
  private def extractCurrentTrack(json: JsValue): Track = {
    val current = (json \ "track")
    val markup = (current \ "markup").as[String]

    Logger.debug(s"got markup from nova: $markup")
    val artist = markup match {
      case artistRx(a) => Some(a)
      case _ => None
    }
    val title = markup match {
      case titleRx(t) => t
      case _ => ""
    }
    val startTime = DateTime.now().getMillis
    val endTime = startTime + 1000 * 60 // add 1 min
    Track(startTime, endTime, title, artist)
  }
}
