package org.tnmk.git_analysis.config;

import jakarta.annotation.Nullable;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.tnmk.tech_common.utils.DateTimeUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@ConfigurationProperties(prefix = "git-analysis.date-range")
@Setter
public class GitAnalysisDateRangeProperties {
  public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private String startDate;
  @Nullable
  private String endDate;

  public LocalDateTime getStartDate() {
    LocalDate localDate = LocalDate.parse(startDate, dateTimeFormatter);
    return localDate.atStartOfDay();
  }

  public LocalDateTime getEndDate() {
    if (StringUtils.isEmpty(endDate)) {
      return LocalDateTime.now();
    }
    LocalDate localDate = LocalDate.parse(endDate, dateTimeFormatter);

    // Get the end of the date.
    return DateTimeUtils.toEndOfDate(localDate);
  }
}
