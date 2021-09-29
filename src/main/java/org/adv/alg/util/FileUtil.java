package org.adv.alg.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public class FileUtil {

  public static boolean deleteFile(Path path) {
    return path.toFile().delete();
  }


  public static Path getPathFromMultiPartFile(MultipartFile file) throws IOException {
    return getPathFromFile(file.getBytes(), ".odt");

  }

  private static Path getPathFromFile(byte[] file, String fileType) throws IOException {
    Path path = Files.write(Paths.get(System.getProperty("java.io.tmpdir"),
        getFileName(fileType)), file,
        StandardOpenOption.CREATE);
    path.toFile().deleteOnExit();
    return path;
  }

  private static String getFileName(String fileType) {
    StringBuilder excelFileName = new StringBuilder();
    excelFileName.append("resume");
    excelFileName.append(UUID.randomUUID().toString());
    excelFileName.append(fileType);
    return excelFileName.toString();
  }
}
