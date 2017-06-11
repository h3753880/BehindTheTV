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

        double[][] mat = norm.getMat();
        //double[][] mat = svd.getU();
        double[][] matContent = new double[mat.length][39];
        double[][] matContentMore = new double[mat.length][45];
        for ( int i = 0 ; i < matContent.length ; i++ ) {
            for ( int j = 0 ; j < matContent[i].length ; j++ ) {
                matContent[i][j] = mat[i][j];
            }
        }
        for ( int i = 0 ; i < matContentMore.length ; i++ ) {
            for ( int j = 0 ; j < matContentMore[i].length ; j++ ) {
                matContentMore[i][j] = mat[i][j];
            }
        }
        SVD s3 = new SVD(mat);
        s3.buildSVD();
        SVD s1 = new SVD(matContent);
        s1.buildSVD();
        SVD s2 = new SVD(matContentMore);
        s2.buildSVD();

        ArrayList<Double> rating = norm.getRating();
        double[] ratingArray = new double[mat.length];

        Prediction p1 = new Prediction(matContent, norm.getImdbRating(), kFold);
        p1.predict();
        double result = p1.getMeanSquaredError();
        System.out.println("Avg MSE = "+result);

        Prediction p2 = new Prediction(matContentMore, norm.getImdbRating(), kFold);
        p2.predict();
        result = p2.getMeanSquaredError();
        System.out.println("Avg MSE = "+result);

        Prediction p3 = new Prediction(mat, norm.getImdbRating(), kFold);
        p3.predict();
        result = p3.getMeanSquaredError();
        System.out.println("Avg MSE = "+result);

        Prediction p4 = new Prediction(s1.getU(), norm.getImdbRating(), kFold);
        p4.predict();
        result = p4.getMeanSquaredError();
        System.out.println("Avg MSE = "+result);

        Prediction p5 = new Prediction(s2.getU(), norm.getImdbRating(), kFold);
        p5.predict();
        result = p5.getMeanSquaredError();
        System.out.println("Avg MSE = "+result);

        Prediction p6 = new Prediction(s3.getU(), norm.getImdbRating(), kFold);
        p6.predict();
        result = p6.getMeanSquaredError();
        System.out.println("Avg MSE = "+result);
        //System.out.println(gson.toJson(shows)); //test
    }
    static double SQR(double e) {return e*e;}
}
