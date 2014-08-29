package models

import play.api.libs.json._
import play.api.Play.current

import reactivemongo.core.commands.LastError
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.collection.JSONCollection

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class User(
  login: String,
  accessToken: String,
  refreshToken: String,
  yoAccounts: Seq[String] = Nil,
  playlistId: Option[String] = None
) {

  def toJson = Json.obj(
    "login"      -> login,
    "yoAccounts" -> yoAccounts
  )
}

object User {
  implicit val userRead  = Json.reads[User]
  implicit val userWrite = Json.writes[User]

  def collection: JSONCollection = ReactiveMongoPlugin.db.collection[JSONCollection]("users")

  def create(user: User): Future[LastError] = collection.insert(Json.toJson(user))

  def update(login: String, accessToken: String, refreshToken: String): Future[LastError] =
    collection.update(
      selector = Json.obj("login" -> login),
      update   = Json.obj(
        "accessToken"  -> accessToken,
        "refreshToken" -> refreshToken
      )
    )

  def update(login: String, yoAccounts: Seq[String], playlistId: Option[String]): Future[LastError] =
    collection.update(
      selector = Json.obj("login" -> login),
      update   = Json.obj(
        "yoAccounts"  -> yoAccounts,
        "playlistId"  -> playlistId
      )
    )

  def get(login: String): Future[Option[User]] =
    collection.find(Json.obj("login" -> login))
              .cursor[User]
              .collect[Seq](1)
              .map(_.headOption)

  def find(yoAccount: String): Future[Seq[User]] =
    collection.find(Json.obj("yoAccounts" -> yoAccount))
              .cursor[User]
              .collect[Seq]()

  def delete(login: String): Future[LastError] =
    collection.remove( Json.obj("login" -> login) )

}