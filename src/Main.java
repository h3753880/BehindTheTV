import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;

/**
 *
 * Normal.getMap -> get attribute name by matrix col index
 * Normal.getGenMap -> get matrix col index by attribute name
 * Normal.getMat -> get matrix
 * Normal.getRating -> get rating
 */
public class Main {
    public static void main(String[] args) {
        String json  = "";
        ArrayList<Show> shows = null;
        Normal norm = null;
        Gson gson = new Gson();

        json = Tool.readFile("show.json");
        shows = gson.fromJson(json, new TypeToken<ArrayList<Show>>(){}.getType());
        norm = new Normal(shows);

        norm.normFeatures();

        norm.printMat();// test
        //System.out.println(gson.toJson(shows)); //test
    }
}
