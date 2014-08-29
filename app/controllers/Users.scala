package controllers

import play.api._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import reactivemongo.api._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection

import models.User
import services.spotify.SpotifyWS

object Users extends Controller with MongoController{
  def collection: JSONCollection = db.collection[JSONCollection]("users")

  val updateReads = (
    (__ \ "yoAccount").read[Seq[String]]
  )

  def me = Action.async { request =>
    request.session.get("login")
            .map { login => User.get(login) }
            .getOrElse(Future.successful(None))
            .flatMap {

              case Some(user) => {
                val playlists = SpotifyWS.playlists(user.login, user.accessToken)
                playlists.map { pls =>
                  Ok(
                    user.toJson ++ Json.obj( "playlists" -> Json.toJson(pls) )
                  )
                }

              }

              case _          => Future.successful(Forbidden("You shall login"))
            }
  }

  def login(login: String) = Action {
    Ok("Connected").withSession("login" -> login)
  }

  def create = Action.async(parse.json[User]) { request =>
    User.create(request.body)
        .map(lastError => Created("Mongo LastError: %s".format(lastError)) )
  }

  def update(login: String) = Action.async(parse.json(updateReads)) { request =>
    User.update(login, request.body)
        .map(lastError => Created("Mongo LastError: %s".format(lastError)) )
  }

  def find(yoAccount: String) = Action.async {
    User.find(yoAccount)
        .map { accounts => Ok(Json.toJson(accounts)) }
  }

  def delete(login: String) = Action.async {
    User.delete(login)
        .map(lastError => Ok("Mongo LastError: %s".format(lastError)) )
  }

}