package com.github.comfreek.gitseek.check

sealed case class CheckResult(check: Check, success: Boolean, messages: List[String])
