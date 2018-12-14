package com.bayer.company360.swagger

import scaldi.Module

object AppInjector {
  implicit val injector = new Module {
    bind [SwaggerConverter] to new SwaggerConverter
    bind [SwaggerMerger] to injected [SwaggerMerger]
  }
}