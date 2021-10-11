package org.adv.alg.service;

public class BoyerMooreSearch extends ResumeSearch

{
  private char[] textC;
  private char[] pat;
  private int searchflag=0;

  public BoyerMooreSearch(char[] textC, char[] pat) {
    this.textC = textC;
    this.pat = pat;
  }

  @Override
  public Integer call() {

    System.out.println("-----------IN BoyerMooreSearch");

    int[] d1 = makeD1(pat);
    int[] d2 = makeD2(pat);

    int k = pat.length - 1;

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
    return searchflag;
  }

  //Computation of Bad-character shift
  public  int[] makeD1(char[] pat) {
    int[] table = new int[10000];
    for (int i = 0; i < 10000; i++)
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
