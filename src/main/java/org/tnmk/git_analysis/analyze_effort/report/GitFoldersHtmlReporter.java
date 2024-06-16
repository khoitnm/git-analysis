package org.tnmk.git_analysis.analyze_effort.report;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.FileOutput;
import gg.jte.resolve.DirectoryCodeResolver;
import org.springframework.stereotype.Service;
import org.tnmk.git_analysis.analyze_effort.model.AliasMemberInManyRepos;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class GitFoldersHtmlReporter {
  public static final DateTimeFormatter commitDateTimeFormatter = DateTimeFormatter.ofPattern("yy/MM/dd hh:mm a");
  public static final DateTimeFormatter reportDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MMM/dd hh:mm a");
  public static final DateTimeFormatter reportDateTimeInFileNameFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  public static final DateTimeFormatter chartDateTimeFormatter = DateTimeFormatter.ofPattern("yy-MM-dd");
  public static final DecimalFormat decimalFormat = new DecimalFormat("#,###");

  private final TemplateEngine templateEngine;

  public GitFoldersHtmlReporter() {
    Path templateFolder = Paths.get("src/main/resources/report");
    this.templateEngine = TemplateEngine.create(new DirectoryCodeResolver(templateFolder), templateFolder, ContentType.Html);
  }

  public void report(LocalDateTime startTimeToAnalyze, LocalDateTime endTimeToAnalyze, Collection<AliasMemberInManyRepos> members) throws IOException {
    List<AliasMemberInManyRepos> sortedMembers = GitFoldersReportHelper.sortMembersByTotalWords(members);
    String outputFilePath = "target/report/analyze_git_" + reportDateTimeInFileNameFormatter.format(startTimeToAnalyze) + "_" + reportDateTimeInFileNameFormatter.format(endTimeToAnalyze) + ".html";
    jteReport("analysis_effort.jte", outputFilePath,
      Map.of(
        "fromDateTime", startTimeToAnalyze,
        "toDateTime", endTimeToAnalyze,
        "members", sortedMembers)
    );
  }

  private <T> void jteReport(String templateFile, String outputPath, Map<String, Object> data) throws IOException {
    Path reportFilePath = Paths.get(outputPath);
    try (FileOutput output = new FileOutput(reportFilePath)) {
      templateEngine.render(templateFile, data, output);
    }
    openReportInBrowser(reportFilePath);
  }

  private void openReportInBrowser(Path htmlFilePath) {
    try {
      File htmlFile = htmlFilePath.toFile();
      // NOTE: This method works on Windows, I'm not sure about other OS.
      // We need to set this headless to 'false' to avoid error:
      // java.awt.HeadlessException: null
      //  at java.desktop/java.awt.Desktop.getDesktop(Desktop.java:302) ~[na:na]
      System.setProperty("java.awt.headless", "false");
      Desktop.getDesktop().browse(htmlFile.toURI());
    } catch (IOException e) {
      throw new RuntimeException("Failed to open report in browser", e);
    }
  }
}
