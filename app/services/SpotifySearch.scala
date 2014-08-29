package services

import java.net.URLEncoder

import play.api.libs.ws.WS

import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

object SpotifySearch {
  val url = "http://ws.spotify.com/search/1/track.json"


  def search(artist: String, track: String): Future[Option[String]] = {
    search2(artist, track).flatMap {
      case a@Some(_) => Future.successful(a)
      case None      => search(track)
    }
  }

  def encode(str: String ) = URLEncoder.encode(str, "UTF8")

  def search(track: String): Future[Option[String]] = {
    WS.url("%s?q=track:%s".format(url, encode(track))).get().map { response =>
      response.json.\("tracks")(0).\("href").as[Option[String]]
    }
  }

  def search2(artist: String, track: String): Future[Option[String]] = {
    WS.url("%s?q=artist:%s+AND+track:%s".format(url, encode(artist), encode(track))).get().map { response =>
      response.json.\("tracks")(0).\("href").as[Option[String]]
    }
  }
}
