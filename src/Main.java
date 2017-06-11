import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import java.util.ListIterator;
/**
 * Normal.getMap -> get attribute name by matrix col index
 * Normal.getGenMap -> get matrix col index by attribute name
 * Normal.getMat -> get matrix
 * Normal.getRating -> get rating
 * Normal.getImdbRating -> get imdb rating
 * Normal.getTitles -> get all titles
 */
public class Main {
    public static void main(String[] args) {
        String json  = "";
        ArrayList<Show> shows = null;
        Normal norm = null;
        Gson gson = new Gson();
        NelderMead findFeatureWeighting = null;
        //SVD

        json = Tool.readFile("show.json");
        shows = gson.fromJson(json, new TypeToken<ArrayList<Show>>(){}.getType());
        norm = new Normal(shows);

        norm.normFeatures();
        //norm.printMat();// test

        HashMap<Integer, String> hmap = norm.getMap();
        // Display content using Iterator
        Set set = hmap.entrySet();
        Iterator iterator = set.iterator();
        try{
            //All your IO Operations
            FileWriter fw = new FileWriter("Feature.csv");
            while(iterator.hasNext()) {
                Map.Entry mentry = (Map.Entry)iterator.next();
                fw.append( String.valueOf(mentry.getValue())+"\n" );
            }
            fw.append("\n");
            fw.close();
        }
        catch(IOException ioe){
            //Handle exception here, most of the time you will just log it.
        }
        ArrayList<String> titles = norm.getTitles();
        try{
            //All your IO Operations
            FileWriter fw = new FileWriter("Title.csv");
            for ( int i = 0 ; i < titles.size() ; i++ )
                fw.append(String.valueOf(i+1)+":"+String.valueOf(titles.get(i))+"\n" );
            fw.append("\n");
            fw.close();
        }
        catch(IOException ioe){
            //Handle exception here, most of the time you will just log it.
        }
        int kFold = 5;  // k-fold cross-validation
        long time1, time2, time3;



        time1 = System.currentTimeMillis();

        // doSomething()


        double[][] mat_full_attribute = norm.getMat();
        //double[][] mat = svd.getU();
        double[][] matContent = new double[mat.length][45];
        for ( int i = 0 ; i < matContent.length ; i++ ) {
            for ( int j = 0 ; j < matContent[i].length ; j++ ) {
                matContent[i][j] = mat[i][j];
            }
        }
        SVD svd = new SVD(mat);
        svd.buildSVD();

        ArrayList<Double> rating = norm.getRating();
        double[] ratingArray = new double[mat.length];

        time2 = System.currentTimeMillis();
        //Prediction predictiveModel = new Prediction(norm.getMat(), norm.getImdbRating(), kFold);
        Prediction predictiveModel = new Prediction(svd.getU(), norm.getImdbRating(), kFold);
        //Prediction predictiveModel = new Prediction(formerResult, norm.getImdbRating(), kFold);
        predictiveModel.predict();

        double result = predictiveModel.getMeanSquaredError();
        System.out.println("Avg MSE = "+result);

        time3 = System.currentTimeMillis();


        System.out.println("SVD花了：" + (time2-time1)/1000 + "秒");

        System.out.println("Prediction花了：" + (time3-time2)/1000 + "秒");

        //System.out.println(gson.toJson(shows)); //test
    }
    static double SQR(double e) {return e*e;}
}
