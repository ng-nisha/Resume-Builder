package org.adv.alg.service;

import java.io.IOException;
import java.nio.file.Path;
import org.adv.alg.util.FileUtil;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.xmlbeans.XmlException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ResumeProcessor {

  public String processResume(MultipartFile file, String keyWord)
      throws IOException, OpenXML4JException, XmlException {

    Path path = FileUtil.getPathFromMultiPartFile(file);
    XWPFWordExtractor x = new XWPFWordExtractor(OPCPackage.open(path.toFile()));
    return x.getText();
  }
}
