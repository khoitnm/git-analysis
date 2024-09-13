package org.tnmk.git_analysis.analyze_effort.model;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.tnmk.git_analysis.analyze_effort.CheckTestFilePolicy;
import org.tnmk.git_analysis.analyze_effort.GitCommitHelper;
import org.tnmk.git_analysis.analyze_effort.GitCommitTicketHelper;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommitFile {
  @NonNull
  private final GitRepo gitRepo;
  /**
   * If the file is deleted, the newPath will be the path of the deleted file.
   */
  @NonNull
  private final String newPath;

  @NonNull
  private final String newFileId;
  @NonNull
  private final int changedLines;
  @NonNull
  private final int changedWords;
  @NonNull
  private final String commitRevision;
  @NonNull
  private final LocalDateTime commitDateTime;

  /**
   * A commit may not have any information about the ticket.
   */
  @Nullable
  private final String ticketId;

  public boolean hasTicket() {
    return StringUtils.isNotBlank(ticketId);
  }

  public String getTicketUrl() {
    return GitCommitTicketHelper.getTicketUrl(gitRepo, ticketId);
  }

  public String getCommitUrl() {
    return GitCommitHelper.getCommitUrl(gitRepo, commitRevision);
  }

  public String getCommitFileUrl() {
    if (gitRepo.getServiceType() == GitServiceType.BITBUCKET) {
      return getCommitUrl() + "#" + newPath;
    } else if (gitRepo.getServiceType() == GitServiceType.GITHUB) {
      // Example: https://github.com/spring-cloud-samples/spring-cloud-gateway-sample/pull/13/commits/f317b4f253ed19388011f956b26c4998cd5557d5#diff-27b682764e88c985aff6db25435408147788fe280aafd3ec6213ffd3c7d90463
      return getCommitUrl() + "#diff-" + newFileId;
    } else {
      throw new IllegalArgumentException("Unsupported service type: " + gitRepo.getServiceType());
    }
  }

  public boolean isTestFile() {
    return CheckTestFilePolicy.isTestFile(newPath);
  }
}
