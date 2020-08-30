package com.github.comfreek.gitseek.check

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.{GitAPIException, InvalidRemoteException, TransportException}
import org.eclipse.jgit.transport.URIish

import scala.collection.mutable
import scala.jdk.CollectionConverters._

class RemotesExist(git: Git, runConfig: RunConfig) extends Check {
  override def name: String = "RemotesExist Check"

  override def description: String = "Checks that all remotes point to accessible Git repositories (without making any changes to the local repo, not even fetching)"

  override def perform(): CheckResult = {
    var success = true
    val messages = mutable.ListBuffer[String]()

    val remotes = git.remoteList().call().asScala
    if (remotes.isEmpty) {
      messages += "No remotes, nothing to do"
    }

    for (remote <- remotes) {
      val uris = remote.getURIs.asScala.toList
      if (uris.isEmpty) {
        messages += s"No URIs for remote `${remote.getName}`, nothing to do here"
      }

      for (uri <- uris) {
        try {
          Git.lsRemoteRepository()
            .setRemote(uri.toASCIIString)
            .setHeads(false)
            .setTags(false)
            .call()

          messages += s"✓ Checked URI `${uri}` associated to remote `${remote.getName}`"
        } catch {
          case error @ (_: GitAPIException | _: InvalidRemoteException | _: TransportException) =>
            success = false
            messages += s"✗ Failed to check URI `${uri}` associated to remote `${remote.getName}`"
            messages += ThrowableSerializer.mkString(error)
        }
      }
    }

    CheckResult(this, success, messages.toList)
  }
}
