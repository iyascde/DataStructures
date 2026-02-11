import java.io.BufferedReader;
import java.io.FileReader;

public class App {
    public static void main(String[] args) throws Exception {
        HashStringSet st = new HashStringSet();
        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\huang\\Desktop\\Prog\\DSA\\Unit 4\\HashIntSet\\book.txt"));
        String line;
        while((line = br.readLine()) != null) {
            String[] words = line.split("[\\s.,!?]+");
            for (String word : words) {
                st.add(word);
            }
        }
        System.out.println(st.toString());
        double hashOverhead = st.getBuckets() * 4 + st.size() * 4 + st.MAX_LOAD + 4 + 4; 
        System.out.println("Hash overhead: " + hashOverhead);
        System.out.println("Data overhead: " + st.getDataSize());
        System.out.println("Total overhead: " + (hashOverhead + st.getDataSize()));
        System.out.println("Memory efficiency: " + (st.getDataSize() / (hashOverhead + st.getDataSize())));
        System.out.println("Load factor: " + st.loadFactor());






































        

    }
}
