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
      val allFieldsParsed = swaggerConverter.parse(allFieldsPopulatedTestCase)
      allFieldsParsed.swagger should equal("2.0")
    }

    "parse the info element" in {
      val allFieldsParsed = swaggerConverter.parse(allFieldsPopulatedTestCase)
      allFieldsParsed.info.title should equal("A fancy title")
      allFieldsParsed.info.description should equal(Some("This API will change your life"))
      allFieldsParsed.info.version should equal("v67")
    }
  }
}
