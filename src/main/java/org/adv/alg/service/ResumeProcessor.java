/*keyword matching in resume
 *Bidirectional Boyer-Moore and Quick search algorithm(BBQ)
 * How to use:
 * Select text file(.docx) and keyword in Postman and send
 * e.g.
 * file: ResumeSample1.docx  keyWord: Experience
 * Result: "Keyword found using QS Algorithm"
 */
package org.adv.alg.service;

import org.adv.alg.util.FileUtil;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.xmlbeans.XmlException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

@Component
public class ResumeProcessor {

  static int searchflag= 0;

  public String processResume(MultipartFile file, String keyWord)
          throws IOException, OpenXML4JException, XmlException {

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

    Boyer_moore thread1 = new Boyer_moore(textC, pat);
    Quicksearch thread2 = new Quicksearch(textC, pat);

    Thread t1 = new Thread(thread1);
    t1.start();
    Thread t2 = new Thread(thread2);
    t2.start();
    try {
      t1.join();
      t2.join();
    } catch (Exception ex) {
      System.out.println("Exception has been caught" + ex);
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
  // Boyer_Moore Algorithm to scan the text from right to left
  public class Boyer_moore extends ResumeProcessor implements Runnable {
    private char[] textC;
    private char[] pat;

    public Boyer_moore(char[] textC, char[] pat) {
      this.textC = textC;
      this.pat = pat;
    }

    public void run() {
      int[] d1 = makeD1(pat);
      int[] d2 = makeD2(pat);

      int k = pat.length - 1;
      int match = 0;
      //Scan the text from 0 to n/2, where n is the length of the text
      while (k <= (textC.length - 1) / 2) {
        int l = pat.length - 1;
        while (l >= 0 && (textC[k] == pat[l])) {
          k--;
          l--;
        }


        if (l < 0) {

          searchflag = 2;
          break;

        }
        if (l > 0) {

          k += Math.max(d1[textC[k]], d2[l]);

        }
      }

    }

    //Computation of Bad-character shift
    public  int[] makeD1(char[] pat) {
      int[] table = new int[255];
      for (int i = 0; i < 255; i++)
        table[i] = pat.length;
      for (int i = 0; i < pat.length - 1; i++)
        table[pat[i]] = pat.length - 1 - i;
      return table;
    }

    public  boolean isPrefix(char[] pat, int pos) {
      int suffixlen = pat.length - pos;
      for (int i = 0; i < suffixlen; i++) {
        if (pat[i] != pat[pos + i])
          return false;
      }
      return true;
    }

    public  int sufLen(char[] pat, int pos) {
      int i;
      for (i = 0; ((pat[pos - i] == pat[pat.length - 1 - i]) && (i < pos)); i++) {
      }

      return i;
    }
    //Computation good-suffix shift
    public  int[] makeD2(char[] pat) {
      int[] delta2 = new int[pat.length];
      int p;
      int last_prefix_index = pat.length - 1;
      for (p = pat.length - 1; p >= 0; p--) {
        if (isPrefix(pat, p + 1))
          last_prefix_index = p + 1;
        delta2[p] = last_prefix_index + (pat.length - 1 - p);
      }
      for (p = 0; p < pat.length - 1; p++) {
        int slen = sufLen(pat, p);
        if (pat[p - slen] != pat[pat.length - 1 - slen])
          delta2[pat.length - 1 - slen] = pat.length - 1 - p + slen;
      }
      return delta2;
    }
  }
  //QuickSearch Algorithm to scan the text from left to right
  public class Quicksearch extends ResumeProcessor implements Runnable{

    private char[] textC;
    private char[] pat;
    public Quicksearch(char[] textC,char[] pat) {
      this.textC =textC;
      this.pat=pat;
    }
    public void run() {

      int[] d3 = makeD3(pat);
      int i = textC.length-1;
      //Scan the text from n to n/2, where n is the length of the text
      while(pat.length-1<=(i/2)) {
        int j = pat.length-1;

        while(j>=0 && textC[i] ==pat[j]) {
          i--;
          j--;

        }

        if(j < 0) {

          searchflag=1;
          break;

        }

        if(i>=pat.length) {
          i-=d3[textC[i-pat.length]];

        }

      }

    }
    // Bad-character shift calculation
    public  int[] makeD3(char[]pat) {
      int[] table = new int[255];
      for(int j=0;j<255;j++)
        table[j]=pat.length+1;
      for(int j=pat.length-1;j>0;j--)
        table[pat[j]]= pat.length-((pat.length-1)-j);
      return table;
    }
  }

}














