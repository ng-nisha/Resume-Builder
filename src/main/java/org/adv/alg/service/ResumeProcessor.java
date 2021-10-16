/*keyword matching in resume
 *Bidirectional Boyer-Moore and Quick search algorithm(BBQ)
 * How to use:
 * Select text file(.docx) and keyword in Postman and send
 * e.g.
 * file: ResumeSample3.docx  keyWord: Experience
 * Result: "Keyword is found"
 *         "Resume fit for the profile"
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
import org.adv.alg.service.Levenshtein;

@Component
public class ResumeProcessor {

  int searchflag= 0;

  private ExecutorService executorService;

  //*** Proposed algorithm--Bidirectional BM and QS (BBQ) algorithm***//

  public String processResumeWithBBQ(MultipartFile file, String keyWord)
          throws IOException, OpenXML4JException, XmlException, InterruptedException, ExecutionException, TimeoutException {

    executorService = Executors.newFixedThreadPool(2);

    String text = FileUtil.getStringFromMultiPartFile(file);

    //if we want to ignore case sensitivity, lower case the strings
    text = text.toLowerCase();
    keyWord = keyWord.toLowerCase();
    //remove whitespace in the text
    String noSpaceStr = text.replaceAll("\\s", "");

    char[] textC = noSpaceStr.toCharArray();
    char[] pat = keyWord.toCharArray();

    List<Callable<Integer>> callableProcessors = new ArrayList<>();

    callableProcessors.add(new BoyerMooreSearch(textC, pat));// method call for BM (left window)
    callableProcessors.add(new QuickSearch(textC, pat));//method call for QS(right window)
    List<Future<Integer>> futures = executorService.invokeAll(callableProcessors);


    for (int index = 0; index < 2; index++) {
      Future<Integer> el = futures.get(index);
      searchflag = el.get(2, TimeUnit.MINUTES);
      if (searchflag == 1 || searchflag == 2) {
        break;
      }
    }

    String str1 = "Keyword Matched" + '\n' + "Resume fit for the profile";
    String str2 = "Keyword Not Matched" + '\n' + "Resume is not fit for the profile";

    //*Maximum allowable mistakes is set to 3 *//

    int maxMistakes = 3;
    String str3 = "Keyword matched with character error" + '\n' + "The keyword detected in the resume -  " + Levenshtein.fuzzySubstringSearch(text, keyWord, 3).toString() + '\n' + "Resume is fit for the profile";

    String str4 = "\nERROR : SearchFlag=";


    if (searchflag == 0) {
      if (Levenshtein.fuzzySubstringSearch(text, keyWord, maxMistakes) == "")
        return str2;
      else
        return str3;
    }
    else if (searchflag == 1 || searchflag ==2)
      return str1;
    else
      return str4;

  }

// ** existing algorithm ---Boyer-Moore**//

  public String processResumeWithBMOnly(MultipartFile file, String keyWord)
          throws IOException, OpenXML4JException, XmlException, InterruptedException, ExecutionException, TimeoutException {

    executorService = Executors.newFixedThreadPool(1);

    String text = FileUtil.getStringFromMultiPartFile(file);

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

    String str1 = "Keyword Matched" + '\n' + "Resume fit for the profile";
    String str2 = "Keyword Not Matched" + '\n' + "Resume is not fit for the profile";
    //*Maximum allowable mistakes is set to 3 *//
    int maxMistakes = 3;
    String str3 = "Keyword matched with character error" + '\n' + "The keyword detected in the resume -  " + Levenshtein.fuzzySubstringSearch(text, keyWord, 3).toString() + '\n' + "Resume is fit for the profile";

    String str4 = "\nERROR : SearchFlag=";

// ****If keyword is not matched, searching for keyword with maximum of 3 mistakes using Levenshtein algorithm****//

    if (searchflag == 0)
      if (Levenshtein.fuzzySubstringSearch(text, keyWord, maxMistakes) == "")
        return str2;
      else
        return str1;

    else if (searchflag == 2)

      return str3;
    else
      return str4;

  }
}














