package com.github.comfreek.gitseek.check

import org.eclipse.jgit.api.Git

class StatusCheck(git: Git, runConfig: RunConfig) extends Check {
  override def name: String = "Status Check"

  override def description: String = "Checks for untracked, changed/modified, removed/missing, and uncommitted files/folders"

  override def perform(): CheckResult = {
    val status = git.status().call()

    // formatter helper for legend
    def f(symbol: String, count: Int): String = if (count == 0) "" else (s"${symbol} ${count}")

    val legend = List(
      f("??", status.getUntracked.size() + status.getUntrackedFolders.size()),
      f("±", status.getChanged.size() + status.getModified.size()),
      f("--", status.getRemoved.size() + status.getMissing.size()),
      f("↯", status.getConflicting.size())
    ).filter(_.nonEmpty).mkString(" ") + " files"

    if (status.isClean) {
      CheckResult(this, success = true, List(s"✓ Clean working directory"))
    } else {
      CheckResult(this, success = false, List("✗ Dirty working directory: " + legend))
    }
  }
}