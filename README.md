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

# Note about the application logic

- The application will analyze based on the branch that is currently checked out. And then, it'll analyze the commits that
  happen before the last commit in that branch. Any commit that happen after that branch will not be analyzed.
- When you merge a branch into another branch, it basically will create a commit for that merge.
  And most of the time, you are not changing any code (except when resolving code conflict), so the application will consider that commit has 0
  changes.
  I know it's not really correct when you need to resolve the conflict, but that's for the future improvement.
- Changed words are calculated based on the updated or added words in the new code. 
  If you deleted some words and commit it, it will consider there's 0 changed words in that file. 
  I know it sounds weird, but imagine dev moved a line to another method, and that lines has 50 words. Should we count that he spend effort to change those 50 words? 
  I wouldn't count it like that because moving/deleting a lines is often much easier than editing/adding some new words. Of courses, it's not always easier, but it's like that most of the time.
  Anyway, I just try my best, calculating effort is complicated challenge that requires much more effort to improve it.
