@import gg.jte.support.ForSupport
@import org.tnmk.git_analysis.analyze_effort.GitFoldersReportHelper
@import org.tnmk.git_analysis.analyze_effort.model.AliasMemberInManyRepos
@import org.tnmk.git_analysis.analyze_effort.model.AliasMemberInRepo
@import java.time.LocalDateTime
@import java.util.List
@import static org.tnmk.git_analysis.analyze_effort.GitFoldersHtmlReporter.commitDateTimeFormatter
@import static org.tnmk.git_analysis.analyze_effort.GitFoldersHtmlReporter.reportDateTimeFormatter
@import static org.tnmk.git_analysis.analyze_effort.GitFoldersHtmlReporter.decimalFormat
@param List<AliasMemberInManyRepos> members
@param LocalDateTime fromDateTime
@param LocalDateTime toDateTime

<html lang="english">
<link rel="stylesheet" href="https://unpkg.com/purecss@2.0.6/build/pure-min.css">
<body>
<style>
  body {
    background: rgba(0, 0, 0, 0.03);
  }

  details {
    cursor: pointer;
  }

  .title {
    font-size: larger;
    padding: 20px 20px 0px 20px;
    display: inline-block;
  }

  .sub-title {
    font-size: smaller;
    padding: 20px;
  }

  .nested-1 {
    margin-left: 50px;
  }

  .nested-2 {
    margin-left: 100px;
  }

  .title-lv1 {
    margin-top: 20px;
    padding: 10px;
    font-weight: bold;
    background: #51859b;
    color: #EFEFEF;
  }

  .title-lv2 {
    font-weight: 600;
    font-size: 95%;
    padding: 5px;
  }

  .report-item {
    font-size: 90%;
  }

  .files-container {
    max-height: 200px;
    overflow-y: auto;
    overflow-wrap: break-word;
  }

  .files-container::-webkit-scrollbar {
    width: 7px;
  }

  .files-container::-webkit-scrollbar-track {
    background: #EEE;
  }

  .files-container::-webkit-scrollbar-thumb {
    background: #BBB;
  }

  .files-container::-webkit-scrollbar-thumb:hover {
    background: #555;
  }

  .file-item {
    font-size: smaller;
    padding: 2px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.1);
    box-shadow: inset 0px 1px 0px 0px #FFF;
  }
</style>
<span class="title">Repositories Analysis</span><span
  class="sub-title">from: ${fromDateTime.format(reportDateTimeFormatter)} to ${toDateTime.format(reportDateTimeFormatter)}</span>

@for (AliasMemberInManyRepos member : members)
  <div class="title-lv1">${member.getAliases().toString()}</div>
  <div class="nested-1 title-lv2">All repositories</div>
  <div class="pure-g nested-2 report-item">
    <div class="pure-u-1-8">Words: ${decimalFormat.format(member.totalWords())}</div>
    <div class="pure-u-1-8">Lines: ${decimalFormat.format(member.totalLines())}</div>
    <div class="pure-u-1-8">Files: ${decimalFormat.format(member.totalFiles())}</div>
    <div class="pure-u-1-8">Commits: ${decimalFormat.format(member.commitsSize())}</div>
    <div class="pure-u-1-8">Words/Commit: ${"%.1f".formatted(member.avgWordsPerCommit())}</div>
    <div class="pure-u-1-8">Lines/Commit: ${"%.1f".formatted(member.avgLinesPerCommit())}</div>
    <div class="pure-u-1-8">Files/Commit: ${"%.1f".formatted(member.avgFilesPerCommit())}</div>
  </div>
  @for (AliasMemberInRepo repo : member.getMemberInRepos())
    <div class="nested-1 title-lv2">${repo.getRepoPath()}</div>
    <div class="pure-g nested-2 report-item">
      <div class="pure-u-1-8">Words: ${decimalFormat.format(repo.getAliasMember().totalWords())}</div>
      <div class="pure-u-1-8">Lines: ${decimalFormat.format(repo.getAliasMember().totalLines())}</div>
      <div class="pure-u-1-8">Files: ${decimalFormat.format(repo.getAliasMember().totalFiles())}</div>
      <div class="pure-u-1-8">Commits: ${decimalFormat.format(repo.getAliasMember().commitsSize())}</div>
      <div class="pure-u-1-8">Words/Commit: ${"%.1f".formatted(repo.getAliasMember().avgWordsPerCommit())}</div>
      <div class="pure-u-1-8">Lines/Commit: ${"%.1f".formatted(repo.getAliasMember().avgLinesPerCommit())}</div>
      <div class="pure-u-1-8">Files/Commit: ${"%.1f".formatted(repo.getAliasMember().avgFilesPerCommit())}</div>
    </div>
  @endfor

  <details class="nested-1" open>
    <summary class="title-lv2">Top committed files:</summary>
    <div class="files-container">
      @for (var fileEntry : ForSupport.of(GitFoldersReportHelper.sortCommitsByTotalWords(member)))
        <div class="pure-g file-item nested-1">
          <div class="pure-u-1-24">${fileEntry.getIndex() + 1}</div>
          <div class="pure-u-2-24">Words: ${decimalFormat.format(fileEntry.get().getChangedWords())}</div>
          <div class="pure-u-2-24">Lines: ${decimalFormat.format(fileEntry.get().getChangedLines())}</div>
          <div class="pure-u-7-24">${fileEntry.get().getCommitRevision()}</div>
          <div class="pure-u-3-24">${commitDateTimeFormatter.format(fileEntry.get().getCommitDateTime())}</div>
          <div class="pure-u-9-24">${fileEntry.get().getNewPath()}</div>
        </div>
      @endfor
    </div>
  </details>
@endfor
</body>
</html>
