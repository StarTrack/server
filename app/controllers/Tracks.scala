package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.json._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import models._
import services._

object Tracks extends Controller {

  def track = Action.async {
    FipRadio.currentTrack.map { track => Ok( Json.toJson(track) ) }
  }

}
