package com.github.comfreek.gitseek.check

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.{GitAPIException, InvalidRemoteException, TransportException}
import org.eclipse.jgit.lib.BranchTrackingStatus
import org.eclipse.jgit.lib.SubmoduleConfig.FetchRecurseSubmodulesMode

import scala.collection.mutable
import scala.jdk.CollectionConverters._

class RemotesEnParCheck(git: Git, runConfig: RunConfig) extends Check {
  override def name: String = "Remotes-en-par Check"

  override def description: String = "Checks if all remotes and their branches are en-par " // TODO improve description, make more accurate

  override def perform(): CheckResult = {
    var success = true
    val messages = mutable.ListBuffer[String]()

    if (runConfig.mayFetch) {
      git.remoteList().call().asScala.foreach(remoteConfig => {
        remoteConfig.getFetchRefSpecs.asScala.foreach(refspec => {
          val fetchCommand = git.fetch()
            .setRemoveDeletedRefs(false)
            .setTimeout(5 /* seconds */)
            .setRefSpecs(refspec)
            .setRemote(remoteConfig.getName)
            .setRecurseSubmodules(FetchRecurseSubmodulesMode.ON_DEMAND)

          try {
            fetchCommand.call()
            // TODO: what about merge conflicts?
          } catch {
            case error@(_: GitAPIException | _: InvalidRemoteException | _: TransportException) =>
              success = false
              messages += s"✗ could not fetch `$refspec` from remote `${remoteConfig.getName}``"
              messages += ThrowableSerializer.mkString(error)
          }
        })
      })
    } else {
      success = false
      messages += "✗ Run config disallows fetching, hence en par check below will perform wrt. remote's branches from last fetch (possibly outdated)"
    }

    val branches = git.branchList().call().asScala
    for (branch <- branches) {
      Option(BranchTrackingStatus.of(git.getRepository, branch.getName)).map(status => {
        val msgPrefix = s"${branch.getName} (remotely tracked at ${status.getRemoteTrackingBranch})"

        (status.getBehindCount, status.getAheadCount) match {
          case (0, 0) =>
            messages += s"✓ $msgPrefix is even with remote"
          case (behind, ahead) =>
            success = false
            messages += s"✗ ${msgPrefix} is ${behind} commits behind and ${ahead} commits ahead"
        }
      }).getOrElse(() => {
        success = false
        s"✗ could not determine tracking status of ${branch.getName}"
      })
    }

    CheckResult(this, success, messages.toList)
  }
}
