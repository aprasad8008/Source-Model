import java.io.FileReader;

public class SourceModel {
    private String name;
    private double[][] probMatrix;

    public SourceModel(String sourceModel, String corpusFile) throws Exception {
        name = sourceModel;
        double[][] charCount = new double[26][26];
        System.out.print("Training " + sourceModel + " model...");

        FileReader cFile = new FileReader(corpusFile);

        int k;
        int prevTextNum;
        int curTextNum = 0;
        int firstChar = 0;
        int firstNum = 0;

        while ((k = cFile.read()) != -1) {

            char text = Character.toLowerCase((char) k);

            if (Character.isAlphabetic(k)) {
                prevTextNum = curTextNum;
                curTextNum = (int) (text - 'a');
                charCount[prevTextNum][curTextNum]++;
                //firstNum check which will never happen again
                if (firstNum == 0) {
                    firstNum++;
                    firstChar = curTextNum;
                }
            }
        }
        charCount[0][firstChar]--;

        probMatrix = charCount;
        double[] totals = new double[26];

        for (int a = 0; a < probMatrix.length; a++) {
            for (int b = 0; b < probMatrix[a].length; b++) {
                totals[a] += probMatrix[a][b];
            }
        }

        for (int i = 0; i < probMatrix.length; i++) {
            for (int j = 0; j < probMatrix[i].length; j++) {
                if (totals[i] != 0) {
                    probMatrix[i][j] /= totals[i];
                    if (probMatrix[i][j] == 0.0) {
                        probMatrix[i][j] = 0.01;
                    }
                } else if (probMatrix[i][j] == 0.0) {
                    probMatrix[i][j] = 0.01;
                }

            }
        }

        System.out.println("done");

    }

    public String getName() {
        return name;
    }

    public String toString() {

        String txtPrint = "Model: " + this.name + "\n";
        txtPrint += "  " + "   ";

        for (int i = 0; i < 26; i++) {
            txtPrint += (char) (i + 97) + "  " + "  ";
        }

        for (int c = 0; c < probMatrix.length; c++) {
            txtPrint += "\n" + (char) (c + 97) + " ";
            for (int d = 0; d < probMatrix[c].length; d++) {
                txtPrint +=
                txtPrint.format("%.2f", Math.round(probMatrix[c][d] * 100.0)
                / 100.0) + " ";
            }
        }
        return txtPrint;
    }

    public double probability(String a) {

        double prob = 1.0;
        int prevNum2;
        char prevCharVal = ' ';
        char curCharVal = ' ';
        String temp = a.replaceAll("[^a-zA-Z]", "");

        for (int i = 0; i < temp.length() - 1; i++) {
            prevNum2 = i;
            prevCharVal = temp.charAt(prevNum2);
            curCharVal = temp.charAt(i + 1);
            prob *=
            probMatrix[(int) Character.toLowerCase(prevCharVal)
            - 'a'][(int) Character.toLowerCase(curCharVal) - 'a'];
        }
        return prob;
    }

    public static void main(String[] args) throws Exception {

        int comIndex = 0;
        SourceModel[] langs = new SourceModel[args.length - 1];

        for (int i = 0; i < args.length - 1; i++) {
            comIndex = args[i].indexOf(".");
            langs[i] = new SourceModel(args[i].substring(0, comIndex), args[i]);
        }

        double sum = 0.0;

        for (int i = 0; i < langs.length; i++) {
            sum += langs[i].probability(args[args.length - 1]);
        }

        System.out.println("Analyzing: " + args[args.length - 1]);
        double probNum = 0.0;
        double test = 0.0;
        String testLang = "";

        for (int i = 0; i < langs.length; i++) {
            probNum = langs[i].probability(args[args.length - 1]) / sum;
            if (test < probNum) {
                test = probNum;
                testLang = langs[i].getName();
            }
            System.out.printf("Probability that test string is %9s : %.2f",
                             langs[i].getName(), probNum);
            System.out.println();
        }
        System.out.println("Test string is most likely " + testLang + ".");

    }
}
