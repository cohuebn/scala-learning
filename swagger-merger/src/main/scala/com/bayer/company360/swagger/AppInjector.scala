package com.bayer.company360.swagger

import scaldi.Module

object AppInjector {
  implicit val injector = new Module {
    bind [SwaggerFileParser] to new SwaggerFileParser
    bind [SwaggerMerger] to injected [SwaggerMerger]
  }
}