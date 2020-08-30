package com.github.comfreek.gitseek.check

import java.io.File

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder

import scala.collection.mutable
import scala.util.Using

object GitRepoCheck {
  private def createDefaultChecksFor(git: Git, runConfig: RunConfig): Seq[Check] = List(
    new StatusCheck(git, runConfig),
    new StashCheck(git, runConfig),
    new RemotesExist(git, runConfig),
    new RemotesEnParCheck(git, runConfig)
  )
}

class GitRepoCheck(private val gitDir: File)(implicit runConfig: RunConfig) extends Check {
  override def name: String = s"Default checks for repo `${gitDir}`"

  override def description: String = s"Performs a set of default checks for repo `${gitDir}`" // TODO: make more precise

  private def runChecks(git: Git): CheckResult = {
    var success = true
    val messages = mutable.ListBuffer[String]()

    GitRepoCheck.createDefaultChecksFor(git, runConfig).foreach(check => {
      val result = check.perform()

      if (result.success) {
        messages += " ".repeat(4) + s"✓ ${check.name} succeeded"
      } else {
        success = false
        messages += " ".repeat(4) + s"✗ ${check.name} failed:"
      }

      messages ++= result.messages.map(" ".repeat(8) + _)
    })

    CheckResult(this, success, messages.toList)
  }

  override def perform(): CheckResult = {
    val thickLine = "=".repeat(50)
    val thinLine = "-".repeat(50)

    val messages = mutable.ListBuffer[String]()
    messages += thickLine
    messages += s"     Found Git repo at `$gitDir`"
    messages += thinLine

    if (runConfig.dryRun) {
      messages += "✗ Not checking due to dry run"

      return CheckResult(this, success = false, messages.toList)
    }

    val builder = new FileRepositoryBuilder

    Using(builder.setGitDir(gitDir)
      .readEnvironment
      .findGitDir // scan environment GIT_* variables
      .build // scan up the file system tree
    ) { repo =>
      Using(new Git(repo)) { git =>
        val overallResult = runChecks(git)

        messages ++= overallResult.messages
        messages += thinLine

        if (overallResult.success) {
          messages += s"✓ Repo ${gitDir} checked successfully!"
        } else {
          messages += s"✗ An error occurred while checking repo ${gitDir}, see above."
        }

        messages += thickLine

        CheckResult(this, overallResult.success, messages.toList)
      }.get // .get required to propagate swallowed exceptions
    }.get // .get required to propagate swallowed exceptions
  }
}
