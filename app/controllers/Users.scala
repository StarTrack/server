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

object Users extends Controller with MongoController{
  def collection: JSONCollection = db.collection[JSONCollection]("users")

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def create = Action.async(parse.json[User]) { request =>
    User.create(request.body)
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