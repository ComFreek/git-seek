# git-seek(1): seek for Git repos and report on their dirtyness

**Do you have tens of Git clones lying around and find yourself frequently switching between them? Did you ever become distracted while doing changes and later forget about them?**
*Then*, this tool might be for you: it recursively searches for repos on your drive and checks them for the following conditions:

- a clean working directory (= empty `git status`)
- an empty stash (= empty `git stash list`)
- the existence of all remotes (≈ that you can still clone from them)
- the up-do-dateness of all branches wrt. their remote branches (≈ `git fetch && git status` saying "your branch is up to date with '...'")

git-seek has been **built with robustness in mind**.
It uses the dedicated [Eclipse's JGit library](https://www.eclipse.org/jgit/) to interfere with Git repos. No brittle parsing of `git` command outputs is done.
The performed actions are read-only *by default*.
Only with `--may-fetch`, it fetches updates, but even then it does absolutely *no merging*.

## Example

```bash
$> git-seek --may-fetch C:\Users\ComFreek\Desktop

==================================================
     Found Git repo at `C:\Users\ComFreek\Desktop\repo1\.git`
--------------------------------------------------
    ✓ Status Check succeeded
        ✓ Clean working directory
    ✓ Stash Check succeeded
        ✓ Empty stash
    ✓ RemotesExist Check succeeded
        ✓ Checked URI `https://github.com/ComFreek/repo1.git` associated to remote `origin`
    ✓ Remotes-en-par Check succeeded
        ✓ refs/heads/devel (remotely tracked at refs/remotes/origin/devel) is even with remote
        ✓ refs/heads/master (remotely tracked at refs/remotes/origin/master) is even with remote
--------------------------------------------------
✓ Repo C:\Users\ComFreek\Desktop\repo1\.git checked successfully!
==================================================

==================================================
     Found Git repo at `C:\Users\ComFreek\Desktop\repo2\.git`
--------------------------------------------------
    ✓ Status Check succeeded
        ✓ Clean working directory
    ✓ Stash Check succeeded
        ✓ Empty stash
    ✓ RemotesExist Check succeeded
        ✓ Checked URI `git@github.com:ComFreek/repo2.git` associated to remote `origin`
    ✗ Remotes-en-par Check failed:
        ✗ refs/heads/devel (remotely tracked at refs/remotes/origin/devel) is 4 commits behind and 0 commits ahead
--------------------------------------------------
✗ An error occurred while checking repo C:\Users\ComFreek\repo2\.git, see above.
==================================================


✗ 1/2 repositories failed checking.
```

## Usage

```
$> git-seek --help

git-seek 0.0.1
Usage: git-seek [options] <dir>

  -h, --help       print this help message
  -n, --dry-run    only search and list Git repositories under <dir>; do not do anything else
  -f, --may-fetch  allow running `git fetch` to assess whether local clone is commits behind/ahead of remote
  <dir>            directory to recursively search for Git repositories
```