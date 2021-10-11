package org.adv.alg.service;

public class QuickSearch extends ResumeSearch

{

    private char[] textC;
    private char[] pat;
    private int searchflag=0;

    public QuickSearch(char[] textC,char[] pat) {
        this.textC =textC;
        this.pat=pat;
    }

    @Override
    public Integer call() {

        System.out.println("-----------IN QuickSearch");

        int[] d3 = makeD3(pat);
        int i = textC.length-1;
        //Scan the text from n to n/2, where n is the length of the text
        while(pat.length-1<=(i/2)) {
            int j = pat.length-1;

            while(j>=0 && textC[i] ==pat[j]) {
                i--;
                j--;
            }

            if (j < 0) {
                searchflag=1;
                break;
            }
            if(i>=pat.length) {
                i-=d3[textC[i-pat.length]];
            }
        }
        return searchflag;
    }

    // Bad-character shift calculation
    public  int[] makeD3(char[]pat) {
        int[] table = new int[10000];
        for(int j=0;j<10000;j++)
            table[j]=pat.length+1;
        for(int j=pat.length-1;j>0;j--)
            table[pat[j]]= pat.length-((pat.length-1)-j);
        return table;
    }

}


