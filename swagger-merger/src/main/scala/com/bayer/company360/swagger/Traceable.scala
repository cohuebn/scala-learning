package com.bayer.company360.swagger

import java.io.File

case class Traceable[T](file: File, value: T)
