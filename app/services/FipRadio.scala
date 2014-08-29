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

case class Track(
                  startTime: Long,
                  endTime: Long,
                  titre: String,
                  titreAlbum: String,
                  interpreteMorceau: String,
                  anneeEditionMusique: Option[String], //missing sometimes
                  visuel: Visuel,
                  lien: String) {
  def start: DateTime = new DateTime(startTime)

  def end: DateTime = new DateTime(endTime)
}

object Track {
  implicit val reader = Json.reads[Track]
}

case class FipPlayer(
                      current: Track,
                      previous1: Track,
                      previous2: Track,
                      next1: Track,
                      next2: Track)

object FipRadio {
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
      val currentTrack = response.json.\("current").\("song").as[Track]
      val p1Track = response.json.\("previous1").\("song").as[Track]
      val p2Track = response.json.\("previous2").\("song").as[Track]
      val n1Track = response.json.\("next1").\("song").as[Track]
      val n2Track = response.json.\("next2").\("song").as[Track]

      val p = FipPlayer(currentTrack, p1Track, p2Track, n1Track, n2Track)

      println(p)
      cachedResponse = Some(p)
      p
    }
  }

}

