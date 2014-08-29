import play.api._

import reactivemongo.api.indexes._
import scala.concurrent.ExecutionContext.Implicits.global

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    val index = Index(
      Seq("login" -> IndexType.Ascending),
      name = Some("loginIdx"),
      unique = true,
      sparse = true
    )

    models.User.collection.indexesManager.ensure(index)
  }

}