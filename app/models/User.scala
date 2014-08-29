package models

import play.api.libs.json._

case class User(login: String, refreshToken: String, yoAccounts: Seq[String])

object User {
  implicit val userRead  = Json.reads[User]
  implicit val userWrite = Json.writes[User]
}