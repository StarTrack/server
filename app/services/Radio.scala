package services

import scala.concurrent.Future

trait Radio {
  def name: String
  def currentTrack(): Future[Track]
}


object Radios {
  def getRadio(name: String): Future[Radio] = {
    availableRadios.find(r => r.name == name).map { r =>
      Future.successful(r)
    }.getOrElse {
      Future.failed(new Exception("this radio does not exist"))
    }
  }

  val availableRadios: Set[Radio] = Set(FipRadio, OuiFmRadio)
}
