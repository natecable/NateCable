import java.util.*;
import java.io.*;

public class SequenceAlignment {
    private static final int MATCH = 1;
    private static final int MISMATCH = -4;
    private static final int GAP_OPENING = -10;
    private static final int GAP_CONTINUATION = -1;

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the name of the file containing the reads: ");
        String fname = scan.next();
        File re = null;
        scan.close();
        
        re = new File(fname);

        Scanner fs = new Scanner(System.in);
        try{
            fs = new Scanner(re);
        }catch(Exception e){
            System.out.println("File not found, exiting...");
            System.exit(0);
        }
        List<String> seq = new ArrayList<String>();
        while (fs.hasNextLine()) {
            String l = fs.nextLine();
            if(l.charAt(0) == '>'){
                continue;
            }
            seq.add(l);
        }

        String seq1 = seq.get(0);
        String seq2 = seq.get(1);

        alignSequences(seq1, seq2);
    }

    public static void alignSequences(String seq1, String seq2) {
        int m = seq1.length() + 1;
        int n = seq2.length() + 1;

        double[][] scoreMatrix = new double[m][n];

        // Initialize the score matrix
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i == 0) {
                    scoreMatrix[i][j] = j * GAP_OPENING;
                } else if (j == 0) {
                    scoreMatrix[i][j] = i * GAP_OPENING;
                } else {
                    scoreMatrix[i][j] = 0;
                }
            }
        }

        // Fill the score matrix
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                double matchOrMismatch = seq1.charAt(i - 1) == seq2.charAt(j - 1) ? MATCH : MISMATCH;
                double diag = scoreMatrix[i - 1][j - 1] + matchOrMismatch;
                double up = scoreMatrix[i - 1][j] + ((i > 1 && scoreMatrix[i - 1][j] == scoreMatrix[i - 2][j]) ? GAP_CONTINUATION : GAP_OPENING);
                double left = scoreMatrix[i][j - 1] + ((j > 1 && scoreMatrix[i][j - 1] == scoreMatrix[i][j - 2]) ? GAP_CONTINUATION : GAP_OPENING);
                scoreMatrix[i][j] = Math.max(diag, Math.max(up, left));
            }
        }


        // Backtrace the score matrix to find the optimal alignment
        StringBuilder alignedSeq1 = new StringBuilder();
        StringBuilder alignedSeq2 = new StringBuilder();

        int i = m - 1;  
        int j = n - 1;
        double totalScore = scoreMatrix[i][j];

        while (i > 0 || j > 0) {
            double currentScore = scoreMatrix[i][j];
            if (i > 0 && j > 0 && currentScore == scoreMatrix[i - 1][j - 1] + (seq1.charAt(i - 1) == seq2.charAt(j - 1) ? MATCH : MISMATCH)) {
                alignedSeq1.insert(0, seq1.charAt(i - 1));
                alignedSeq2.insert(0, seq2.charAt(j - 1));
                i--;
                j--;
            } else if (i > 0 && currentScore == scoreMatrix[i - 1][j] + (i > 1 && scoreMatrix[i - 1][j] == scoreMatrix[i - 2][j] ? GAP_CONTINUATION : GAP_OPENING)) {
                alignedSeq1.insert(0, seq1.charAt(i - 1));
                alignedSeq2.insert(0, '_');
                i--;
            } else {
                alignedSeq1.insert(0, '_');
                alignedSeq2.insert(0, seq2.charAt(j - 1));
                j--;
            }
        }

        try{
            File out = new File("Alignments.aln");
            out.createNewFile();
            System.out.println("Saving output to Alignments.aln");
            FileWriter fw = new FileWriter(out);
            fw.write("> Sequence1\n");
            fw.write(alignedSeq1.toString());
            fw.write("\n> Sequence2\n");
            fw.write(alignedSeq2.toString());
            fw.write("\n\nTotal Score: " + totalScore);
            fw.close();
        }catch(Exception e){
            System.out.println("Unknown Error writing to file");
        }

    }
}