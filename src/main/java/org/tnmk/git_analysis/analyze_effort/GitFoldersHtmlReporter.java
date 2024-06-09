package org.tnmk.git_analysis.analyze_effort;

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
import java.util.Collection;

@Service
public class GitFoldersHtmlReporter {
  private final TemplateEngine templateEngine;

  public GitFoldersHtmlReporter() {
    Path templateFolder = Paths.get("src/main/resources/report");
    this.templateEngine = TemplateEngine.create(new DirectoryCodeResolver(templateFolder), templateFolder, ContentType.Html);
  }

  public void report(Collection<AliasMemberInManyRepos> members) throws IOException {
    Path reportFilePath = Paths.get("report/analyze_effort.html");
    try (FileOutput output = new FileOutput(reportFilePath)) {
      templateEngine.render("analysis_effort.jte", members, output);
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
