package com.cory.playground.test

import org.mockito.{ArgumentMatchersSugar, IdiomaticMockito}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// tests that show how to use mockito-scala (particularly IdomaticMockito)
class MockitoSpec extends WordSpec
  with Matchers
  with ScalaFutures
  with IdiomaticMockito
  with ArgumentMatchersSugar {
  // classes that emulate real-life service/dao pattern
  case class Foo(value: String)

  class FakeDao {
    def get(filterParam: String): Future[Seq[Foo]] = ???
  }

  class FakeService(dao: FakeDao) {
    def getFoos(filterParam: String, valueSuffix: String): Future[Seq[Foo]] = {
      dao.get(filterParam).map { foos =>
        foos.map { foo =>
          foo.copy(value = s"${foo.value}$valueSuffix")
        }
      }
    }
  }

  // setup a service using a mocked out dao layer
  trait Setup {
    val dao = mock[FakeDao]
    val service = new FakeService(dao)

    // default behavior so that dao doesn't need to be individually setup for each test
    val defaultResult = Seq(Foo("default-foo"))
    dao.get(*) shouldReturn Future.successful(defaultResult)
  }

  "Mockito" should {
    "allow defaulting the value returned by a mock" in new Setup {
      val result = service.getFoos("any filter you want", "-the-suffix")

      val expected = Seq(Foo("default-foo-the-suffix"))
      result.futureValue should contain theSameElementsAs(expected)
    }

    "allow overriding the defaulted mock result with more specific matcher" in new Setup {
      val daoResult = Seq(
        Foo("specific1"),
        Foo("specific2")
      )
      dao.get("specific-filter") shouldReturn Future.successful(daoResult)

      val result = service.getFoos("specific-filter", "-the-suffix")

      val expected = Seq(
        Foo("specific1-the-suffix"),
        Foo("specific2-the-suffix")
      )
      result.futureValue should contain theSameElementsAs(expected)
    }

    "allow verifying calls to mock" in new Setup {
      service.getFoos("the-filter", "-the-suffix")

      dao.get(*) wasCalled once
      dao.get("the-filter") wasCalled once
      dao.get("not-the-filter") wasNever called
    }
  }
}
