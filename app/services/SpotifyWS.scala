package services

import play.api._
import java.net.URLEncoder
import play.api.libs.json._
import play.api.libs.ws.WS

import scala.concurrent._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.Play.current

import models._

object Conf {
  val client_id = Play.configuration.getString("spotify.id").get
  val client_secret = Play.configuration.getString("spotify.secret").get
  val redirect_uri = "http://localhost:9000/callback";
  val spotify_authorize = "https://accounts.spotify.com/authorize"
  val spotify_token = "https://accounts.spotify.com/api/token"

  val user_info_endpoint = "https://api.spotify.com/v1/me"
  val playlists_endpoint = "https://api.spotify.com/v1/users/%s/playlists"
}

case class Tokens(
  access_token: String,
  refresh_token: String
)

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
  def userAuthUrl(security_token: String): String = {
    val scope = "user-read-private user-read-email playlist-modify-private playlist-modify-public playlist-read-private"

    Conf.spotify_authorize + "?" + Utils.encodeUrlParams(
      Map(
        "response_type" -> "code",
        "client_id" -> Conf.client_id,
        "scope" -> scope,
        "redirect_uri" -> Conf.redirect_uri,
        "state" -> security_token
      )
    )
  }

  def getTokens(code: String): Future[Tokens] = {
    getTokensJson(code).flatMap { json =>
      ( (json \ "access_token").asOpt[String], (json \ "refresh_token").asOpt[String] ) match {

        case (Some(access_token), Some(refresh_token)) =>
          Future.successful( Tokens(access_token, refresh_token) )

        case _ =>
          Future.failed( new Exception("Cannot find access_token") )
      }
    }
  }

  def getTokensJson(code: String): Future[JsValue] = {
    WS.url(Conf.spotify_token).post(
      Map(
        "code" -> Seq(code),
        "redirect_uri" -> Seq(Conf.redirect_uri),
        "grant_type" -> Seq("authorization_code"),
        "client_id" -> Seq(Conf.client_id),
        "client_secret" -> Seq(Conf.client_secret)
      )
    ).flatMap { response =>
      response.status match {
        case 200 => Future.successful(Json.parse(response.body) )
        case otherCode => Future.failed(new Exception("Http code: %d Response: %s".format(otherCode, response.body)))
      }
    }
  }

  private def wsWithAuth(url: String, access_token: String) = {
    val headers = ("Authorization" -> s"Bearer $access_token")

    WS.url(url).withHeaders(headers)
  }

  def getSpotifyUser(access_token: String): Future[SpotifyUser] = {
    val url = Conf.user_info_endpoint

    wsWithAuth(url, access_token).get.flatMap { response =>
      response.status match {
        case 200 => {
          val parsed = Json.parse(response.body)
          Future.successful(parsed.as[SpotifyUser])
        }
        case _ => Future.failed(new Exception("Spotify user id bad status"))
      }
    }
  }

  def playlists(login: String, access_token: String): Future[Seq[Playlist]] = {
    val url = Conf.playlists_endpoint.format( login )

    wsWithAuth(url, access_token).get.flatMap { response =>
      response.status match {
        case 200 =>
          val parsed = Json.parse(response.body)
          Future.successful( (parsed \ "items").as[Seq[Playlist]] )
        case _ => Future.failed(new Exception("Spotify playlists bad status"))
      }
    }
  }

  def addToPlayList(user: User, trackId: String) = {
    val headers = ("Authorization" -> s"Bearer ${user.accessToken}")

     WS.url(s"https://api.spotify.com/v1/users/${user.login}/playlists/${user.playlistId}/tracks")
       .withHeaders(headers)
       .post( Json.toJson(Seq(trackId)) )
  }

}

object Utils {
  def encodeUrlParams(params: Map[String,String]): String = {
   params.map {
     case (k, v) =>
       URLEncoder.encode(k,"utf-8") + "=" + URLEncoder.encode(v, "utf-8")
   }.mkString("&")
  }
}