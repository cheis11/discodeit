package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.config.DiscodeitProperties;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(DiscodeitProperties.class)
public class DiscodeitApplication {

  // 파일 및 서브디렉토리를 재귀적으로 삭제하는 메서드
  private static void deleteDirectoryContents(File directory) {
    if (directory.exists() && directory.isDirectory()) {
      File[] files = directory.listFiles();
      if (files != null) {
        for (File file : files) {
          if (file.isDirectory()) {
            deleteDirectoryContents(file);
          }
          if (file.delete()) {
            System.out.println("deleted: " + file.getAbsolutePath());
          } else {
            System.err.println("delete failed: " + file.getAbsolutePath());
          }
        }
      }
    }
  }

  public static void clearDataFiles() {
    File dataDir = new File(".discodeit"); // 최상위 "data" 디렉토리를 대상으로 합니다.

    if (dataDir.exists() && dataDir.isDirectory()) {
      System.out.println(dataDir.getPath() + "' delete contents...");
      deleteDirectoryContents(dataDir); // 모든 내용을 재귀적으로 삭제

      if (dataDir.delete()) { // 이제 비어있는 "data" 디렉토리 자체를 삭제
        System.out.println("'" + dataDir.getPath() + "' directory deleted");
      } else {
        System.err.println("'" + dataDir.getPath() + "' directory not deleted");
      }
    }

    if (!dataDir.exists()) {
      if (dataDir.mkdir()) {
        System.out.println("'" + dataDir.getPath() + "' directory created");
      } else {
        System.err.println("'" + dataDir.getPath() + "' directory not created");
      }
    }
  }

  public static void main(String[] args) {
    clearDataFiles();
    SpringApplication app = new SpringApplication(DiscodeitApplication.class);
  }
}