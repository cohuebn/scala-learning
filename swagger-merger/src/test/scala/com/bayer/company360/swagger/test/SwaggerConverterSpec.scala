package com.bayer.company360.swagger.test

import com.bayer.company360.swagger.SwaggerConverter

class SwaggerConverterSpec extends Spec {
  var swaggerConverter: SwaggerConverter = _

  override def beforeEach(): Unit = {
    super.beforeEach()
    swaggerConverter = new SwaggerConverter
  }

  "A swagger converter" should {
    val allFieldsPopulatedTestCase = getResourceAsFile("all-fields-populated.yaml")

    "parse the swagger version" in {
      val parsed = swaggerConverter.parse(allFieldsPopulatedTestCase)
      parsed.swagger should equal("2.0")
    }

    "parse the info element" in {
      val parsed = swaggerConverter.parse(allFieldsPopulatedTestCase)
      parsed.info.title should equal("A fancy title")
      parsed.info.description should equal(Some("This API will change your life"))
      parsed.info.version should equal("v67")
    }

    "parse the host element" in {
      val parsed = swaggerConverter.parse(allFieldsPopulatedTestCase)
      parsed.host should equal("a.domain.com")
    }

    "parse the schemes element" in {
      val parsed = swaggerConverter.parse(allFieldsPopulatedTestCase)
      parsed.schemes should contain theSameElementsInOrderAs List("https", "http")
    }

    "parse the base path element" in {
      val parsed = swaggerConverter.parse(allFieldsPopulatedTestCase)
      parsed.basePath should equal("/wacky-cat-pics/v6")
    }

    "parse the max records element" in {
      val parsed = swaggerConverter.parse(allFieldsPopulatedTestCase)
      parsed.`x-what-is-maximum-number-of-records-that-could-be-returned` should equal(33)
    }

    "parse the produces element" in {
      val parsed = swaggerConverter.parse(allFieldsPopulatedTestCase)
      val expected = List("application/json", "image/png", "image/gif", "image/jpeg")
      parsed.produces should contain theSameElementsInOrderAs expected
    }
  }
}
