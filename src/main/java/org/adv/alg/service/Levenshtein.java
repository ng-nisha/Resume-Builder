package org.adv.alg.service;
import java.util.stream.IntStream;

public class Levenshtein {

    //*Levenshtein Algorithm to find the keyword with mistakes*//
    private static final String EMPTY = "";
    public static CharSequence fuzzySubstringSearch(CharSequence term, CharSequence query, int maxDistance) {
        if (term == null || term.length() == 0 || query == null || query.length() == 0 || maxDistance < 0) { // check input and short circuit
            return EMPTY;
        }

        int[][] minDistances = minDistances(term, query);

        MinScore minScore = minScore(query, minDistances);

        if (minScore.score > maxDistance) { // short circuit
            return EMPTY;
        }

        return reconstructResult(term, query, minDistances, minScore.endIndex);
    }

    //* Calculates the minimum distances between given query and term using dynamic programming* //

    private static int[][] minDistances(CharSequence term, CharSequence query) {
        int[][] m = new int[query.length() + 1][term.length() + 1];
        for (int i = 1; i <= query.length(); i++) {
            for (int j = 0; j <= term.length(); j++) {
                if (j == 0) {
                    m[i][j] = i;  // initial conditions
                }
                else if (term.charAt(j - 1) == query.charAt(i - 1)) {
                    m[i][j] = m[i - 1][j - 1];
                }
                else {
                    m[i][j] = 1 + IntStream.of(m[i][j - 1], m[i - 1][j], m[i - 1][j - 1]).min().getAsInt();
                }
            }
        }
        return m;
    }


    //* Calculates the minimum score between text and keyword.*/
    private static MinScore minScore(CharSequence query, int[][] minDistances) {
        int score = -1;
        int endIndex = -1;
        for (int i = 0; i < minDistances[query.length()].length; i++) {
            if (score < 0 || score >= minDistances[query.length()][i]) {
                score = minDistances[query.length()][i];
                endIndex = i;
            }
        }
        return new MinScore(score, endIndex);
    }


    // * Reconstructs the fuzzy matching substring.*//

    private static CharSequence reconstructResult(CharSequence term, CharSequence query, int[][] minDistances, int endIndex) {
        int row = query.length();
        int col = endIndex;
        while (row > 0 && col > 0) {
            if (query.charAt(row - 1) == term.charAt(col - 1)) {
                row--;
                col--;
            }
            else {
                int min = IntStream.of(minDistances[row][col - 1], minDistances[row - 1][col], minDistances[row - 1][col - 1]).min().getAsInt();
                if (minDistances[row][col - 1] == min) {
                    col--;
                }
                else if (minDistances[row - 1][col] == min) {
                    row--;
                }
                else {
                    row--;
                    col--;
                }
            }
        }

        return term.subSequence(col, endIndex);
    }

    private static class MinScore {
        public final int score;
        public final int endIndex;

        public MinScore(int score, int endIndex) {
            this.score = score;
            this.endIndex = endIndex;
        }
    }


}
