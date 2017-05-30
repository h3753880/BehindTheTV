import java.io.*;

/**
 * Created by Howard on 2017/5/30.
 */
public class Tool {
    public static String readFile(String name) {
        String content = "";
        FileReader fr = null;
        BufferedReader br = null;

        try {
            fr = new FileReader(name);
            br = new BufferedReader(fr);

            while(br.ready()) {
                content += br.readLine();
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fr.close();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return content;
    }
}
