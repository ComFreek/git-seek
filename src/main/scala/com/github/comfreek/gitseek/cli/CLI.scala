package com.github.comfreek.gitseek.cli

import scopt.OParser
import java.io.File

import com.github.comfreek.gitseek.ProgramInfo
import com.github.comfreek.gitseek.check.{GitRepoCheck, RunConfig}
import com.github.comfreek.gitseek.util.DirectoryWalker
import com.github.comfreek.gitseek.check.GitRepoCheck

case class CLIConfig(
                      dir: File = new File("."),
                      runConfig: RunConfig = RunConfig()
                 )
object CLI {
  private val builder = OParser.builder[CLIConfig]
  private val parser = {
    import builder._

    OParser.sequence(
      programName(ProgramInfo.executableName),
      head(ProgramInfo.programName, ProgramInfo.version),

      help('h', "help")
        .text("print this help message"),

      opt[Unit]('n', "dry-run")
        .optional()
        .action((_, c) => c.copy(runConfig = c.runConfig.copy(dryRun = true)))
        .text("only search and list Git repositories under <dir>; do not do anything else"),

      opt[Unit]('f', "may-fetch")
        .optional()
        .action((_, c) => c.copy(runConfig = c.runConfig.copy(mayFetch = true)))
        .text("allow running `git fetch` to assess whether local clone is commits behind/ahead of remote"),

      arg[File]("<dir>")
        .unbounded()
        .required()
        .action((d, c) => c.copy(dir = d))
        .text("directory to recursively search for Git repositories")
    )
  }

  def main(args: Array[String]): Unit = {
    OParser.parse(parser, args, CLIConfig()) match {
      case Some(config) =>
        var foundRepos = 0
        var failures = 0

        DirectoryWalker.walkGitRepos(config.dir, gitDir => {
          foundRepos += 1
          val repoResult = new GitRepoCheck(gitDir)(config.runConfig).perform()
          repoResult.messages.foreach(println)

          if (!repoResult.success) {
            failures += 1
          }
        })

        println("\n\n")

        if (failures == 0) {
          println(s"✓ Checked ${foundRepos}/${foundRepos} repositories successfully!")
          sys.exit(0)
        } else {
          println(s"✗ ${failures}/${foundRepos} repositories failed checking.")
          sys.exit(1)
        }
      case None =>
        // OParser.parse already outputs the help text should this case occur.
        sys.exit(1)
    }
  }
}
