/*keyword matching in resume
 *Bidirectional Boyer-Moore and Quick search algorithm(BBQ)
 * How to use:
 * Select text file(.docx) and keyword in Postman and send
 * e.g.
 * file: ResumeSample1.docx  keyWord: Experience
 * Result: "Keyword found using QS Algorithm"
 */
package org.adv.alg.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.adv.alg.util.FileUtil;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.xmlbeans.XmlException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ResumeProcessor {

  int searchflag= 0;

  private ExecutorService executorService;

  public String processResume(MultipartFile file, String keyWord)
          throws IOException, OpenXML4JException, XmlException, InterruptedException, ExecutionException, TimeoutException {

    executorService = Executors.newFixedThreadPool(2);

    Path path = FileUtil.getPathFromMultiPartFile(file);
    XWPFWordExtractor x = new XWPFWordExtractor(OPCPackage.open(path.toFile()));
    String text = x.getText();
    //if we want to ignore case sensitivity, lower case the strings
    text = text.toLowerCase();
    keyWord = keyWord.toLowerCase();
    //remove whitespace in the text
    String noSpaceStr = text.replaceAll("\\s", "");

    char[] textC = noSpaceStr.toCharArray();
    char[] pat = keyWord.toCharArray();

    List<Callable<Integer>> callableProcessors = new ArrayList<>();

    callableProcessors.add(new BoyerMooreSearch(textC, pat));
    callableProcessors.add(new QuickSearch(textC, pat));
    List<Future<Integer>>  futures = executorService.invokeAll(callableProcessors);


    for(int index=0;index<2;index++) {
      Future<Integer> el = futures.get(index);
      searchflag = el.get(2, TimeUnit.MINUTES);
      if (searchflag==1||searchflag==2) {
      break;
      }
    }

    String str1="Keyword not found ";
    String str2 = "Keyword found using Quick Search Algorithm ";
    String str3 = "Keyword found using BM Algorithm ";
    String str4 ="\nERROR : SearchFlag=";


    if (searchflag == 0)
      return str1;

    else if (searchflag == 1)

      return str2;

    else if (searchflag == 2)

      return str3;
    else
      return str4;

  }

  public String processResumeWithBMOnly(MultipartFile file, String keyWord)
          throws IOException, OpenXML4JException, XmlException, InterruptedException, ExecutionException, TimeoutException {

    executorService = Executors.newFixedThreadPool(2);

    Path path = FileUtil.getPathFromMultiPartFile(file);
    XWPFWordExtractor x = new XWPFWordExtractor(OPCPackage.open(path.toFile()));
    String text = x.getText();
    //if we want to ignore case sensitivity, lower case the strings
    text = text.toLowerCase();
    keyWord = keyWord.toLowerCase();
    //remove whitespace in the text
    String noSpaceStr = text.replaceAll("\\s", "");

    char[] textC = noSpaceStr.toCharArray();
    char[] pat = keyWord.toCharArray();

    List<Callable<Integer>> callableProcessors = new ArrayList<>();

    callableProcessors.add(new BoyerMooreSearch(textC, pat));
    List<Future<Integer>>  futures = executorService.invokeAll(callableProcessors);

    Future<Integer> el = futures.get(0);
    searchflag = el.get(2, TimeUnit.MINUTES);

    String str1="Keyword not found ";
    String str3 = "Keyword found using BM Algorithm ";
    String str4 ="\nERROR : SearchFlag=";

    if (searchflag == 0)
      return str1;

    else if (searchflag == 2)

      return str3;
    else
      return str4;

  }
}














