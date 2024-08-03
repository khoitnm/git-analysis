# git-analysis

# Note about reading analysis numbers.

I think everybody already aware of these things, but I still want to emphasize them again:

1. Person A has less code (words) than person B doesn't mean that person A is less productive than person B.
   - Maybe person A has to tackle more complex problems than person B.
   - Or maybe person A spend more time to do code review, to do research (which is written into document, not code), or spend more time to help other
   members.
   - Or maybe person A can write simpler or shorter code than person B.
   - Or person B may be actually just renaming some methods, hence he changes code in many places, but the productivity may actually lower than person A.

2. The same lines of code doesn't mean the same amount of code: For example: code on Front-End has limit 80 characters per line, while Back-End may
   have 150 characters per line.
   So, with the same amount of code, the Back-End will have fewer lines.

3. But there's one interesting note about the simplicity of the code:
   When a developer wants to make their code simpler, they might need to tweak it several times. 
   This can lead to more commits being made over time, even if the final pull request that gets merged looks like it has fewer changes compared to unrefined codes.
   That’s why looking at all the changes made in all commits, not just the final pull request, can give us a better picture of the effort a developer puts into improving the code quality.
   And I want to emphasize again: __it's just show the effort, not the efficiency or productivity__ 
   because an excellent developer may just write a very simple solution for a complicated problem in the first place and his totally codes will be smaller.

# Note about the application logic

- The application will analyze based on the branch that is currently checked out. And then, it'll analyze the commits that
  happen before the last commit in that branch. Any commit that happen after that branch will not be analyzed.
- Merging one branch into another typically results in a merge commit. 
  Generally, this does not involve code changes (except when resolving conflicts), so the application considers such commits to have zero changes. 
  While this may not accurately reflect the effort involved in resolving conflicts, it is a consideration for future enhancement.
- Changed words are calculated based on the updated or added words in the new code. 
  If you deleted some words and commit it, it will consider there's 0 changed words in that file.
  You might think it’s odd, but imagine a developer removed one method which has 50 words. Should we count that he spend effort to change those 50 words? 
  I wouldn't, because deleting codes is usually easier than editing/adding them.
  Sure, sometimes it’s not that simple, but most of the time it is.
  I know it's not perfect, but calculating effort is complicated challenge that requires much sophisticated effort to improve it.
