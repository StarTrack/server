package models

case class User(login: String, refreshToken: String, yoAccounts: Seq[String])