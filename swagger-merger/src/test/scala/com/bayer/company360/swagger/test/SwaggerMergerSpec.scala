package com.bayer.company360.swagger.test

import java.io.File

import com.bayer.company360.swagger.SwaggerSchema._
import com.bayer.company360.swagger.{SwaggerConverter, SwaggerMerger}
import faker._

class SwaggerMergerSpec extends Spec {
  var swaggerConverter: SwaggerConverter = _
  var swaggerMerger: SwaggerMerger = _

  override def beforeEach(): Unit = {
    super.beforeEach()
    swaggerConverter = stub[SwaggerConverter]
    swaggerMerger = new SwaggerMerger(swaggerConverter)
  }

  "A swagger merger" should {
    "use base file for top-level information" in {
      val baseFile = new File("the-base")
      val filesToMerge = Seq(new File("blah"), new File("another"))

      val baseSwaggerDoc = DataGenerator.swaggerDoc(
        info = new SwaggerInfo(Lorem.sentence(), None, "0.0.1-fake"),
        host = "theexpectedhost.com",
        schemes = List(Lorem.sentence(1), Lorem.sentence(1)),
        basePath = "the-expected-base-path",
        produces = List("application/json", "image/gif"),
      )
      val doc1ToMerge = DataGenerator.swaggerDoc()
      val doc2ToMerge = DataGenerator.swaggerDoc()
      (swaggerConverter.parse(_)).when(baseFile).returns(baseSwaggerDoc)
      (swaggerConverter.parse(_)).when(filesToMerge.head).returns(doc1ToMerge)
      (swaggerConverter.parse(_)).when(filesToMerge.last).returns(doc2ToMerge)

      val result = swaggerMerger.mergeFiles(baseFile, filesToMerge)

      result.info.title should equal(baseSwaggerDoc.info.title)
      result.info.description should equal(baseSwaggerDoc.info.description)
      result.info.version should equal(baseSwaggerDoc.info.version)
      result.host should equal(baseSwaggerDoc.host)
      result.schemes should equal(baseSwaggerDoc.schemes)
      result.basePath should equal(baseSwaggerDoc.basePath)
      result.`x-what-is-maximum-number-of-records-that-could-be-returned` should equal(baseSwaggerDoc.`x-what-is-maximum-number-of-records-that-could-be-returned`)
      result.produces should equal(baseSwaggerDoc.produces)
    }
  }
}
