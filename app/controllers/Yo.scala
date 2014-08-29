package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import models._

object Yo extends Controller {

  val yoToken = Play.configuration.getString("yo.token").getOrElse("")

  def yo(token: String, yoAccount: String) = Action.async {
    if( yoToken != token ) {
      Future.successful(BadRequest("Invalid token"))
    } else {
      for {
        users <- User.find(yoAccount)
      } yield {
        Ok("yo")
      }
    }
  }

}
