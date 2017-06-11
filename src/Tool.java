import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

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

    public static void featureCSV(Normal norm) {
        HashMap<Integer, String> hmap = norm.getMap();
        // Display content using Iterator
        Set set = hmap.entrySet();
        Iterator iterator = set.iterator();
        try {
            //All your IO Operations
            FileWriter fw = new FileWriter("Feature.csv");
            while (iterator.hasNext()) {
                Map.Entry mentry = (Map.Entry) iterator.next();
                fw.append(String.valueOf(mentry.getValue()) + "\n");
            }
            fw.append("\n");
            fw.close();
        } catch (IOException ioe) {
            //Handle exception here, most of the time you will just log it.
        }
    }

    public static void titleCSV(Normal norm) {
        ArrayList<String> titles = norm.getTitles();
        try {
            //All your IO Operations
            FileWriter fw = new FileWriter("Title.csv");
            for (int i = 0; i < titles.size(); i++)
                fw.append(String.valueOf(i + 1) + ":" + String.valueOf(titles.get(i)) + "\n");
            fw.append("\n");
            fw.close();
        } catch (IOException ioe) {
            //Handle exception here, most of the time you will just log it.
        }
    }

    public static double[][] getNewMat(double[][] mat, int count) {
        double[][] matContent = new double[mat.length][count];

        for (int i = 0; i < matContent.length; i++) {
            for (int j = 0; j < matContent[i].length; j++) {
                matContent[i][j] = mat[i][j];
            }
        }

        return matContent;
    }


    public static void printResult(ArrayList<Double> results) {
        for(int i=0; i<results.size(); i++) {
            switch(i) {
                case 0:
                    System.out.print("A ->   ");
                    break;
                case 1:
                    System.out.print("A + B ->   ");
                    break;
                case 2:
                    System.out.print("A + B + C ->   ");
                    break;
                case 3:
                    System.out.print("(A) -> SVD  ");
                    break;
                case 4:
                    System.out.print("(A+B) -> SVD  ");
                    break;
                case 5:
                    System.out.print("(A+B+C) -> SVD  ");
                    break;
                /*case 6:
                    System.out.print("(A+C) -> SVD  ");*/
            }
            System.out.println("AVG MSE = " + results.get(i));
        }
    }

}
