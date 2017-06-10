import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

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

    public static ArrayList<String> readFileByLine(String name) {
        FileReader fr = null;
        BufferedReader br = null;
        ArrayList<String> result = new ArrayList<>();

        try {
            fr = new FileReader(name);
            br = new BufferedReader(fr);

            while(br.ready()) {
                result.add(br.readLine());
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

        return result;
    }

    public static ArrayList<Double> scaling(ArrayList<Double> orginals) {
        double max = Collections.max(orginals);
        double min = Collections.min(orginals);
        ArrayList<Double> results = new ArrayList<>();

        if(max-min == 0)
        {
            for(int i=0; i<orginals.size(); i++)
                results.add(0.0);
            return results;
        }

        for(double v: orginals) {
            results.add( (v-min) / (max-min) );
        }

        return results;
    }
}
