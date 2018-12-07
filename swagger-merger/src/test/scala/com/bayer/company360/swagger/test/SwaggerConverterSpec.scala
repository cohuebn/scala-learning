package com.bayer.company360.swagger.test

import com.bayer.company360.swagger.SwaggerConverter
import com.bayer.company360.swagger.SwaggerSchema.PathName

class SwaggerConverterSpec extends Spec {
  var swaggerConverter: SwaggerConverter = _

  override def beforeEach(): Unit = {
    super.beforeEach()
    swaggerConverter = new SwaggerConverter
  }

  "A swagger converter" when {
    "swagger fields are populated" should {
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
        val expected = Seq("application/json", "image/png", "image/gif", "image/jpeg")
        parsed.produces should contain theSameElementsInOrderAs expected
      }

      "parse all paths" in {
        val parsed = swaggerConverter.parse(allFieldsPopulatedTestCase)
        val expected = Set[PathName]("/wearing-hats", "/search-the-interwebs")
        parsed.paths.keys should contain theSameElementsAs expected
      }

      "parse a 'get' path" in {
        val parsed = swaggerConverter.parse(allFieldsPopulatedTestCase)

        assertOnOption(parsed.paths.get("/wearing-hats")) { path =>
          path.get should not be None
          path.post shouldBe None

          assertOnOption(path.get) { getOperation =>
            getOperation.summary should equal(Option("Cats in hats"))
            getOperation.operationId should equal(Option("cats-in-hats"))
            getOperation.description should equal(Option("You should understand this endpoint by now"))
            assertOnOption(getOperation.tags) { tags =>
              tags should contain theSameElementsInOrderAs (Seq("kitties", "outerwear"))
            }
            getOperation.parameters.map(_.name) should contain theSameElementsInOrderAs (Seq("hat-category", "limit", "offset"))

            val parameterByName = (name: String) => getOperation.parameters.find(_.name == name)
            assertOnOption(parameterByName("hat-category")) { parameter =>
              parameter.in should equal("query")
              parameter.description should equal(Option("Only show pictures from the given hat categories"))
              parameter.required should equal(Option(true))
              parameter.`type` should equal(Option("array"))
              parameter.`x-example` should equal(Option("hat-category=flowered&hat-category=fireman"))
              assertOnOption(parameter.items) { items =>
                items.`type` should equal(Some("string"))
                items.format should equal(Some("alpha_1"))
              }
            }

            assertOnOption(parameterByName("limit")) { parameter =>
              parameter.in should equal("query")
              parameter.description should equal(Option("Limit of results returned. Defaults to 25."))
              parameter.required should equal(Option(false))
              parameter.`type` should equal(Option("integer"))
              parameter.format should equal(Option("int32"))
            }

            assertOnOption(parameterByName("offset")) { parameter =>
              parameter.in should equal("query")
              parameter.description should equal(Option("Offset of returned results. Defaults to 0."))
              parameter.required should equal(Option(false))
              parameter.`type` should equal(Option("integer"))
              parameter.format should equal(Option("int32"))
            }

            getOperation.responses.keys should contain theSameElementsAs (Seq(200, 400, 429, 500))
            val responseByStatus = (status: Int) => getOperation.responses.get(status)
            assertOnOption(responseByStatus(200)) { response =>
              response.description should equal(Option("A list of matching cats-in-hats images"))
              response.schema.`type` should equal(Option("object"))
              assertOnOption(response.schema.properties) { responseProperties =>
                assertOnOption(responseProperties.get("items")) { items =>
                  items.`type` should equal(Some("array"))
                  items.items.get.`$ref` should equal(Some("#/definitions/catListing"))
                }

                assertOnOption(responseProperties.get("limit")) { limit =>
                  limit.`type` should equal(Some("integer"))
                }
                assertOnOption(responseProperties.get("offset")) { limit =>
                  limit.`type` should equal(Some("integer"))
                }
              }
            }

            assertOnOption(responseByStatus(400)) { response =>
              response.description should equal(Option("Missing or invalid query parameter(s)"))
              response.schema.`$ref` should equal(Option("#/definitions/error"))
            }

            assertOnOption(responseByStatus(429)) { response =>
              response.description should equal(Option("Downstream connection limit exceeded"))
              response.schema.`$ref` should equal(Option("#/definitions/error"))
            }

            assertOnOption(responseByStatus(500)) { response =>
              response.description should equal(Option("Internal server error"))
              response.schema.`$ref` should equal(Option("#/definitions/error"))
            }
          }
        }
      }

      "parse a 'post' path" in {
        val parsed = swaggerConverter.parse(allFieldsPopulatedTestCase)

        assertOnOption(parsed.paths.get("/search-the-interwebs")) { path =>
          path.get should be(None)
          path.post should not be None

          assertOnOption(path.post) { postOperation =>
            postOperation.summary should equal(Option("search the whole internet for matching pics"))
            postOperation.description should equal(Option("impressive feat of technology"))
            postOperation.operationId should equal(Option("search-the-interwebs"))
            assertOnOption(postOperation.tags) { tags =>
              tags should contain theSameElementsInOrderAs (Seq("al gore", "kitties"))
            }
            postOperation.parameters.map(_.name) should contain theSameElementsInOrderAs (Seq("search", "limit"))

            val parameterByName = (name: String) => postOperation.parameters.find(_.name == name)
            assertOnOption(parameterByName("search")) { parameter =>
              parameter.in should equal("body")
              parameter.description should equal(Option("The search criteria"))
              assertOnOption(parameter.schema) { schema =>
                schema.`$ref` should equal(Some("#/definitions/searchCriteria"))
              }
            }

            assertOnOption(parameterByName("limit")) { parameter =>
              parameter.in should equal("query")
              parameter.description should equal(Option("Limit of results returned. Defaults to 25."))
              parameter.required should equal(Option(false))
              parameter.`type` should equal(Option("integer"))
              parameter.format should equal(Option("int32"))
            }

            postOperation.responses.keys should contain theSameElementsAs (Seq(200, 500))
            val responseByStatus = (status: Int) => postOperation.responses.get(status)
            assertOnOption(responseByStatus(200)) { response =>
              response.description should equal(Option("A list of matching cats images"))
              response.schema.`type` should equal(Option("object"))
              assertOnOption(response.schema.properties) { responseProperties =>
                assertOnOption(responseProperties.get("items")) { items =>
                  items.`type` should equal(Some("array"))
                  items.items.get.`$ref` should equal(Some("#/definitions/catListing"))
                }

                assertOnOption(responseProperties.get("limit")) { limit =>
                  limit.`type` should equal(Some("integer"))
                }
              }
            }

            assertOnOption(responseByStatus(500)) { response =>
              response.description should equal(Option("Internal server error"))
              response.schema.`$ref` should equal(Option("#/definitions/error"))
            }
          }
        }
      }

      "parse definitions" in {
        val parsed = swaggerConverter.parse(allFieldsPopulatedTestCase)

        parsed.definitions.keys should contain theSameElementsAs(Seq("catListing", "error"))

        val definitionByName = (name: String) => parsed.definitions.get(name)
        assertOnOption(definitionByName("catListing")) { definition =>
          definition.`type` should equal(Some("object"))
          assertOnOption(definition.properties) { properties =>
            properties.keys should contain theSameElementsAs(Seq("url", "name", "views"))

            assertOnOption(properties.get("url")) { url => {
              url.`type` should equal(Some("string"))
              url.description should equal(Some("The url for the cat pic"))
            }}

            assertOnOption(properties.get("name")) { url => {
              url.`type` should equal(Some("string"))
              url.description should equal(Some("The name of the cat"))
            }}

            assertOnOption(properties.get("views")) { url => {
              url.`type` should equal(Some("number"))
              url.description should equal(Some("The number of times this picture has been viewed"))
            }}
          }
        }

        assertOnOption(definitionByName("error")) { definition =>
          definition.`type` should equal(Some("object"))
          assertOnOption(definition.properties) { properties =>
            properties.keys should contain theSameElementsAs(Seq("status", "code", "errorMessage"))

            assertOnOption(properties.get("status")) { url => {
              url.`type` should equal(Some("string"))
            }}

            assertOnOption(properties.get("code")) { url => {
              url.`type` should equal(Some("integer"))
              url.format should equal(Some("int32"))
            }}

            assertOnOption(properties.get("errorMessage")) { url => {
              url.`type` should equal(Some("string"))
            }}
          }
        }
      }
    }
  }
}
