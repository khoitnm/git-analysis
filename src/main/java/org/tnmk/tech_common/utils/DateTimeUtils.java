package org.tnmk.tech_common.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateTimeUtils {
  public static LocalDateTime toEndOfDate(LocalDate localDate) {
    LocalDateTime result = localDate.plusDays(1).atStartOfDay().minusSeconds(1);
    return result;
  }
}
