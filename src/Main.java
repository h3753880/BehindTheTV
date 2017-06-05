import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

        //norm.printMat();// test
        SVD svd = new SVD(norm.getMat());
        //svd.buildSVD();
        /*HashMap<Integer, String> hmap = norm.getMap();
        // Display content using Iterator
        Set set = hmap.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry)iterator.next();
            System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
            System.out.println(mentry.getValue());
        }*/


        /*double[][] m = {
                { 1, 1, 1, 0, 0 },
                { 3, 3, 3, 0, 0 },
                { 4, 4, 4, 0, 0 },
                { 5, 5, 5, 0, 0 },
                { 0, 2, 0, 4, 4 },
                { 0, 0, 0, 5, 5 },
                { 0, 1, 0, 6, 2 },
                { 3, 1, 0, 1, 2 },
                { 0, 1, 1, 2, 2 },
                { 0, 1, 3, 2, 2 },
                { 0, 1, 2, 2, 2 },
                { 0, 1, 0, 4, 1 },
                { 5, 1, 4, 2, 2 },
                { 3, 1, 3, 2, 2 },
                { 2, 1, 1, 2, 2 },
                { 1, 1, 0, 3, 2 },
        };
        ArrayList<Double> rating = new ArrayList<Double>();
        double[] actualRating = {3.3, 4.4, 5.5, 6.6, 8.2, 8.0, 3.4, 5.5,
                6.0, 8.0, 9.1, 7.8, 5.5, 6.7, 7.8, 6.9};
        for(double d : actualRating)
            rating.add(d);
        findFeatureWeighting = new NelderMead(m, rating);*/
        ArrayList<Double> rating = norm.getRating();
        findFeatureWeighting = new NelderMead(norm.getMat(), norm.getRating());
        findFeatureWeighting.descend();
        findFeatureWeighting.printRating();
        findFeatureWeighting.printPredictedRating();

        System.out.println("MSE = "+findFeatureWeighting.getMSE());
        //findFeatureWeighting.printDistance();
        //System.out.println();
        //findFeatureWeighting.printWeightings();
        findFeatureWeighting.printFeatureWeighting();

        //System.out.println(gson.toJson(shows)); //test
    }
}
