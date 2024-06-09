@import org.tnmk.git_analysis.analyze_effort.model.AliasMemberInManyRepos
@import java.util.List
@param List<AliasMemberInManyRepos> members
<html lang="english">
<link rel="stylesheet" href="https://unpkg.com/purecss@2.0.6/build/pure-min.css">
<body>
<h1>Members' Efforts</h1>
@for (AliasMemberInManyRepos member : members)
  <div>

  </div>
  <h2>${member.getAliases().toString()}</h2>
  <p>Total Words: ${member.totalWords()}</p>
  <p>Total Lines: ${member.totalLines()}</p>
  <p>Total Files: ${member.totalFiles()}</p>
  <p>Commits: ${member.commitsSize()}</p>
  <p>Words/Commit: ${member.avgWordsPerCommit()}</p>
  <p>Lines/Commit: ${member.avgLinesPerCommit()}</p>
  <p>Files/Commit: ${member.avgFilesPerCommit()}</p>
@endfor
</body>
</html>
