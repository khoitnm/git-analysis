package org.tnmk.tech_common.utils;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;

public class PathMatcherUtils {
  public static PathMatcher getPathMatcher(String pattern) {
    FileSystem fileSystem = FileSystems.getDefault();
    return fileSystem.getPathMatcher(pattern);
  }

  public static boolean match(String pathAsString, String pattern) {
    PathMatcher pathMatcher = getPathMatcher(pattern);
    Path path = Path.of(pathAsString);
    boolean result = pathMatcher.matches(path);
    return result;
  }

  public static boolean matchAnyPattern(String pathAsString, List<String> patterns) {
    return patterns.stream().anyMatch(pattern -> match(pathAsString, pattern));
  }
}
