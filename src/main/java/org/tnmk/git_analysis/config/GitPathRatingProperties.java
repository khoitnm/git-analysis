package org.tnmk.git_analysis.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GitPathRatingProperties {
  private String pathPattern;
  /**
   * This is the rate of how much effort a developer need to create a file that satisfy such pathPattern.
   * For example:
   * - For *.dtsx, it's generated from some tools, so the required effort is not that much, and effortRate will be low.
   */
  private double effortRate;
  private double effortFixed;
}
