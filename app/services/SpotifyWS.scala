package services

import play.api._
import play.api.libs.json._
import play.api.libs.ws.WS

import scala.concurrent._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.Play.current

import models._
import utils.Encoding

object Conf {
  val client_id =     Play.configuration.getString("spotify.id").get
  val client_secret = Play.configuration.getString("spotify.secret").get
  val redirect_uri =  Play.configuration.getString("spotify.redirect_url").get
}

object Endpoints {
  val authorize =  "https://accounts.spotify.com/authorize"
  val token =      "https://accounts.spotify.com/api/token"

  val user_info =  "https://api.spotify.com/v1/me"
  val playlists =  "https://api.spotify.com/v1/users/%s/playlists"
  val add_track =  "https://api.spotify.com/v1/users/%s/playlists/%s/tracks"

  val track_info = "https://api.spotify.com/v1/tracks/%s"
}

case class SpotifyUser(
  id: String,
  email: String
)

object SpotifyUser {
  implicit val reader = Json.reads[SpotifyUser]
}

case class Playlist(
  id: String,
  name: String
)

object Playlist {
  implicit val format = Json.format[Playlist]
}

object SpotifyWS {


  private def wsWithAuth(url: String, accessToken: String) = {
    val headers = ("Authorization" -> s"Bearer $accessToken")

    WS.url(url).withHeaders(headers)
               .withHeaders("Content-Type" -> "application/json")
  }

  def getSpotifyUser(accessToken: String): Future[SpotifyUser] = {
    val url = Endpoints.user_info

    wsWithAuth(url, accessToken).get.flatMap { response =>
      response.status match {
        case 200 => {
          val parsed = Json.parse(response.body)
          Future.successful(parsed.as[SpotifyUser])
        }
        case err => Future.failed(new Exception("Spotify user id bad status: $err body: $response.body"))
      }
    }
  }

  def playlists(login: String, accessToken: String): Future[Seq[Playlist]] = {
    val url = Endpoints.playlists.format( login )

    wsWithAuth(url, accessToken).get.flatMap { response =>
      response.status match {
        case 200 =>
          val parsed = Json.parse(response.body)
          Future.successful( (parsed \ "items").as[Seq[Playlist]] )
        case err => Future.failed(new Exception("Spotify playlists bad status: $err body: $response.body"))
      }
    }
  }

  def addToPlayList(user: User, trackId: String): Future[_] = {
    user.playlistId.map { playlistId =>
      play.Logger.debug("Add to playList : %s - %s - %s".format(user.login, playlistId, trackId) )
      val url = Endpoints.add_track.format(user.login, playlistId)

      wsWithAuth(url, user.accessToken)
        .post( Json.toJson(Seq(trackId)) )
        .map { response =>
          response.status match {
            case 201 => play.Logger.debug("Track was succesufly added to the playlist")
            case err => play.Logger.error("Error while added the track to the playlist: " + response.status.toString + " - " + response.body)
          }

        }
    }.getOrElse( Future.successful("No playList") )
  }

  def trackInfos(trackId: String): Future[JsValue] = {
    val cleanId = trackId.replace("spotify:track:", "")

    WS.url(Endpoints.track_info.format(cleanId))
      .get
      .map( _.json )
  }

}
