import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.test.Helpers._
import play.api.test._
import services.SpotifySearch

import scala.concurrent.Await

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

  "Application" should {

    "send 404 on a bad request" in new WithApplication{
      route(FakeRequest(GET, "/boum")) must beNone
    }

    "render the index page" in new WithApplication{
      val home = route(FakeRequest(GET, "/")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      contentAsString(home) must contain ("Your new application is ready.")
    }
/*
    "test fip"  in new WithApplication{
      val f = FipRadio.currentTrack()
      val r = Await.result(f, scala.concurrent.duration.Duration(30, "seconds"))
      val r2 = Await.result(FipRadio.currentTrack(), scala.concurrent.duration.Duration(1, "seconds"))
    }
*/
    "test spotify search"  in new WithApplication{
      val f = SpotifySearch.search(artist = "Duke Ellington", track = "ISFAHAN")
      val r = Await.result(f, scala.concurrent.duration.Duration(30, "seconds"))
    }
  }
}
