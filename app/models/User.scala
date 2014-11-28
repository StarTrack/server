package models

import play.api.libs.json._
import play.api.Play.current

import reactivemongo.core.commands.LastError
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.collection.JSONCollection

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import services.SpotifyAuth

case class User(
  login: String,
  accessToken: String,
  refreshToken: String,
  yoAccounts: Seq[String] = Nil,
  playlistId: Option[String] = None
) {

  def toJson = Json.obj(
    "login"      -> login,
    "yoAccounts" -> yoAccounts,
    "playlistId" -> playlistId
  )
}

object User {
  implicit val userRead  = Json.reads[User]
  implicit val userWrite = Json.writes[User]

  def collection: JSONCollection = ReactiveMongoPlugin.db.collection[JSONCollection]("users")

  def create(user: User): Future[LastError] = collection.insert(Json.toJson(user.copy(login = user.login.toUpperCase)))

  def update(login: String, accessToken: String, refreshToken: String): Future[LastError] =
    collection.update(
      selector = Json.obj("login" -> login.toUpperCase),
      update   = Json.obj( "$set" -> Json.obj(
        "accessToken"  -> accessToken,
        "refreshToken" -> refreshToken
      ))
    )

  def update(login: String, yoAccounts: Seq[String], playlistId: Option[String]): Future[LastError] = {
    val builder = Seq.newBuilder[(String, JsValue)]

    builder += ( ("yoAccounts", Json.toJson(yoAccounts.map(_.toUpperCase))) )
    playlistId.map { pl =>  builder+= ( ("playlistId", JsString(pl)) ) }

    collection.update(
      selector = Json.obj("login" -> login),
      update   = Json.obj( "$set" -> new JsObject(builder.result))
    )
  }

  def get(login: String): Future[Option[User]] =
    collection.find(Json.obj("login" -> login))
              .cursor[User]
              .collect[Seq](1)
              .map(_.headOption)

  def getWithException(login: String): Future[User] =
    get(login).flatMap {
      case Some(user) => Future.successful(user)
      case None => Future.failed(new Exception("No user found"))
    }

  def find(yoAccount: String): Future[Seq[User]] =
    collection.find(Json.obj("yoAccounts" -> yoAccount.toUpperCase))
              .cursor[User]
              .collect[Seq]()

  def delete(login: String): Future[LastError] =
    collection.remove( Json.obj("login" -> login) )

  def refreshAccessToken(user: User): Future[User] = {
    SpotifyAuth.refreshAccessToken(user.refreshToken).flatMap { newAccessToken =>
      update(user.login, newAccessToken, user.refreshToken).map { lastErr =>
        user.copy(accessToken = newAccessToken)
      }
    }
  }

}