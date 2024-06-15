@import gg.jte.support.ForSupport
@import org.tnmk.git_analysis.analyze_effort.report.GitFoldersHtmlReporter
@import org.tnmk.git_analysis.analyze_effort.report.GitFoldersReportHelper
@import org.tnmk.git_analysis.analyze_effort.model.AliasMemberInManyRepos
@import org.tnmk.git_analysis.analyze_effort.model.AliasMemberInRepo
@import org.tnmk.git_analysis.analyze_effort.report.commit_per_day_chart.GitCommitPerDayChartHelper
@import org.tnmk.git_analysis.analyze_effort.report.commit_per_day_chart.model.CommitsInDay
@import org.tnmk.git_analysis.analyze_effort.report.commit_per_day_chart.model.PlotlyData
@import org.tnmk.tech_common.utils.FilePathUtils
@import java.time.LocalDateTime
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
  <script src="https://cdn.plot.ly/plotly-latest.min.js"></script>
</head>
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
    padding: 20px 20px 0 20px;
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
    background: #009496;
    color: #EFEFEF;
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
</style>
<span class="title">Repositories Analysis</span>
<span class="sub-title">from ${fromDateTime.format(reportDateTimeFormatter)} to ${toDateTime.format(reportDateTimeFormatter)}</span>

@for (AliasMemberInManyRepos member : members)
  !{var memberPullRequests = member.pullRequests();}
  <div class="pure-g title-lv1">
    <div class="pure-u-6-24">${member.getAliases().toString()}</div>
    <div class="pure-u-1-24"
         title="Words in commit (which could be different from changed words in PR); and it doesn't count the changes in the merge commit.">
      Words
    </div>
    <div class="pure-u-1-24"
         title="Lines in commit (which could be different from changed lines in PR); and it doesn't count the changes in the merge commit.">
      Lines
    </div>
    <div class="pure-u-1-24"
         title="Files in commit (which could be different from changed files in PR); and it doesn't count the changes in the merge commit.">
      Files
    </div>
    <div class="pure-u-1-24">PRs</div>
    <div class="pure-u-2-24">Words/PR</div>
    <div class="pure-u-2-24">Lines/PR</div>
    <div class="pure-u-2-24">Files/PR</div>
    <div class="pure-u-2-24">Commits</div>
    <div class="pure-u-2-24">Words/Commit</div>
    <div class="pure-u-2-24">Lines/Commit</div>
    <div class="pure-u-2-24">Files/Commit</div>
  </div>
  <div class="pure-g repo-report-item repo-report-item-main">
    <div class="pure-u-6-24" title="${getRepoNameFromMemberInRepo(member)}">
      Overall
      @if (member.getMemberInRepos().size() == 1)
        - ${GitFoldersReportHelper.getRepoName(member.getMemberInRepos().get(0).getRepoPath())}
      @endif
    </div>
    <div class="pure-u-1-24">${decimalFormat.format(member.totalWords())}</div>
    <div class="pure-u-1-24">${decimalFormat.format(member.totalLines())}</div>
    <div class="pure-u-1-24">${decimalFormat.format(member.totalFiles())}</div>
    <div class="pure-u-1-24">${memberPullRequests.size()}</div>
    <div class="pure-u-2-24">${"%.1f".formatted(avgWords(memberPullRequests))}</div>
    <div class="pure-u-2-24">${"%.1f".formatted(avgLines(memberPullRequests))}</div>
    <div class="pure-u-2-24">${"%.1f".formatted(avgFiles(memberPullRequests))}</div>
    <div class="pure-u-2-24">${decimalFormat.format(member.commitsSize())}</div>
    <div class="pure-u-2-24">${"%.1f".formatted(member.avgWordsPerCommit())}</div>
    <div class="pure-u-2-24">${"%.1f".formatted(member.avgLinesPerCommit())}</div>
    <div class="pure-u-2-24">${"%.1f".formatted(member.avgFilesPerCommit())}</div>
  </div>
  @if (member.getMemberInRepos().size() > 1)
    @for (AliasMemberInRepo repo : member.getMemberInRepos())
      !{var repoPullRequests = repo.getAliasMember().pullRequests();}
      <div class="pure-g repo-report-item">
        <div class="pure-u-6-24" title="${repo.getRepoPath()}">${GitFoldersReportHelper.getRepoName(repo.getRepoPath())}</div>
        <div class="pure-u-1-24">${decimalFormat.format(repo.getAliasMember().totalWords())}</div>
        <div class="pure-u-1-24">${decimalFormat.format(repo.getAliasMember().totalLines())}</div>
        <div class="pure-u-1-24">${decimalFormat.format(repo.getAliasMember().totalFiles())}</div>
        <div class="pure-u-1-24">${repoPullRequests.size()}</div>
        <div class="pure-u-2-24">${"%.1f".formatted(avgWords(repoPullRequests))}</div>
        <div class="pure-u-2-24">${"%.1f".formatted(avgLines(repoPullRequests))}</div>
        <div class="pure-u-2-24">${"%.1f".formatted(avgFiles(repoPullRequests))}</div>
        <div class="pure-u-2-24">${decimalFormat.format(repo.getAliasMember().commitsSize())}</div>
        <div class="pure-u-2-24">${"%.1f".formatted(repo.getAliasMember().avgWordsPerCommit())}</div>
        <div class="pure-u-2-24">${"%.1f".formatted(repo.getAliasMember().avgLinesPerCommit())}</div>
        <div class="pure-u-2-24">${"%.1f".formatted(repo.getAliasMember().avgFilesPerCommit())}</div>
      </div>
    @endfor
  @endif
  <details class="">
    <summary class="repo-report-item repo-report-item-main"
             title="Pull Requests: Just note that it's currently include PRs from 'dev' to 'master' branch. They would be excluded in the future.">
      Pull Requests
    </summary>
    <div class="pure-g file-item nested-1">
      <div class="pure-u-1-24">#</div>
      <div class="pure-u-7-24">Folder</div>
      <div class="pure-u-1-24">Words</div>
      <div class="pure-u-1-24">Lines</div>
      <div class="pure-u-1-24">Files</div>
      <div class="pure-u-6-24">CommitId</div>
      <div class="pure-u-3-24">DateTime</div>
      <div class="pure-u-4-24">Merged by</div>
    </div>
    <div class="files-container">
      @for (var commitEntry : ForSupport.of(GitFoldersReportHelper.sortPullRequestsByWords(member)))
        <div class="pure-g file-item nested-1">
          <div class="pure-u-1-24">${commitEntry.getIndex() + 1}</div>
          <div class="pure-u-7-24">${FilePathUtils.getLastPathPart(commitEntry.get().getRepoPath())}</div>
          <div class="pure-u-1-24">${decimalFormat.format(commitEntry.get().getWordsCount())}</div>
          <div class="pure-u-1-24">${decimalFormat.format(commitEntry.get().getLinesCount())}</div>
          <div class="pure-u-1-24">${decimalFormat.format(commitEntry.get().getFilesCount())}</div>
          <div class="pure-u-6-24">${commitEntry.get().getCommitRevision()}</div>
          <div class="pure-u-3-24">${commitDateTimeFormatter.format(commitEntry.get().getCommitDateTime())}</div>
          <div class="pure-u-4-24">${commitEntry.get().getCommitter()}</div>
        </div>
      @endfor
    </div>
  </details>
  <details class="" open>
    <summary class="repo-report-item repo-report-item-main">Top ${GitFoldersReportHelper.TOP_FILES_TO_REPORT_PER_MEMBER} committed files:</summary>
    <div class="pure-g file-item nested-1">
      <div class="pure-u-1-24">#</div>
      <div class="pure-u-1-24">Words</div>
      <div class="pure-u-1-24">Lines</div>
      <div class="pure-u-5-24">CommitId</div>
      <div class="pure-u-2-24">DateTime</div>
      <div class="pure-u-14-24">File</div>
    </div>
    <div class="files-container">
      @for (var fileEntry : ForSupport.of(GitFoldersReportHelper.sortCommitsByTotalWords(member)))
        <div class="pure-g file-item nested-1">
          <div class="pure-u-1-24">${fileEntry.getIndex() + 1}</div>
          <div class="pure-u-1-24">${decimalFormat.format(fileEntry.get().getChangedWords())}</div>
          <div class="pure-u-1-24">${decimalFormat.format(fileEntry.get().getChangedLines())}</div>
          <div class="pure-u-5-24">${fileEntry.get().getCommitRevision()}</div>
          <div class="pure-u-2-24">${commitDateTimeFormatter.format(fileEntry.get().getCommitDateTime())}</div>
          <div class="pure-u-14-24">${fileEntry.get().getNewPath()}</div>
        </div>
      @endfor
    </div>
  </details>
@endfor


@for(AliasMemberInManyRepos member : members)
  !{var memberKey = member.getMemberKey();}
  <div id="heatmap_${memberKey}"></div>
  !{ PlotlyData plotlyData = GitCommitPerDayChartHelper.getMemberCommitsEachDayChartData(fromDateTime, toDateTime, member);}

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
          [0.01, 'rgb(0,79,79)'],
          [0.4, 'rgb(0,101,101)'],
          [0.6, 'rgb(0,166,166)'],
          [0.8, 'rgb(0,232,232)'],
          [1, 'rgb(179,255,255)']
        ],
        zmin: 0,
        zmax: 30
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
      // width: 1920,
      height: 380,
      title: 'Commits of ${member.getAliases().toString()}',
      xaxis: {
        title: 'Date'
        // tickformat: '%Y-%m-%d'
      },
      yaxis: {
        title: 'Days of Week',
        // tickformat: '%a'
        tickvals: [1, 3, 5]
      },
      xgap: 5,
      ygap: 5,
      // annotations: annotations
    };

    Plotly.newPlot('heatmap_${memberKey}', data_${memberKey}, layout_${memberKey});
  </script>
@endfor
</body>
</html>
