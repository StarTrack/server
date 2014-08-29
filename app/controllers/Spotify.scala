package controllers

import play.api._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{Json, JsValue}
import play.api.mvc._
import play.api.Play.current
import scala.util.Random

import scala.concurrent._

import services.spotify._

object Spotify extends Controller {

  def auth = Action {
    val security_token = Random.alphanumeric.take(16).mkString
    val url = SpotifyWS.userAuthUrl(security_token)

    Redirect(url).withSession(
      "user_security_token" -> security_token
    )
  }

  def callback(code: String, state: String) = Action.async {
    println("code: " + code)
    println("state: " + state)
    val tokens: Future[Tokens] = SpotifyWS.getTokens(code)
    tokens.flatMap { tokens =>
      SpotifyWS.getSpotifyUser(tokens.access_token).map { user =>
        Ok("userId: " + user.id)
      }
    } recover {
      case msg: Exception => BadRequest(msg.toString)
    }

  }

  def authenticationSuccess() = {

  }

}