package controllers

import play.api._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{Json, JsValue}
import play.api.mvc._
import play.api.Play.current
import scala.util.Random

import scala.concurrent._

import services._
import models._

object Auth extends Controller {

  val state_session_name = "user_state"

  def auth = Action {
    val user_state = Random.alphanumeric.take(16).mkString
    val url = SpotifyAuth.userAuthUrl(user_state)

    Redirect(url).withSession(
      state_session_name -> user_state
    )
  }

  def callback(code: String, state: String) = Action.async { request =>
    val is_state_valid =
      user_state_matching(request.session.get(state_session_name), state)

    if (is_state_valid) {
      SpotifyAuth.getTokens(code).flatMap { tokens =>
        SpotifyWS.getSpotifyUser(tokens.access_token).flatMap { spotifyUser =>
          authenticationSuccess(tokens, spotifyUser).map { user =>
            Redirect("/").withSession(
              "login" -> user.login
            )
          }
        }
      } recover {
        case msg: Exception => BadRequest(msg.toString)
      }
    } else {
      Future.successful(BadRequest("Unexpected user state"))
    }

  }

  private def user_state_matching(expected_state: Option[String], http_state: String) = {
    expected_state match {
      case Some(expected) if expected == http_state => true
      case _ => {
        play.Logger.debug("Expecting %s == %s".format(expected_state, http_state))
        false
      }
    }
  }

  private def authenticationSuccess(tokens: Tokens, spotifyUser: SpotifyUser): Future[User] = {
    User.get(spotifyUser.id).map {
      case Some(user) => {
        User.update(user.login, tokens.access_token, tokens.refresh_token)
        user
      }
      case None => {
        val user = User(
          spotifyUser.id,
          tokens.access_token,
          tokens.refresh_token
        )

        User.create(user)
        user
      }
    }
  }

}