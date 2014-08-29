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
                  lien: String)

object Track {
  implicit val reader = Json.reads[Track]
}

object FipRadio {
  private var cachedResponse: Option[Track] = None

  def currentTrack(): Future[Track] = if (cachedResponse.isDefined) {
    val timestamp = DateTime.now().getMillis
    val track = cachedResponse.get
    if (track.endTime > timestamp) {
      Future.successful(track)
    } else {
      getPlayerCurrent
    }
  } else {
    getPlayerCurrent
  }

  val apiURL = "http://www.fipradio.fr/sites/default/files/import_si/si_titre_antenne/FIP_player_current.json"

  private def getPlayerCurrent: Future[Track] = {
    WS.url(apiURL).get().map { response =>
      val t = response.json.\("current").\("song").as[Track]
      println(t)
      cachedResponse = Some(t)
      t
    }
  }

}

