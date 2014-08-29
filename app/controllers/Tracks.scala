package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.ws.WS

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import models._
import services._

object Tracks extends Controller {

  def track = Action.async {
    FipRadio.currentTrack.map { track => Ok( Json.toJson(track) ) }
  }

  private def currentTrackId: Future[Option[String]] = {
    for {
      track   <- FipRadio.currentTrack
      trackId <- SpotifySearch.search(track)
    } yield trackId
  }

  private def currentTrackInfos: Future[Option[JsValue]] = {
    currentTrackId.flatMap {
      case Some(id) => SpotifyWS.trackInfos(id).map(Some(_))
      case None     => Future.successful(None)
    }
  }

  def spotifyTrack = Action.async {
    currentTrackInfos.map {
      case Some(js) => Ok( Json.prettyPrint(js) )
      case None     => Ok("Can't find track")
    }
  }

  def cover = Action.async {
    currentTrackInfos.flatMap {
      case None     => Future.successful( Ok("Can't find track") )
      case Some(js) => {
        val url = ((js \ "album" \ "images").as[JsArray].value(0) \ "url").as[String]
        play.Logger.debug("URL : " + url)

        WS.url(url).get().map { r =>
          Ok(r.underlying[com.ning.http.client.Response].getResponseBodyAsBytes).as("image/png")
        }
      }
    }
  }

}
