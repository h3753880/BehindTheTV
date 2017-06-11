/**
 * Created by yangyaochia on 05/06/2017.
 */

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;


public class Prediction {

    static private double[][] m;
    static private ArrayList<Double> rating;
    static private NelderMead findFeatureWeighting = null;
    static private int kFold;
    static private double[] error;
    static private int numTesting;
    static private int numTraining;
    static private double[][] matTesting;
    static private double[][] matTraining;

    static private ArrayList<Double> ratingTesting;
    static private ArrayList<Double> ratingTraining;

    static private double[] predictedRating;

    static private double[][] distance;
    static private double[][] weightings;
    static private double[][] featureWeighting;


    static private double[][] finalDistance;
    static private double[][] finalWeightings;
    static private double[]   finalPredictedRating;

    static private double THETA;

    //static private double[][] errorTesting;

    public Prediction(double[][] mat, ArrayList<Double> rating, int kFold) {
        this.m = mat;
        this.rating = rating;
        this.kFold = kFold;
        this.error = new double[kFold];
        this.numTesting = mat.length/kFold;
        this.numTraining = (mat.length*(kFold-1)/kFold == 0)? numTesting:mat.length*(kFold-1)/kFold;

        this.predictedRating = new double[numTesting];
        this.distance = new double [numTesting][numTraining];

        this.weightings = new double [numTesting][numTraining];

        this.finalDistance = new double [mat.length][mat.length];
        this.finalWeightings = new double [mat.length][mat.length];
        this.finalPredictedRating = new double [mat.length];

        this.matTraining = new double[numTraining][mat[0].length];
        this.matTesting = new double[numTesting][mat[0].length];

        this.featureWeighting = new double[kFold+1][mat[0].length];

        this.ratingTesting = new ArrayList<Double>();
        for (int i = 0 ; i < numTesting ; i++)
            ratingTesting.add(0.0);

        this.ratingTraining = new ArrayList<Double>();
        for (int i = 0 ; i < numTraining ; i++)
            ratingTraining.add(0.0);

        //this.errorTesting = new double[kFold][20];
    }

    public void predict()
    {

        for ( int i = 0 ; i < kFold ; i++ ) {

            buildMatTraining(i);
            buildMatTesting(i);

            buildRatingTraining(i);
            buildRatingTesting(i);

            System.out.println("matTraining row = "+matTraining.length+" col = "+matTraining[0].length);
            System.out.println("matTesting row = "+matTesting.length+" col = "+matTesting[0].length);
            System.out.println("ratingTraining row = "+ratingTraining.size());
            System.out.println("ratingTesting row = "+ratingTesting.size() );

            findFeatureWeighting = new NelderMead(matTraining, ratingTraining);

            findFeatureWeighting.descend();
            //findFeatureWeighting.printRating(i);
            //findFeatureWeighting.printPredictedRating(i);
            //findFeatureWeighting.printMat(i);
            //findFeatureWeighting.printFeatureWeighting(i);
            //findFeatureWeighting.printDistance(i);
            //findFeatureWeighting.printWeightings(i);
            System.out.println("MSE = "+findFeatureWeighting.getMSE());
            double[] v = findFeatureWeighting.getFeatureWeighting();

            for (int j = 0 ; j < v.length ; j++ ) {
                featureWeighting[i][j] = v[j];
            }

            THETA = findFeatureWeighting.getTheta();
            error[i] = getError(v, i);

            printRatTrain(i);
            printRatTest(i);

        }
        for ( int j = 0 ; j < featureWeighting[0].length ; j++ )
        {
            for ( int i = 0 ; i < featureWeighting.length - 1 ; i++ ) {
                featureWeighting[kFold][j] += SQR(featureWeighting[i][j]);
            }
            featureWeighting[kFold][j] = Math.sqrt(featureWeighting[kFold][j]/kFold);
        }
        printFinalFeatureWeighting();
        printFinalPredictedRating();
        printActualRating();

    }


    private void printFinalFeatureWeighting() {

        try{
            //All your IO Operations
            FileWriter fw = new FileWriter("FinalFeatureWeighting.csv");
            for (int index = 0; index < featureWeighting[kFold].length; index++)
            {
                fw.append(String.valueOf(featureWeighting[kFold][index])+"\n");
            }
            fw.append("\n");
            fw.close();
        }
        catch(IOException ioe){
            //Handle exception here, most of the time you will just log it.
        }
    }

    private void buildMatTraining(int iTimes){
        for ( int i = 0 ; i < matTraining.length ; i++ ) {
            for ( int j = 0 ; j < matTraining[i].length ; j++ ) {
                matTraining[i][j] = m[(i + iTimes * numTraining) % m.length][j];
            }
        }

    }
    private void buildMatTesting(int iTimes){
        for ( int i = 0 ; i < matTesting.length ; i++ ) {
            for ( int j = 0 ; j < matTesting[i].length ; j++ ) {
                matTesting[i][j] = m[(i + (iTimes+1) * numTraining)% m.length][j];
            }
        }
    }
    private void buildRatingTraining(int iTimes){

        for ( int i = 0 ; i < ratingTraining.size() ; i++ ) {
                ratingTraining.set(i, rating.get((i + iTimes * numTraining) % m.length));
        }
    }
    private void buildRatingTesting(int iTimes){

        for ( int i = 0 ; i < ratingTesting.size() ; i++ ) {
            ratingTesting.set(i, rating.get( ((i + (iTimes+1) * numTraining) % m.length)) );
        }

    }
    private double getError(double v[], int iTimes) {
        double error = 0;

        for ( int i = 0 ; i < matTesting.length ; i++ ) {
            error += SQR(ratingTesting.get(i) - predictingRating(i, v, iTimes));
            System.out.println("i = "+i+": Actual raing = "+ratingTesting.get(i)+" , Predicted raing = "+predictedRating[i]);
        }
        error /= matTesting.length;
        return error;
    }

    static double predictingRating(int indexPredicted, double v[], int iTimes)
    {
        double tempDistance = 0;

        for ( int i = 0 ; i < matTraining.length ; i++ ) {
            //if ( i == indexPredicted )
            //    continue;
            for ( int j = 0 ; j < matTraining[0].length ; j++ ) {
                tempDistance += SQR(v[j] * (matTesting[indexPredicted][j] - matTraining[i][j]) );
            }
            distance[indexPredicted][i] = Math.sqrt(tempDistance);
            tempDistance = 0;
        }

        for ( int i = 0 ; i < matTraining.length ; i++ ) {
            weightings[indexPredicted][i] = Math.exp(-1 * THETA * distance[indexPredicted][i]);
        }
        double sum = 0;
        for ( int i = 0 ; i < matTraining.length ; i++ ) {
            //if ( i != indexPredicted ) {
                sum += weightings[indexPredicted][i];
            //}
        }
        for ( int i = 0 ; i < matTraining.length ; i++ ) {
            //if ( i != indexPredicted ) {
                weightings[indexPredicted][i] /= sum;
            //}
        }

        double predictedResult = 0;
        for ( int i = 0 ; i < matTraining.length ; i++ ) {
            //if ( i != indexPredicted ) {
                predictedResult += weightings[indexPredicted][i] * ratingTraining.get(i);
                //System.out.println(fwd(weightings[indexPredicted][i], 8, 4)+" "+fwd(ratingTraining.get(i),8,2));
            //}
        }
        predictedRating[indexPredicted] = predictedResult;
        finalPredictedRating[((indexPredicted + (iTimes+1) * numTraining) % m.length)] = predictedResult;
        return predictedResult;
    }

    static double SQR(double x)
    {
        return x*x;
    }

    public double[] getFinalPredictedRating(){
        return finalPredictedRating;
    }

    static void printFinalPredictedRating(){
        try{
            //All your IO Operations
            FileWriter fw = new FileWriter("Final_Predicted_Rating.csv");
            for (int index = 0; index < finalPredictedRating.length; index++)
            {
                fw.append(String.valueOf(finalPredictedRating[index])+"\n");
            }
            fw.append("\n");
            fw.close();
        }
        catch(IOException ioe){
            //Handle exception here, most of the time you will just log it.
        }
    }
    static void printActualRating() {
        try{
            //All your IO Operations
            FileWriter fw = new FileWriter("Actual_Rating.csv");
            for (int index = 0; index < rating.size(); index++)
            {
                fw.append(String.valueOf(rating.get(index))+"\n");
            }
            fw.append("\n");
            fw.close();
        }
        catch(IOException ioe){
            //Handle exception here, most of the time you will just log it.
        }
    }

    public double getMeanSquaredError()
    {
        double result = 0;
        for (int i = 0 ; i < error.length ; i++ )
        {
            result += error[i];
            System.out.print(error[i]+" ");
        }
        System.out.println();
        result /= error.length;
        return result;
    }
    public void printRatTrain(int i){
        try{
            //All your IO Operations
            FileWriter fw = new FileWriter("rating_training"+i+".csv");
            for (int index = 0; index < ratingTraining.size(); index++)
            {
                fw.append(String.valueOf(ratingTraining.get(index))+"\n");
            }
            fw.append("\n");
            fw.close();
        }
        catch(IOException ioe){
            //Handle exception here, most of the time you will just log it.
        }
    }
    public void printRatTest(int i){
        try{
            //All your IO Operations
            FileWriter fw = new FileWriter("rating_testing"+i+".csv");
            for (int index = 0; index < ratingTesting.size(); index++)
            {
                fw.append(String.valueOf(ratingTesting.get(index))+"\n");
            }
            fw.append("\n");
            fw.close();
        }
        catch(IOException ioe){
            //Handle exception here, most of the time you will just log it.
        }
    }

    static String fwd(double x, int w, int d)
    // converts a double to a string with given width and decimals.
    {
        java.text.DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(d);
        df.setMinimumFractionDigits(d);
        df.setGroupingUsed(false);
        String s = df.format(x);
        while (s.length() < w)
            s = " " + s;
        if (s.length() > w) {
            s = "";
            for (int i=0; i<w; i++)
                s = s + "-";
        }
        return s;
    }
}
