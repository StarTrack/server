package services

import play.api._
import play.api.libs.json._
import play.api.libs.ws.WS
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.Play.current
import play.api.Logger

import scala.concurrent._

object YoWS {

  val url = "https://api.justyo.co/yo"

  def tokenForRadio(name: String): String = {
    Play.configuration.getString(s"yo.tokens.$name").getOrElse {
      new Exception(s"Radio token $name not found")
      ""
    }
  }

  def yoForRadio(reciever: String, radioName: String): Future[_] = {
    yo(reciever, tokenForRadio(radioName))
  }

  private def yo(reciever: String, token: String) = {
    WS.url(url).post(
      Map(
        "api_token" -> Seq(token),
        "username"  -> Seq(reciever)
      )
    ).flatMap { response =>
      response.status match {
        case 200 => Future.successful(Json.parse(response.body) )
        case otherCode => Future.failed(new Exception("Http code: %d Response: %s".format(otherCode, response.body)))
      }
    }

  }

}