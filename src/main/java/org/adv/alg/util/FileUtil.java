package org.adv.alg.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.io.File;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.xmlbeans.XmlException;
import org.springframework.web.multipart.MultipartFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;


public class FileUtil {

  public static boolean deleteFile(Path path) {
    return path.toFile().delete();
  }


  public static String getStringFromMultiPartFile(MultipartFile file) throws IOException, OpenXML4JException, XmlException {
      String contentType = file.getContentType();
      String text = "";
      if (contentType.equals("application/pdf")) {
        Path path =  getPathFromFile(file.getBytes(), ".pdf");
        PDDocument document = PDDocument.load(new File(path.toString())) ;
        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        stripper.setSortByPosition(true);
        PDFTextStripper tStripper = new PDFTextStripper();
        text = tStripper.getText(document);
        return text;
      } else {
          Path path = getPathFromFile(file.getBytes(), ".odt");
          XWPFWordExtractor x = new XWPFWordExtractor(OPCPackage.open(path.toFile()));
          text = x.getText();
          return text;
      }
  }

  private static Path getPathFromFile(byte[] file, String fileType) throws IOException {
    Path path = Files.write(Paths.get(System.getProperty("java.io.tmpdir"),
        getFileName(fileType)), file,
        StandardOpenOption.CREATE);
    path.toFile().deleteOnExit();
    return path;
  }

  private static String getFileName(String fileType) {
    StringBuilder fileName = new StringBuilder();
      fileName.append("resume");
      fileName.append(UUID.randomUUID().toString());
      fileName.append(fileType);
    return fileName.toString();
  }
}
