package com.github.comfreek.gitseek.util

import java.io.File

import scala.collection.mutable

object DirectoryWalker {
  /**
   * Finds all Git repos occurring arbitrarily deep under `root`.
   *
   * @param root     The root.
   * @param callback A function called for every found `.git` directory.
   *                 If there is a directory, say, `a/.git` and `a/b/.git` (e.g. from a submodule),
   *                 then the callback is called for both (not necessarily consecutively).
   */
  def walkGitRepos(root: File, callback: File => Unit): Unit = {
    val directories = mutable.Stack[File](root)

    while (directories.nonEmpty) {
      val dir = directories.pop()

      if (dir.getName == ".git") {
        callback(dir)
        // do not recurse into .git directories
        // even though, in theory, they might contain Git repos themselves
      } else {
        Option(dir.listFiles()).foreach(children => directories.pushAll(children))
      }
    }
  }
}
