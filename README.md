# git-seek

**CLI tool to seek your hard drive for Git repos and check their dirtyness**

```bash
$> git-seek C:\Users\ComFreek\Desktop


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
     Found Git repo at `C:\Users\ComFreek\Desktop\repo1\.git`
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
✗ An error occurred while checking repo C:\Users\ComFreek\repo1\.git, see above.
==================================================


✗ 1/2 repositories failed checking.
```