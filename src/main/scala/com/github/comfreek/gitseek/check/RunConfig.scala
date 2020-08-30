package com.github.comfreek.gitseek.check

sealed case class RunConfig(
                             dryRun: Boolean = false,
                             mayFetch: Boolean = false
                           )
