package services

import play.api.libs.json._
import play.api.libs.ws.WS

import scala.concurrent._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.Play.current

import utils.{ Http, Encoding }

case class Tokens(
  access_token: String,
  refresh_token: String
)

object SpotifyAuth {
  def userAuthUrl(security_token: String): String = {
    val scope = "user-read-private user-read-email playlist-modify-private playlist-modify-public playlist-read-private"

    Endpoints.authorize + "?" + Http.encodeUrlParams(
      Map(
        "response_type" -> "code",
        "client_id"     -> Conf.client_id,
        "scope"         -> scope,
        "redirect_uri"  -> Conf.redirect_uri,
        "state"         -> security_token
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
    WS.url(Endpoints.token).post(
      Map(
        "code" -> Seq(code),
        "redirect_uri"  -> Seq(Conf.redirect_uri),
        "grant_type"    -> Seq("authorization_code"),
        "client_id"     -> Seq(Conf.client_id),
        "client_secret" -> Seq(Conf.client_secret)
      )
    ).flatMap { response =>
      response.status match {
        case 200 => Future.successful(Json.parse(response.body) )
        case otherCode => Future.failed(new Exception("Http code: %d Response: %s".format(otherCode, response.body)))
      }
    }
  }


  def refreshAccessToken(refreshToken: String): Future[String] = {
    val header_part: String = Encoding.encodeBase64("%s:%s".format(Conf.client_id, Conf.client_secret))

    WS.url(Endpoints.token)
      .withHeaders(
        ("Authorization" -> s"Basic $header_part")
      )
      .post(
        Map(
          "grant_type"    -> Seq("refresh_token"),
          "refresh_token" -> Seq(refreshToken)
        )
      )
      .flatMap { response =>
        response.status match {
          case 200 =>       {
            play.Logger.debug("Access token was refreshed")
            Future.successful( (response.json \ "access_token").as[String] )
          }
          case otherCode => Future.failed(new Exception("Http code: %d Response: %s".format(otherCode, response.body)))
        }
      }
  }


  def withNewAccessToken(refreshToken: String)(block: String => Future[_] ) = {
    SpotifyAuth.refreshAccessToken(refreshToken).flatMap { accessToken =>
      block(accessToken)
    }
  }
}