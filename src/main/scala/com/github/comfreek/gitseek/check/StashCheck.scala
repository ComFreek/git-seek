package com.github.comfreek.gitseek.check

import org.eclipse.jgit.api.Git

class StashCheck(git: Git, runConfig: RunConfig) extends Check {
  override def name: String = "Stash Check"

  override def description: String = "Verifies that the stash is empty, and hence cannot possibly contain important left-over work"

  override def perform(): CheckResult = {
    git.stashList().call().size() match {
      case 0 =>
        CheckResult(this, success = true, List(s"✓ Empty stash"))
      case n =>
        CheckResult(this, success = false, List(s"✗ Stash contains $n ${if (n == 1) "entry" else "entries"}"))
    }
  }
}