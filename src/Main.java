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
        SVD svd = new SVD(norm.getMat());
        svd.buildSVD();
        //svd.printU();
        //svd.printUW();

        double[][] mat = norm.getMat();
        double[][] matContent = new double[mat.length][36];
        for ( int i = 0 ; i < matContent.length ; i++ ) {
            for ( int j = 0 ; j < matContent[i].length ; j++ ) {
                matContent[i][j] = mat[i][j];
            }
        }
        //double[][] mat = svd.getU();
        ArrayList<Double> rating = norm.getRating();
        double[] ratingArray = new double[mat.length];

        //System.out.print(formerResult[0].length);
        /*double[][] testMat = new double[70][formerResult[0].length];
        for ( int i = 0 ; i < testMat.length ; i++ ){
            for ( int j = 0 ; j < testMat[i].length ; j++ ){
                testMat[i][j] = formerResult[i][j];
            }
        }*/
        time2 = System.currentTimeMillis();
        //Prediction predictiveModel = new Prediction(norm.getMat(), norm.getImdbRating(), kFold);
        Prediction predictiveModel = new Prediction(mat, norm.getImdbRating(), kFold);
        //Prediction predictiveModel = new Prediction(formerResult, norm.getImdbRating(), kFold);
        predictiveModel.predict();

        double result = predictiveModel.getMeanSquaredError();
        System.out.println("Avg MSE = "+result);

        //result = predictiveModel.getAvgMeanSquaredError();
        //System.out.println("AVG MSE = "+result);

        time3 = System.currentTimeMillis();


        System.out.println("SVD花了：" + (time2-time1)/1000 + "秒");

        System.out.println("Prediction花了：" + (time3-time2)/1000 + "秒");

/*
        for ( int i = 0 ; i < ratingArray.length ; i++ )
        {
            ratingArray[i] = rating.get(i);
        }
        double[][] matTest = new double[6][mat[0].length];
        double[][] matTrain = new double[64][mat[0].length];

        double[] ratTest = new double[6];
        double[] ratTrain = new double[64];

        for ( int i = 0 ; i < matTest.length ; i++) {
            for ( int j = 0 ; j < mat[i].length ; j++ )
                matTest[i][j] = mat[i][j];
        }
        for ( int i = matTest.length ; i < mat.length ; i++) {
            for ( int j = 0 ; j < mat[i].length ; j++ )
                matTrain[ i - matTest.length ][j] = mat[i][j];
        }
        for ( int i = 0 ; i < matTest.length ; i++) {
            ratTest[i] = ratingArray[i];
        }
        for ( int i = matTest.length ; i < mat.length ; i++) {
            ratTrain[ i - ratTest.length ] = ratingArray[i];
        }


        MultipleLinearRegression regression = new MultipleLinearRegression(matTrain, ratTrain);
        regression.printPredictedRating();
        System.out.println("MLR MSE = "+regression.getMSE());
        System.out.println("MLR R2 = "+regression.R2());

        double[] beta = regression.getBeta();
        double[] predictedRatingMLR = new double[ratTest.length];

        for ( int i = 0 ; i < predictedRatingMLR.length ; i++ )
        {
            for ( int j = 0 ; j < beta.length ; j++ )
                predictedRatingMLR[i] += beta[j]*matTest[i][j];
        }
        double error = 0;
        for ( int i = 0 ; i < predictedRatingMLR.length ; i++ )
        {
            error += SQR(ratTest[i] - predictedRatingMLR[i]);
            System.out.println(ratTest[i]+" "+predictedRatingMLR[i]);
        }
        error /= predictedRatingMLR.length;
        System.out.println("RatTest MSE = "+error);*/


        //System.out.printf("%.2f + %.2f beta1 + %.2f beta2  (R^2 = %.2f)\n",
        //        regression.beta(0), regression.beta(1), regression.beta(2), regression.R2());


        //System.out.println(gson.toJson(shows)); //test
    }
    static double SQR(double e) {return e*e;}
}
