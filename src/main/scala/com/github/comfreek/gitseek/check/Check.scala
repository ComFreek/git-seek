package com.github.comfreek.gitseek.check

import org.eclipse.jgit.api.Git

trait Check {
  def name: String

  def description: String

  def perform(): CheckResult
}

// TODO: needed?
abstract class ConcreteCheck(implicit git: Git, runConfig: RunConfig) extends Check
