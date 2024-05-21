import java.io.*;


public class Ex1 {
    public static void main(String[] args) {
        String input = "input.txt", output = "output.txt";
        BayesianNetwork bn;
        try {
            BufferedReader br = new BufferedReader(new FileReader(input));
            FileWriter fw = new FileWriter(output);
            String line = br.readLine();
            bn = new BayesianNetwork(line);
            line = br.readLine();
            while (line != null) {
                String ans = bn.answer(line);
                fw.write(ans + "\n");
                line = br.readLine();
            }
            br.close();
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

