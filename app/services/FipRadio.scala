package services

import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.libs.ws.WS
import scala.concurrent.Future

import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._

case class Visuel(small: String, medium: String)

object Visuel {
  implicit val reader = Json.reads[Visuel]
}

case class Track( startTime: Long,
                  endTime: Long,
                  titre: String,
                  interpreteMorceau: Option[String] ) {

  def start: DateTime = new DateTime(startTime)

  def end: DateTime = new DateTime(endTime)
}

object Track {
  implicit val reader = Json.reads[Track]
  implicit val writer = Json.writes[Track]
}

case class FipPlayer(current: Track)

object FipRadio extends Radio {
  val name = "fip"
  private var cachedResponse: Option[FipPlayer] = None

  def currentTrack(): Future[Track] = getFipPlayer().map(_.current)

  def getFipPlayer(): Future[FipPlayer] = if (cachedResponse.isDefined) {
    val timestamp = DateTime.now().getMillis
    val currentTrack = cachedResponse.get.current
    if (currentTrack.endTime > timestamp) {
      Future.successful(cachedResponse.get)
    } else {
      getPlayerCurrent
    }
  } else {
    getPlayerCurrent
  }

  val apiURL = "http://www.fipradio.fr/sites/default/files/import_si/si_titre_antenne/FIP_player_current.json"

  private def getPlayerCurrent: Future[FipPlayer] = {
    WS.url(apiURL).get().map { response =>
      val json = response.json
      val currentTrack = json.\("current").\("song").as[Track]

      val p = FipPlayer(currentTrack)

      //println(p)
      cachedResponse = Some(p)
      p
    }
  }

}

