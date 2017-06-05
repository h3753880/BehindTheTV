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
/**
 * Normal.getMap -> get attribute name by matrix col index
 * Normal.getGenMap -> get matrix col index by attribute name
 * Normal.getMat -> get matrix
 * Normal.getRating -> get rating
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
        ArrayList<String> titles = norm.getTitles();
        try{
            //All your IO Operations
            FileWriter fw = new FileWriter("Title.csv");
            for ( int i = 0 ; i < titles.size() ; i++ ) {
                fw.append( i+":"+titles.get(i)+"\n" );
            }
            fw.append("\n");
            fw.close();
        }
        catch(IOException ioe){
            //Handle exception here, most of the time you will just log it.
        }


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
        // SVD to compress the features into concepts
        //SVD svd = new SVD(norm.getMat());
        //svd.buildSVD();

        findFeatureWeighting = new NelderMead(norm.getMat(), norm.getRating());
        //  dimension of input matrix has been reduced
        //findFeatureWeighting = new NelderMead(svd.getU(), norm.getRating());

        findFeatureWeighting.printMat();
        findFeatureWeighting.descend();
        findFeatureWeighting.printDistance();
        findFeatureWeighting.printWeightings();
        findFeatureWeighting.printFeatureWeighting();

        findFeatureWeighting.printRating();
        findFeatureWeighting.printPredictedRating();

        System.out.println("MSE = "+findFeatureWeighting.getMSE());
        //findFeatureWeighting.printDistance();


        //System.out.println(gson.toJson(shows)); //test
    }
}
