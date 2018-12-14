package com.bayer.company360.swagger.test

import java.io.File

import com.bayer.company360.swagger.{AccumulatedSwaggerDoc, SwaggerDocAccumulator}
import com.bayer.company360.swagger.SwaggerSchema.{SwaggerBase, SwaggerDoc}
import com.bayer.company360.swagger.test.DataGenerator.HttpMethod

class SwaggerDocAccumulatorSpec extends Spec {
  trait Setup {
    val file1 = new File(faker.Internet.domain_word)
    val swaggerDoc1 = DataGenerator.swaggerDoc(
      paths = Map(
        "/path1" -> DataGenerator.swaggerPath(HttpMethod.Get),
        "/path2" -> DataGenerator.swaggerPath(HttpMethod.Post)
      ),
      definitions = Map("definition1" -> DataGenerator.swaggerDefinition())
    )

    val file2 = new File(faker.Internet.domain_word)
    val swaggerDoc2 = DataGenerator.swaggerDoc(
      paths = Map(
        "/path1" -> swaggerDoc1.paths("/path1").copy(),
        "/newPath" -> DataGenerator.swaggerPath(HttpMethod.Post)
      ),
      definitions = Map(
        "definition1" -> swaggerDoc1.definitions("definition1").copy(),
        "newDefinition" -> DataGenerator.swaggerDefinition()
      )
    )

    val file3 = new File(faker.Internet.domain_word)
    val swaggerDoc3 = DataGenerator.swaggerDoc(
      paths = Map(
        "/path1" -> swaggerDoc1.paths("/path1").copy(),
        "/newPath2" -> DataGenerator.swaggerPath(HttpMethod.Get)
      ),
      definitions = Map("definition1" -> swaggerDoc1.definitions("definition1").copy())
    )
  }

  trait BaseSwaggerDocAssertions extends Setup {
    def getExpectedSwaggerDoc: SwaggerDoc
    def getSwaggerBase: SwaggerBase

    "set the swagger version" in new Setup {
      val result = getSwaggerBase
      result.swagger should equal(getExpectedSwaggerDoc.swagger)
    }

    "set the info element" in new Setup {
      val result = getSwaggerBase
      result.info.title should equal(getExpectedSwaggerDoc.info.title)
      result.info.description should equal(getExpectedSwaggerDoc.info.description)
      result.info.version should equal(getExpectedSwaggerDoc.info.version)
    }

    "set the host element" in new Setup {
      val result = getSwaggerBase
      result.host should equal(getExpectedSwaggerDoc.host)
    }

    "set the schemes element" in new Setup {
      val result = getSwaggerBase
      result.schemes should contain theSameElementsInOrderAs getExpectedSwaggerDoc.schemes
    }

    "set the base path element" in new Setup {
      val result = getSwaggerBase
      result.basePath should equal(getExpectedSwaggerDoc.basePath)
    }

    "set the max records element" in new Setup {
      val result = getSwaggerBase
      result.`x-what-is-maximum-number-of-records-that-could-be-returned` should equal(getExpectedSwaggerDoc.`x-what-is-maximum-number-of-records-that-could-be-returned`)
    }

    "set the produces element" in new Setup {
      val result = getSwaggerBase
      result.produces should contain theSameElementsInOrderAs getExpectedSwaggerDoc.produces
    }
  }

  "A SwaggerDocAccumulator" when {
    "creating a new AccumulatedSwaggerDoc" should {
      behave like new BaseSwaggerDocAssertions{
        override def getExpectedSwaggerDoc: SwaggerDoc = swaggerDoc1
        override def getSwaggerBase: SwaggerBase =
          SwaggerDocAccumulator.createAccumulatedSwaggerDoc(file1, swaggerDoc1)
      }

      "set the paths element" in new Setup {
        var result = SwaggerDocAccumulator.createAccumulatedSwaggerDoc(file1, swaggerDoc1)
        result.paths.keys should contain theSameElementsAs(Seq("/path1", "/path2"))

        result.paths("/path1").map(_.value) should contain theSameElementsAs(Seq(swaggerDoc1.paths("/path1")))
        result.paths("/path1").map(_.file) should contain theSameElementsAs(Seq(file1))

        result.paths("/path2").map(_.value) should contain theSameElementsAs(Seq(swaggerDoc1.paths("/path2")))
        result.paths("/path2").map(_.file) should contain theSameElementsAs(Seq(file1))
      }

      "set the definitions element" in new Setup {
        var result = SwaggerDocAccumulator.createAccumulatedSwaggerDoc(file1, swaggerDoc1)
        result.definitions.keys should contain theSameElementsAs(Seq("definition1"))

        result.definitions("definition1").map(_.value) should contain theSameElementsAs(Seq(swaggerDoc1.definitions("definition1")))
        result.definitions("definition1").map(_.file) should contain theSameElementsAs(Seq(file1))
      }
    }

    "merging accumulators" should {
      behave like new BaseSwaggerDocAssertions {
        override def getExpectedSwaggerDoc: SwaggerDoc = swaggerDoc1
        override def getSwaggerBase: SwaggerBase = {
          val accumulated1 = SwaggerDocAccumulator.createAccumulatedSwaggerDoc(file1, swaggerDoc1)
          val accumulated2 = SwaggerDocAccumulator.createAccumulatedSwaggerDoc(file2, swaggerDoc2)
          val accumulated3 = SwaggerDocAccumulator.createAccumulatedSwaggerDoc(file3, swaggerDoc3)
          SwaggerDocAccumulator.mergeAccumulators(Seq(accumulated1, accumulated2, accumulated3))
        }
      }

      "append paths from multiple swagger documents" in new Setup {
        val accumulated1 = SwaggerDocAccumulator.createAccumulatedSwaggerDoc(file1, swaggerDoc1)
        val accumulated2 = SwaggerDocAccumulator.createAccumulatedSwaggerDoc(file2, swaggerDoc2)
        val accumulated3 = SwaggerDocAccumulator.createAccumulatedSwaggerDoc(file3, swaggerDoc3)
        val result = SwaggerDocAccumulator.mergeAccumulators(Seq(accumulated1, accumulated2, accumulated3))

        result.paths.keys should contain theSameElementsAs(Seq("/path1", "/path2", "/newPath", "/newPath2"))
        val path1 = result.paths("/path1")
        val expectedPath1Values = Seq(swaggerDoc1.paths("/path1"), swaggerDoc2.paths("/path1"), swaggerDoc3.paths("/path1"))
        path1.map(x => x.value) should contain theSameElementsAs(expectedPath1Values)
        path1.map(x => x.file) should contain theSameElementsAs(Seq(file1, file2, file3))

        val path2 = result.paths("/path2")
        val expectedPath2Values = Seq(swaggerDoc1.paths("/path2"))
        path2.map(x => x.value) should contain theSameElementsAs(expectedPath2Values)
        path2.map(x => x.file) should contain theSameElementsAs(Seq(file1))

        val newPath = result.paths("/newPath")
        val expectedNewPathValues = Seq(swaggerDoc2.paths("/newPath"))
        newPath.map(x => x.value) should contain theSameElementsAs(expectedNewPathValues)
        newPath.map(x => x.file) should contain theSameElementsAs(Seq(file2))

        val newPath2 = result.paths("/newPath2")
        val expectedNewPath2Values = Seq(swaggerDoc3.paths("/newPath2"))
        newPath2.map(x => x.value) should contain theSameElementsAs(expectedNewPath2Values)
        newPath2.map(x => x.file) should contain theSameElementsAs(Seq(file3))
      }

      "append definitions from another swagger document" in new Setup {
        val accumulated1 = SwaggerDocAccumulator.createAccumulatedSwaggerDoc(file1, swaggerDoc1)
        val accumulated2 = SwaggerDocAccumulator.createAccumulatedSwaggerDoc(file2, swaggerDoc2)
        val accumulated3 = SwaggerDocAccumulator.createAccumulatedSwaggerDoc(file3, swaggerDoc3)
        val result = SwaggerDocAccumulator.mergeAccumulators(Seq(accumulated1, accumulated2, accumulated3))

        result.definitions.keys should contain theSameElementsAs(Seq("definition1", "newDefinition"))
        val definition1 = result.definitions("definition1")
        val expectedDefinition1Values = Seq(swaggerDoc1.definitions("definition1"), swaggerDoc2.definitions("definition1"), swaggerDoc3.definitions("definition1"))
        definition1.map(x => x.value) should contain theSameElementsAs(expectedDefinition1Values)
        definition1.map(x => x.file) should contain theSameElementsAs(Seq(file1, file2, file3))

        val newDefinition = result.definitions("newDefinition")
        val expectedNewDefinitionValues = Seq(swaggerDoc2.definitions("newDefinition"))
        newDefinition.map(x => x.value) should contain theSameElementsAs(expectedNewDefinitionValues)
        newDefinition.map(x => x.file) should contain theSameElementsAs(Seq(file2))
      }
    }

    "converting back to swagger doc" should {
      def convertValidAccumulated(accumulated: AccumulatedSwaggerDoc): SwaggerDoc = {
        val result = SwaggerDocAccumulator.toSwaggerDoc(accumulated)
        result.success.value
      }

      behave like new BaseSwaggerDocAssertions {
        override def getExpectedSwaggerDoc: SwaggerDoc = swaggerDoc1
        override def getSwaggerBase: SwaggerBase = {
          val accumulated1 = SwaggerDocAccumulator.createAccumulatedSwaggerDoc(file1, swaggerDoc1)
          val accumulated2 = SwaggerDocAccumulator.createAccumulatedSwaggerDoc(file2, swaggerDoc2)
          val accumulated3 = SwaggerDocAccumulator.createAccumulatedSwaggerDoc(file3, swaggerDoc3)
          val accumulated = SwaggerDocAccumulator.mergeAccumulators(Seq(accumulated1, accumulated2, accumulated3))
          convertValidAccumulated(accumulated)
        }
      }

      "flatten paths" in new Setup {
        val accumulated1 = SwaggerDocAccumulator.createAccumulatedSwaggerDoc(file1, swaggerDoc1)
        val accumulated2 = SwaggerDocAccumulator.createAccumulatedSwaggerDoc(file2, swaggerDoc2)
        val accumulated3 = SwaggerDocAccumulator.createAccumulatedSwaggerDoc(file3, swaggerDoc3)
        val accumulated = SwaggerDocAccumulator.mergeAccumulators(Seq(accumulated1, accumulated2, accumulated3))
        val result = convertValidAccumulated(accumulated)

        result.paths.keys should contain theSameElementsAs(Seq("/path1", "/path2", "/newPath", "/newPath2"))
        result.paths("/path1") should equal(swaggerDoc1.paths("/path1"))
        result.paths("/path2") should equal(swaggerDoc1.paths("/path2"))
        result.paths("/newPath") should equal(swaggerDoc2.paths("/newPath"))
        result.paths("/newPath2") should equal(swaggerDoc3.paths("/newPath2"))
      }

      "flatten definitions" in new Setup {
        val accumulated1 = SwaggerDocAccumulator.createAccumulatedSwaggerDoc(file1, swaggerDoc1)
        val accumulated2 = SwaggerDocAccumulator.createAccumulatedSwaggerDoc(file2, swaggerDoc2)
        val accumulated3 = SwaggerDocAccumulator.createAccumulatedSwaggerDoc(file3, swaggerDoc3)
        val accumulated = SwaggerDocAccumulator.mergeAccumulators(Seq(accumulated1, accumulated2, accumulated3))
        val result = convertValidAccumulated(accumulated)

        result.definitions.keys should contain theSameElementsAs(Seq("definition1", "newDefinition"))
        result.definitions("definition1") should equal(swaggerDoc1.definitions("definition1"))
        result.definitions("newDefinition") should equal(swaggerDoc2.definitions("newDefinition"))
      }
    }
  }
}
