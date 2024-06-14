@import gg.jte.support.ForSupport
@import org.tnmk.git_analysis.analyze_effort.report.GitFoldersReportHelper
@import org.tnmk.git_analysis.analyze_effort.model.AliasMemberInManyRepos
@import org.tnmk.git_analysis.analyze_effort.model.AliasMemberInRepo
@import org.tnmk.tech_common.utils.FilePathUtils
@import java.time.LocalDateTime
@import java.util.List
@import static org.tnmk.git_analysis.analyze_effort.report.GitFoldersHtmlReporter.commitDateTimeFormatter
@import static org.tnmk.git_analysis.analyze_effort.report.GitFoldersHtmlReporter.reportDateTimeFormatter
@import static org.tnmk.git_analysis.analyze_effort.report.GitFoldersHtmlReporter.decimalFormat
@import static org.tnmk.git_analysis.analyze_effort.report.GitFoldersReportHelper.avgFiles
@import static org.tnmk.git_analysis.analyze_effort.report.GitFoldersReportHelper.avgLines
@import static org.tnmk.git_analysis.analyze_effort.report.GitFoldersReportHelper.avgWords
@import static org.tnmk.git_analysis.analyze_effort.report.GitFoldersReportHelper.getRepoNameFromMemberInRepo
@param List<AliasMemberInManyRepos> members
@param LocalDateTime fromDateTime
@param LocalDateTime toDateTime

<html lang="english">
<link rel="stylesheet" href="https://unpkg.com/purecss@2.0.6/build/pure-min.css">
<body>
<style>

</style>
<span class="title">Repositories Analysis</span>
<span class="sub-title">from ${fromDateTime.format(reportDateTimeFormatter)} to ${toDateTime.format(reportDateTimeFormatter)}</span>

@for (AliasMemberInManyRepos member : members)
  <h1>${member.getAliases().toString()}</h1>
@endfor
</body>
</html>
