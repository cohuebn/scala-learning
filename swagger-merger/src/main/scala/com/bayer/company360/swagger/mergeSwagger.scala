package com.bayer.company360.swagger

import sbt._, Keys._

object mergeSwagger extends AutoPlugin {
  override lazy val projectSettings = Seq(
    includeFilter := "*.yaml"
  )
}
