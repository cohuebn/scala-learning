addSbtPlugin("com.bayer.company360" % "swagger-merger" % "0.0.1-SNAPSHOT")

//sys.props.get("plugin.version") match {
//  case Some(pluginVersion) => {
//    val pluginLocation = "com.bayer.company360" % "swagger-merger" % pluginVersion
//    addSbtPlugin(pluginLocation)
//    libraryDependencies += pluginLocation
//  }
//  case _ => sys.error("""|The system property 'plugin.version' is not defined.
//                         |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
//}