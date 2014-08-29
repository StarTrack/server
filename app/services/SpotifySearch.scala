package services

import java.net.URLEncoder

import play.api.libs.ws.WS

import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

object SpotifySearch {
  val url = "http://ws.spotify.com/search/1/track.json"

  def search(artist: String, track: String): Future[Option[String]] = {
    WS.url(url + "?q=artist:"+URLEncoder.encode(artist)+"+AND+track:"+URLEncoder.encode(track)).get().map { response =>
      //println(response.body)
      val json = response.json
      val currentTrack = json.\("tracks")(0).\("href").as[Option[String]]
      //println(currentTrack)
      currentTrack
    }
  }
}
