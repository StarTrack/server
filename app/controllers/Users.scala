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
import services.SpotifyWS

object Users extends Controller with MongoController{
  def collection: JSONCollection = db.collection[JSONCollection]("users")

  val updateReads = (
    (__ \ "yoAccounts").read[Seq[String]] and
    (__ \ "playlistId").readNullable[String]
  ).tupled

  def me = Action.async { request =>
    request.session.get("login").map { login =>
      (for {
        user <- User.getWithException(login)
        refreshedUser <- User.refreshAccessToken(user)
        playlists <- SpotifyWS.playlists(user.login, user.accessToken)
      } yield {
        Ok(
          user.toJson ++ Json.obj( "playlists" -> Json.toJson(playlists) )
        )
      })
      .recover {
        case exception => {
          Logger.error(s"Exception $exception")
          Forbidden("You shall login (nf)")
        }
      }
    }.getOrElse(Future.successful(Forbidden("You shall login (cookies)")))

  }

  def create = Action.async(parse.json[User]) { request =>
    User.create(request.body)
        .map(lastError => Created("Mongo LastError: %s".format(lastError)) )
  }

  def update(login: String) = Action.async(parse.json(updateReads)) { request =>
    val (yoAccounts, playlistId) = request.body
    User.update(login, yoAccounts, playlistId)
        .map(lastError => Ok("Mongo LastError: %s".format(lastError)) )
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