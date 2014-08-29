package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.json._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import models._
import services._

object Yo extends Controller {

  val yoToken = Play.configuration.getString("yo.token").getOrElse("")

  def yo(token: String, yoAccount: String) = Action.async {
    if( yoToken != token ) {
      Future.successful(BadRequest("Invalid token"))
    } else {
      val res = for {
        users   <- User.find(yoAccount)
        track   <- FipRadio.currentTrack
        trackId <- SpotifySearch.search(track)
      } yield (users, trackId)

      res.flatMap {
        case ( users, Some(trackId) ) =>
          Future.sequence(users.map { user => SpotifyWS.addToPlayList(user, trackId) })
                .map { _ => Ok("Yo") }

        case ( users, None ) => Future.successful( Ok("Yo : can't find Track") )
      }
    }
  }

}
