@import gg.jte.support.ForSupport
@import org.apache.commons.lang3.StringUtils
@import org.tnmk.git_analysis.analyze_effort.report.GitFoldersHtmlReporter
@import org.tnmk.git_analysis.analyze_effort.report.GitFoldersReportHelper
@import org.tnmk.git_analysis.analyze_effort.model.AliasMemberInManyRepos
@import org.tnmk.git_analysis.analyze_effort.model.AliasMemberInRepo
@import org.tnmk.git_analysis.analyze_effort.report.contributions_per_day_chart.GitContributionsPerDayChartHelper
@import org.tnmk.git_analysis.analyze_effort.report.contributions_per_day_chart.GitContributionsPerDayChartHelper
@import org.tnmk.git_analysis.analyze_effort.report.contributions_per_day_chart.model.ContributionsInDay
@import org.tnmk.git_analysis.analyze_effort.report.contributions_per_day_chart.model.PlotlyData
@import org.tnmk.tech_common.utils.FilePathUtils
@import java.time.LocalDateTime
@import java.time.temporal.ChronoUnit
@import java.util.Arrays
@import java.util.List
@import java.util.stream.Collectors
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
<head>
  <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery.tablesorter/2.31.3/js/jquery.tablesorter.min.js"></script>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jquery.tablesorter/2.31.3/css/theme.default.min.css">
  <script src="https://cdn.plot.ly/plotly-latest.min.js"></script>
  <title>Repos Analysis: ${fromDateTime.format(reportDateTimeFormatter)} - ${toDateTime.format(reportDateTimeFormatter)}</title>
</head>
<body>
<style>
  body {
    background: rgba(0, 0, 0, 0.03);
  }

  summary {
    cursor: pointer;
  }

  header {
    display: flex;
    justify-content: space-between;
    padding: 5px 5px 0px 0px;
    background: rgba(0, 0, 0, 0.9);
  }

  .title {
    font-size: larger;
    padding: 0px 10px 0 10px;
    display: inline-block;
    /*font-weight: bold;*/
    color: #DDD;
  }

  .sub-title {
    font-size: smaller;
    /*color: rgba(0, 0, 0, 0.5);*/
    color: rgba(255, 255, 255, 0.5);
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
    background: #009496;
    color: rgba(255, 255, 255, 0.5);
  }

  .title-lv1 .member-name {
    /*font-weight: bold;*/
    color: rgba(255, 255, 255, 0.8);
  }

  .title-lv2 {
    font-weight: 600;
    font-size: 95%;
    padding: 5px;
  }

  .repo-report-item {
    font-size: 80%;
    padding: 5px 2px 2px 10px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.1);
    /*box-shadow: inset 0px 1px 0px 0px #FFF;*/
    color: #888;
  }

  .repo-report-item-main {
    color: #000;
    font-size: 90%;
    background: rgba(0, 0, 0, 0.05);
  }

  .files-container {
    max-height: 200px;
    overflow-y: auto;
    overflow-wrap: break-word;
  }

  /* SCROLLBAR FOR FILES-CONTAINER: BEGIN //////////////////////////////////////////////////////// */
  .files-container::-webkit-scrollbar {
    width: 10px;
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

  /* SCROLLBAR FOR FILES-CONTAINER: END //////////////////////////////////////////////////////// */

  .file-item {
    font-size: smaller;
    padding: 2px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.1);
    box-shadow: inset 0px 1px 0px 0px #FFF;
  }

  .commit-link:link {
    color: #005d5d;
  }

  .commit-link:hover {
    color: #009496;
  }

  .commit-link:visited {
    color: #445454;
  }

  .commit-link.test-file:link {
    color: #b18445;
  }

  .commit-link.test-file:hover {
    color: #d7a054;
  }

  .commit-link.test-file:visited {
    color: #6e6249;
  }

  .active-tab {
    display: block;
  }

  .inactive-tab {
    display: none;
  }

  .tab-header {
    cursor: pointer;
    padding: 5px 20px 10px 20px; /* Top has only 5px because active-header already has 5px border */
    border: none;
    background: none;
    color: rgba(255, 255, 255, 0.5);
  }

  .active-tab-header {
    color: rgba(0, 0, 0, 1);
    cursor: default;
    border-top: 5px solid #009496;
    background: #f6f6f6;
  }
</style>
<header>
  <div style="line-height: 30px;"> <!-- make content vertical center -->
    <span class="title">Repositories Analysis</span>
    <span class="sub-title">
    ${fromDateTime.format(reportDateTimeFormatter)} - ${toDateTime.format(reportDateTimeFormatter)} (${fromDateTime.until(toDateTime, ChronoUnit.WEEKS) + 1} weeks)
    </span>
  </div>
  <nav>
    <button id="btnCommitMetrics" onClick="showCommitsMetrics()" class="tab-header">Commit Metrics</button>
    <button id="btnContributionGraph" onClick="showContributionGraph()" class="tab-header">Daily Contribution Graph</button>
  </nav>
</header>
<section id="commits-metrics">
  @for (var memberEntry : ForSupport.of(members))
    !{
        var member = memberEntry.get();
//        var memberPullRequests = member.pullRequests();
    }
    <div class="pure-g title-lv1">
      <div class="pure-u-6-24 member-name">${memberEntry.getIndex()}. ${member.getAliases().stream().collect(Collectors.joining(", "))}</div>
      <div class="pure-u-2-24"
           title="Words in commits (which could be different from changed words in PR); and it doesn't count the changes in the merge commit.">
        Words
      </div>
      <div class="pure-u-2-24"
           title="Lines in commits (which could be different from changed lines in PR); and it doesn't count the changes in the merge commit.">
        Lines
      </div>
      <div class="pure-u-2-24"
           title="Files in commits (which could be different from changed files in PR); and it doesn't count the changes in the merge commit.">
        Files
      </div>

      <div class="pure-u-2-24">Commits</div>
      <div class="pure-u-2-24" title="Words/Commit">Words/Commit</div>
      <div class="pure-u-2-24" title="Lines/Commit">Lines/Commit</div>
      <div class="pure-u-2-24" title="Files/Commit">Files/Commit</div>

      <%--      <div class="pure-u-1-24">PRs</div>--%>
      <%--      <div class="pure-u-2-24">Words/PR</div>--%>
      <%--      <div class="pure-u-2-24">Lines/PR</div>--%>
      <%--      <div class="pure-u-2-24">Files/PR</div>--%>
      <div class="pure-u-2-24">Words Test</div>
      <div class="pure-u-2-24">Files Test</div>
    </div>
    <div class="pure-g repo-report-item repo-report-item-main">
      <div class="pure-u-6-24" title="${getRepoNameFromMemberInRepo(member)}">
        Overall
        @if (member.getMemberInRepos().size() == 1)
          - ${GitFoldersReportHelper.getRepoName(member.getMemberInRepos().get(0).getRepoPath())}
        @endif
      </div>
      <div class="pure-u-2-24">${decimalFormat.format(member.totalWords())}</div>
      <div class="pure-u-2-24">${decimalFormat.format(member.totalLines())}</div>
      <div class="pure-u-2-24">${decimalFormat.format(member.totalFiles())}</div>

      <div class="pure-u-2-24">${decimalFormat.format(member.commitsSize())}</div>
      <div class="pure-u-2-24">${"%.1f".formatted(member.avgWordsPerCommit())}</div>
      <div class="pure-u-2-24">${"%.1f".formatted(member.avgLinesPerCommit())}</div>
      <div class="pure-u-2-24">${"%.1f".formatted(member.avgFilesPerCommit())}</div>

      <div class="pure-u-2-24">${member.reportTestWords()}</div>
      <div class="pure-u-2-24">${member.reportTestFiles()}</div>
      <%--      <div class="pure-u-1-24">${memberPullRequests.size()}</div>--%>
      <%--      <div class="pure-u-2-24">${"%.1f".formatted(avgWords(memberPullRequests))}</div>--%>
      <%--      <div class="pure-u-2-24">${"%.1f".formatted(avgLines(memberPullRequests))}</div>--%>
      <%--      <div class="pure-u-2-24">${"%.1f".formatted(avgFiles(memberPullRequests))}</div>--%>
    </div>
    @if (member.getMemberInRepos().size() > 1)
      @for (AliasMemberInRepo repo : member.getMemberInRepos())
        !{//var repoPullRequests = repo.getAliasMember().pullRequests();}
        <div class="pure-g repo-report-item">
          <div class="pure-u-6-24" title="${repo.getRepoPath()}">${GitFoldersReportHelper.getRepoName(repo.getRepoPath())}</div>
          <div class="pure-u-2-24">${decimalFormat.format(repo.getAliasMember().totalWords())}</div>
          <div class="pure-u-2-24">${decimalFormat.format(repo.getAliasMember().totalLines())}</div>
          <div class="pure-u-2-24">${decimalFormat.format(repo.getAliasMember().totalFiles())}</div>

          <div class="pure-u-2-24">${decimalFormat.format(repo.getAliasMember().commitsSize())}</div>
          <div class="pure-u-2-24">${"%.1f".formatted(repo.getAliasMember().avgWordsPerCommit())}</div>
          <div class="pure-u-2-24">${"%.1f".formatted(repo.getAliasMember().avgLinesPerCommit())}</div>
          <div class="pure-u-2-24">${"%.1f".formatted(repo.getAliasMember().avgFilesPerCommit())}</div>

          <div class="pure-u-2-24">${repo.getAliasMember().reportTestWords()}</div>
          <div class="pure-u-2-24">${repo.getAliasMember().reportTestFiles()}</div>

          <%--          <div class="pure-u-1-24">${repoPullRequests.size()}</div>--%>
          <%--          <div class="pure-u-2-24">${"%.1f".formatted(avgWords(repoPullRequests))}</div>--%>
          <%--          <div class="pure-u-2-24">${"%.1f".formatted(avgLines(repoPullRequests))}</div>--%>
          <%--          <div class="pure-u-2-24">${"%.1f".formatted(avgFiles(repoPullRequests))}</div>--%>
        </div>
      @endfor
    @endif

    <!-- Show a list of PullRequests -->
    <%--    <details class="">--%>
    <%--      <summary class="repo-report-item repo-report-item-main"--%>
    <%--               title="Pull Requests: Despite efforts to exclude PRs from 'dev' to 'master', the lack of permission for BitBucket server integration resulted in some PRs not being successfully excluded, although most were.">--%>
    <%--        Pull Requests--%>
    <%--      </summary>--%>
    <%--      <div class="pure-g file-item nested-1">--%>
    <%--        <div class="pure-u-1-24">#</div>--%>
    <%--        <div class="pure-u-7-24">Repo</div>--%>
    <%--        <div class="pure-u-1-24">Words</div>--%>
    <%--        <div class="pure-u-1-24">Lines</div>--%>
    <%--        <div class="pure-u-1-24">Files</div>--%>
    <%--        <div class="pure-u-6-24">CommitId</div>--%>
    <%--        <div class="pure-u-3-24">DateTime</div>--%>
    <%--        <div class="pure-u-4-24">Merged by</div>--%>
    <%--      </div>--%>
    <%--      <div class="files-container">--%>
    <%--        @for (var commitEntry : ForSupport.of(GitFoldersReportHelper.sortPullRequestsByWords(member)))--%>
    <%--          <div class="pure-g file-item nested-1">--%>
    <%--            <div class="pure-u-1-24">${commitEntry.getIndex() + 1}</div>--%>
    <%--            <div class="pure-u-7-24">${commitEntry.get().getGitRepo().getRepoName()}</div>--%>
    <%--            <div class="pure-u-1-24">${decimalFormat.format(commitEntry.get().getWordsCount())}</div>--%>
    <%--            <div class="pure-u-1-24">${decimalFormat.format(commitEntry.get().getLinesCount())}</div>--%>
    <%--            <div class="pure-u-1-24">${decimalFormat.format(commitEntry.get().getFilesCount())}</div>--%>
    <%--            <div class="pure-u-6-24"><a href="${commitEntry.get().getCommitUrl()}" class="commit-link">${commitEntry.get().getCommitRevision()}</a>--%>
    <%--            </div>--%>
    <%--            <div class="pure-u-3-24">${commitDateTimeFormatter.format(commitEntry.get().getCommitDateTime())}</div>--%>
    <%--            <div class="pure-u-4-24">${commitEntry.get().getCommitter()}</div>--%>
    <%--          </div>--%>
    <%--        @endfor--%>
    <%--      </div>--%>
    <%--    </details>--%>
    <details class="" open>
      <summary class="repo-report-item repo-report-item-main">Top ${GitFoldersReportHelper.TOP_FILES_TO_REPORT_PER_MEMBER} committed files:</summary>
      <div class="pure-g file-item nested-1">
        <div class="pure-u-1-24">#</div>
        <div class="pure-u-1-24">Words</div>
        <div class="pure-u-1-24">Lines</div>
        <div class="pure-u-5-24">CommitId</div>
        <div class="pure-u-2-24">Ticket</div>
        <div class="pure-u-2-24">DateTime</div>
        <div class="pure-u-12-24">File</div>
      </div>
      <div class="files-container">
        @for (var fileEntry : ForSupport.of(GitFoldersReportHelper.sortCommitsByTotalWords(member)))
          !{String cssLink = "commit-link " + (fileEntry.get().isTestFile() ? "test-file" : "");}

          <div class="pure-g file-item nested-1" title="${fileEntry.get().isTestFile() ? "Test File" : ""}">
            <div class="pure-u-1-24">${fileEntry.getIndex() + 1}</div>
            <div class="pure-u-1-24">${decimalFormat.format(fileEntry.get().getChangedWords())}</div>
            <div class="pure-u-1-24">${decimalFormat.format(fileEntry.get().getChangedLines())}</div>
            <div class="pure-u-5-24">
              <a href="${fileEntry.get().getCommitUrl()}" class="${cssLink}">${fileEntry.get().getCommitRevision()}</a>
            </div>
            <div class="pure-u-2-24">
              @if (fileEntry.get().hasTicket())
                <a href="${fileEntry.get().getTicketUrl()}" class="${cssLink}">${fileEntry.get().getTicketId()}</a>
              @else
                No Ticket
              @endif
            </div>
            <div class="pure-u-2-24">${commitDateTimeFormatter.format(fileEntry.get().getCommitDateTime())}</div>
            <div class="pure-u-12-24">
              <a href="${fileEntry.get().getCommitFileUrl()}" class="${cssLink}">${fileEntry.get().getNewPath()}</a>
            </div>
          </div>
        @endfor
      </div>
    </details>

    <!-- Top tickets -->
    <details class="" open>
      !{ var commitTasks = member.commitTasks();}
      <summary class="repo-report-item repo-report-item-main">All tickets: ${commitTasks.size()}</summary>
      <div class="files-container nested-1">
        <table class="tablesorter">
          <thead>
          <tr>
            <th>#</th>
            <th>Ticket</th>
            <th>Words</th>
            <th>Lines</th>
            <th>Files</th>
            <th>First Commit</th>
            <th>Last Commit</th>
          </tr>
          </thead>
          <tbody>
          @for (var taskEntry : ForSupport.of(commitTasks))
            !{ var displayTicketId = taskEntry.get().isUnknownTicket() ? "No ticket" : taskEntry.get().getTicketId();}
            <tr title="${displayTicketId}">
              <td>${taskEntry.getIndex() + 1}</td>
              <td>
                @if (!taskEntry.get().isUnknownTicket())
                  <a href="${taskEntry.get().getTicketUrl()}" class="commit-link">${displayTicketId}</a>
                @else
                  ${displayTicketId}
                @endif
              </td>
              <td>${decimalFormat.format(taskEntry.get().getWords())}</td>
              <td>${decimalFormat.format(taskEntry.get().getLines())}</td>
              <td>${decimalFormat.format(taskEntry.get().getFiles())}</td>
              <td>${commitDateTimeFormatter.format(taskEntry.get().getFirstCommitDateTime())}</td>
              <td>${commitDateTimeFormatter.format(taskEntry.get().getLastCommitDateTime())}</td>
            </tr>
          @endfor
          </tbody>
        </table>
      </div>
    </details>
  @endfor
</section>


<section id="contribution-graph">
  @for(AliasMemberInManyRepos member : members)
    !{var memberKey = member.getMemberKey();}
    <div id="heatmap_${memberKey}" style="padding: 20px 0 0 20px;"></div>
    !{ PlotlyData plotlyData = GitContributionsPerDayChartHelper.getMemberContributionsEachDayChartData(fromDateTime, toDateTime, member);}

    <script>
      var x = [];
      @for(int i = 0; i < plotlyData.getX().size(); i++)
      x.push('${plotlyData.getX().get(i).format(GitFoldersHtmlReporter.chartDateTimeFormatter)}');
      @endfor

      var y = [];
      @for(int i = 0; i < plotlyData.getY().size(); i++)
      y.push('${plotlyData.getY().get(i)}');
      @endfor

      var texts = [];
      @for(int i = 0; i < plotlyData.getTexts().length; i++)
      var itext = [];
      @for(int j = 0; j < plotlyData.getTexts()[i].length; j++)
      itext.push('${plotlyData.getTexts()[i][j]}');
      @endfor
      texts.push(itext);
      @endfor

      var data_${memberKey} = [
        // {
        //   z: [[10, 20, 30, 40, 50], [20, 30, 40, 50, 60], [30, 40, 50, 60, 70], [40, 50, 60, 70, 80]],
        //   x: ['2023-01-01', '2023-01-02', '2023-01-03', '2023-01-04', '2023-01-05'],
        //   y: ['Mon', 'Tues', 'Weds', 'Thurs'],
        //   type: 'heatmap'
        // }
        {
          z: ${plotlyData.getZAsString()},
          x: x,
          y: y,
          type: 'heatmap',
          text: texts,
          hoverinfo: 'text',
          colorscale: [
            [0, 'rgb(35,35,35)'],
            [0.001, 'rgb(0,87,87)'],
            [0.3, 'rgb(1,155,155)'],
            [0.4, 'rgb(0,199,199)'],
            [0.5, 'rgb(0,255,255)'],
            [1, 'rgb(234,255,255)']
          ],
          zmin: 0,
          zmax: 10000
        }
      ];
      var annotations = [];
      var dataObj = data_${memberKey}[0];
      for (var i = 0; i < dataObj.y.length; i++) {
        for (var j = 0; j < dataObj.x.length; j++) {
          var currentValue = dataObj.z[i][j];
          var textColor = 'white'; // Optional: Set text color based on cell value

          var annotation = {
            xref: 'x1',
            yref: 'y1',
            x: dataObj.x[j],
            y: dataObj.y[i],
            text: '',//currentValue.toFixed(2),
            font: {
              family: 'Arial',
              size: 12,
              color: textColor
            },
            showarrow: false,
            align: 'center',
            xanchor: 'center',
            yanchor: 'middle',
            bordercolor: 'rgb(0,0,0)', // Border color
            borderwidth: 2, // Border width
            width: 20,
            height: 20
          };
          annotations.push(annotation);
        }
      }
      var layout_${memberKey} = {
        width: ${100 + 80 + (fromDateTime.until(toDateTime, ChronoUnit.WEEKS) + 1) * 27},
        height: 380,
        title: 'Changed words of ${member.getAliases().toString()}',
        xaxis: {
          // title: 'Date'
          // tickformat: '%Y-%m-%d'
        },
        yaxis: {
          title: '',
          // tickformat: '%a'
          // tickvals: [1, 3, 5]
        },
        xgap: 5,
        ygap: 5,
        // annotations: annotations
      };

      Plotly.newPlot('heatmap_${memberKey}', data_${memberKey}, layout_${memberKey});

      // Javascript on heatmap click event
      var heatmap = document.getElementById('heatmap_${memberKey}');

      heatmap.on('plotly_click', function (data) {
        var x = data.points[0].x;
        var y = data.points[0].y;
        var z = data.points[0].z;

        // Perform your action here
        console.log('You clicked on cell with coordinates: x=' + x + ', y=' + y + ', z=' + z);
      });
    </script>
  @endfor
</section>
<script>
  function showCommitsMetrics() {
    document.getElementById('commits-metrics').className = 'active-tab';
    document.getElementById('contribution-graph').className = 'inactive-tab';

    document.getElementById('btnCommitMetrics').classList.add('active-tab-header');
    document.getElementById('btnContributionGraph').classList.remove('active-tab-header');
  }

  function showContributionGraph() {
    document.getElementById('commits-metrics').className = 'inactive-tab';
    document.getElementById('contribution-graph').className = 'active-tab';

    document.getElementById('btnCommitMetrics').classList.remove('active-tab-header');
    document.getElementById('btnContributionGraph').classList.add('active-tab-header');
  }

  showCommitsMetrics();
</script>
<script>
  $(document).ready(function () {
    $(".tablesorter").tablesorter();
  });
</script>
</body>
</html>
