package com.bayer.company360.swagger.test

import java.io.File

import com.bayer.company360.swagger.SwaggerSchema._
import com.bayer.company360.swagger.test.DataGenerator.HttpMethod
import com.bayer.company360.swagger._
import faker._

import scala.util.{Failure, Success}

class SwaggerMergerSpec extends Spec {
  trait Setup {
    val swaggerConverter = stub[SwaggerConverter]
    val swaggerMerger = new SwaggerMerger(swaggerConverter)

    val baseFile = new File("the-base")
    val baseSwaggerDoc = DataGenerator.swaggerDoc(
      info = DataGenerator.swaggerInfo(title = Lorem.sentence(), version = "0.0.1-fake"),
      host = "theexpectedhost.com",
      schemes = List(Lorem.sentence(1), Lorem.sentence(1)),
      basePath = "the-expected-base-path",
      produces = List("application/json", "image/gif"),
      paths = Map(
        "/a/base/path" -> DataGenerator.swaggerPath(HttpMethod.Get, summary = "from the base")
      ),
      definitions = Option(Map(
        "baseDocDef" -> DataGenerator.swaggerDefinition(`type` = "string")
      ))
    )

    (swaggerConverter.parse(_)).when(baseFile).returns(Success(baseSwaggerDoc))

    def mergeValidFiles(baseFile: File, filesToMerge: Seq[File]) = {
      val mergeResult = swaggerMerger.mergeFiles(baseFile, filesToMerge)
      if (mergeResult.isFailure)
        fail(s"Unexpected merge failure\n${mergeResult.failure.exception}", mergeResult.failure.exception)

      mergeResult.success.value
    }
  }

  "A swagger merger" should {
    "aggregate all file parsing failures" in new Setup {
      val filesToMerge = (1 to 2).map(counter => new File(s"file-$counter"))
      val docs = filesToMerge.map(_ => DataGenerator.swaggerDoc())
      (swaggerConverter.parse(_)).when(filesToMerge.head).returns(Failure(new Exception("Whoopsie doopsies")))
      (swaggerConverter.parse(_)).when(filesToMerge.last).returns(Failure(new Exception("Ouchy")))

      val result = swaggerMerger.mergeFiles(baseFile, filesToMerge)

      result.failure.exception shouldBe a[AggregateThrowable]
    }

      "use base file for top-level information" in new Setup {
      val filesToMerge = (1 to 2).map(counter => new File(s"file-$counter"))
      val docs = filesToMerge.map(_ => DataGenerator.swaggerDoc())
      (swaggerConverter.parse(_)).when(filesToMerge.head).returns(Success(docs.head))
      (swaggerConverter.parse(_)).when(filesToMerge.last).returns(Success(docs.last))

      val result = mergeValidFiles(baseFile, filesToMerge)

      result.info.title should equal(baseSwaggerDoc.info.title)
      result.info.description should equal(baseSwaggerDoc.info.description)
      result.info.version should equal(baseSwaggerDoc.info.version)
      result.host should equal(baseSwaggerDoc.host)
      result.schemes should equal(baseSwaggerDoc.schemes)
      result.basePath should equal(baseSwaggerDoc.basePath)
      result.`x-what-is-maximum-number-of-records-that-could-be-returned` should equal(baseSwaggerDoc.`x-what-is-maximum-number-of-records-that-could-be-returned`)
      result.produces should equal(baseSwaggerDoc.produces)
    }

    "merge paths from all swagger documents" in new Setup {
      val file1 = new File("file-1")
      val doc1 = DataGenerator.swaggerDoc(paths = Option(Map(
        "/sub1/path1" -> DataGenerator.swaggerPath(HttpMethod.Get),
        "/sub1/path2" -> DataGenerator.swaggerPath(HttpMethod.Post)
      )))
      (swaggerConverter.parse(_)).when(file1).returns(Success(doc1))

      val file2 = new File("file-2")
      val doc2 = DataGenerator.swaggerDoc(paths = Option(Map(
        "/sub1/path1" -> doc1.paths("/sub1/path1").copy(),
        "/sub2/path1" -> DataGenerator.swaggerPath(HttpMethod.Get),
      )))
      (swaggerConverter.parse(_)).when(file2).returns(Success(doc2))

      val result = mergeValidFiles(baseFile, Seq(file1, file2))

      val expectedPaths = Seq("/a/base/path", "/sub1/path1", "/sub1/path2", "/sub2/path1")
      result.paths.keys should contain theSameElementsAs(expectedPaths)
      assertOnOption(result.paths.get("/a/base/path")) { path =>
        path.get should equal(baseSwaggerDoc.paths("/a/base/path").get)
        path.post shouldBe(empty)
      }

      assertOnOption(result.paths.get("/sub1/path1")) { path =>
        path.get should equal(doc1.paths("/sub1/path1").get)
        path.post shouldBe(empty)
      }

      assertOnOption(result.paths.get("/sub1/path2")) { path =>
        path.get shouldBe(empty)
        path.post should equal(doc1.paths("/sub1/path2").post)
      }

      assertOnOption(result.paths.get("/sub2/path1")) { path =>
        path.get should equal(doc2.paths("/sub2/path1").get)
        path.post shouldBe(empty)
      }
    }

    "merge definitions from all swagger documents" in new Setup {
      val file1 = new File("file-1")
      val file2 = new File("file-2")

      val doc1 = DataGenerator.swaggerDoc(definitions = Option(Map(
        "doc1Def1" -> DataGenerator.swaggerDefinition(`type` = "string"),
        "baseDocDef" -> DataGenerator.swaggerDefinition(`type` = "string")
      )))
      (swaggerConverter.parse(_)).when(file1).returns(Success(doc1))

      val doc2 = DataGenerator.swaggerDoc(definitions = Option(Map(
        "doc2Def1" -> DataGenerator.swaggerDefinition(`type` = "integer"),
        "doc2Def2" -> DataGenerator.swaggerDefinition(`$ref` = "another-ref")
      )))
      (swaggerConverter.parse(_)).when(file2).returns(Success(doc2))

      val result = mergeValidFiles(baseFile, Seq(file1, file2))

      val expectedDefinitions = Seq("baseDocDef", "doc1Def1", "doc2Def1", "doc2Def2")
      result.definitions.keys should contain theSameElementsAs(expectedDefinitions)
      assertOnOption(result.definitions.get("baseDocDef")) { definition =>
        definition.`type` should equal(Some("string"))
      }

      assertOnOption(result.definitions.get("doc1Def1")) { definition =>
        definition.`type` should equal(Some("string"))
      }

      assertOnOption(result.definitions.get("doc2Def1")) { definition =>
        definition.`type` should equal(Some("integer"))
      }

      assertOnOption(result.definitions.get("doc2Def2")) { definition =>
        definition.`$ref` should equal(Some("another-ref"))
      }
    }

    "aggregate errors for all conflicting paths" in new Setup {
      val file1 = new File("file-1")
      val doc1 = DataGenerator.swaggerDoc(paths = Option(Map(
        "/sub1/path1" -> DataGenerator.swaggerPath(HttpMethod.Get),
        "/sub1/path2" -> DataGenerator.swaggerPath(HttpMethod.Post),
        "/sub1/path3" -> DataGenerator.swaggerPath(HttpMethod.Post)
      )))
      (swaggerConverter.parse(_)).when(file1).returns(Success(doc1))

      val file2 = new File("file-2")
      val doc2 = DataGenerator.swaggerDoc(paths = Option(Map(
        "/sub1/path1" -> DataGenerator.swaggerPath(HttpMethod.Get, summary = "I don't match doc1"),
        "/sub1/path3" -> DataGenerator.swaggerPath(HttpMethod.Get, summary = "I don't match doc1"),
        "/sub2/path1" -> DataGenerator.swaggerPath(HttpMethod.Get)
      )))
      (swaggerConverter.parse(_)).when(file2).returns(Success(doc2))

      val result = swaggerMerger.mergeFiles(baseFile, Seq(file1, file2))

      result.failure.exception shouldBe a[AggregateThrowable]
      val message = result.failure.exception.getMessage
      message should include("The path /sub1/path1 has conflicts")
      message should include("The path /sub1/path3 has conflicts")
      message should not include("/sub1/path2")
      message should not include("/sub2/path1")
    }

    "aggregate errors for all conflicting definitions" in new Setup {
      val file1 = new File("file-1")
      val doc1 = DataGenerator.swaggerDoc(definitions = Option(Map(
        "baseDocDef" -> DataGenerator.swaggerDefinition(`type` = "int")
      )))
      (swaggerConverter.parse(_)).when(file1).returns(Success(doc1))

      val file2 = new File("file-2")
      val doc2 = DataGenerator.swaggerDoc(definitions = Option(Map(
        "notTheBaseDocDef" -> DataGenerator.swaggerDefinition(`type` = "string")
      )))
      (swaggerConverter.parse(_)).when(file2).returns(Success(doc2))

      val result = swaggerMerger.mergeFiles(baseFile, Seq(file1, file2))

      result.failure.exception shouldBe a[AggregateThrowable]
      result.failure.exception.getMessage should include("The definition baseDocDef has conflicts")
      result.failure.exception.getMessage should not include("notTheBaseDocDef")
    }

    "aggregate errors when both paths and definitions conflict" in new Setup {
      val file1 = new File("file-1")
      val doc1 = DataGenerator.swaggerDoc(
        paths = Option(Map(
        "/sub1/path1" -> DataGenerator.swaggerPath(HttpMethod.Get),
        "/sub1/path2" -> DataGenerator.swaggerPath(HttpMethod.Post),
        )),
        definitions = Option(Map(
          "baseDocDef" -> DataGenerator.swaggerDefinition(`type` = "int"),
          "notTheBaseDocDef" -> DataGenerator.swaggerDefinition(`type` = "string")
        ))
      )
      (swaggerConverter.parse(_)).when(file1).returns(Success(doc1))

      val file2 = new File("file-2")
      val doc2 = DataGenerator.swaggerDoc(paths = Option(Map(
        "/sub1/path1" -> DataGenerator.swaggerPath(HttpMethod.Get, summary = "I don't match doc1"),
        "/sub2/path1" -> DataGenerator.swaggerPath(HttpMethod.Get)
      )))
      (swaggerConverter.parse(_)).when(file2).returns(Success(doc2))

      val result = swaggerMerger.mergeFiles(baseFile, Seq(file1, file2))

      result.failure.exception shouldBe a[AggregateThrowable]
      val message = result.failure.exception.getMessage
      message should include("The path /sub1/path1 has conflicts")
      message should not include("/sub1/path2")
      message should not include("/sub2/path1")
      result.failure.exception.getMessage should include("The definition baseDocDef has conflicts")
      message should not include("notTheBaseDocDef")
    }
  }
}
