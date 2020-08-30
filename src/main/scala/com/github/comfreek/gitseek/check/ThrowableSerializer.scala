package com.github.comfreek.gitseek.check

object ThrowableSerializer {
  def mkString(error: Throwable): String = {
    error.getMessage + "\n" + error.getStackTrace.map(_.toString).mkString("\n")
  }
}
