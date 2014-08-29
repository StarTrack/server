package services.spotify

import java.net.URLEncoder
import play.api.libs.json.{Json, JsValue}
import play.api.libs.ws.WS

import scala.concurrent._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.Play.current

object Conf {
  val client_id = "b49ca56baba34a0f885bc137b9cba887";
  val client_secret = "dbf9641a44ce4b7b90063e8cb6a61422";
  val redirect_uri = "http://localhost:9000/callback";
  val spotify_authorize = "https://accounts.spotify.com/authorize"
  val spotify_token = "https://accounts.spotify.com/api/token"
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

object SpotifyWS {
  def userAuthUrl(security_token: String): String = {
    val scope = "user-read-private user-read-email playlist-modify-private playlist-modify-public"

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

  def getSpotifyUser(access_token: String): Future[SpotifyUser] = {
    val headers = ("Authorization" -> s"Bearer $access_token")

    WS.url("https://api.spotify.com/v1/me").withHeaders(headers).get().flatMap { response =>
      response.status match {
        case 200 => {
          val parsed = Json.parse(response.body)
          Future.successful(parsed.as[SpotifyUser])
        }
        case _ => Future.failed(new Exception("Spotify id is bad"))
      }
    }
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
}

object Utils {
  def encodeUrlParams(params: Map[String,String]): String = {
   params.map {
     case (k, v) =>
       URLEncoder.encode(k,"utf-8") + "=" + URLEncoder.encode(v, "utf-8")
   }.mkString("&")
  }
}