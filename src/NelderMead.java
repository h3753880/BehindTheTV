/**
 * Modified on objective function by yangyaochia on 31/05/2017.
 * Original author is from
 * //  simplex minimizer
 * //  Nelder & Mead 1965 Computer J, v.7, 308-313.
 * //  Lagarias et al 1998 SIAM J.Optim. p.112
 * //  M.Lampton UCB SSL 2004
 * //  I use an augmented simplex that keeps funcval with each vertex.
 */

import java.io.*;
import java.util.*;
import java.text.DecimalFormat;
import java.io.FileWriter;


public class NelderMead
{


    static private int MAXITER = 0;
    static private int ncalls = 0;
    static private final double TOL = 1E-6;

    static private final double LAMBDA = 0.001;
    static private final double THETA = 20;

    static private int NDIMS;
    static private int NPTS;
    static private int FUNC;
    static private int NTVSHOW;

    static private final int NCLOSETVSHOW = 10;


    static private double[][] mat;
    static private double[][] distance;
    static private double[][] weightings;
    static private double[]   featureWeighting;
    static private ArrayList<Double> rating;
    static private ArrayList<Double> predictedRating;

    static private double[][] closestDistance;
    static private int[][] indexClosestDistance;

    public NelderMead(double[][] mat, ArrayList<Double> rating) {
        this.mat = mat;
        this.rating = rating;
        this.predictedRating = new ArrayList<Double>();
        for (int i = 0 ; i < rating.size() ; i++)
            predictedRating.add(0.0);
        //System.out.println(mat[0].length);
        NDIMS = mat[0].length;
        NPTS = NDIMS + 1;
        FUNC = NDIMS;
        NTVSHOW = mat.length;
        MAXITER = NDIMS * NTVSHOW;
        this.distance = new double[NTVSHOW][NTVSHOW];
        this.weightings = new double[NTVSHOW][NTVSHOW];
        this.featureWeighting = new double[NDIMS];

        this.closestDistance = new double[NTVSHOW][NCLOSETVSHOW];
        this.indexClosestDistance = new int [NTVSHOW][NCLOSETVSHOW];
    }

    public void descend()
    {
        ////// set up the starting simplex //////////////////
        System.out.println("NTVSHOW = "+mat.length+" NFEATURE = "+mat[0].length);
        //System.out.println("rating len = "+rating.size());
        double simplex[][] = new double[NPTS][NPTS]; // [row][col] = [whichvx][coord,FUNC]

        for ( int i = 1 ; i < simplex.length ; i++ ) {
            simplex[0][i-1] = 1;
            simplex[i][i-1] = 5;
        }
        for ( int i = 1 ; i < simplex.length ; i++ ) {
            for ( int j = 0 ; j < simplex[i].length - 1 ; j++ ) {
                if ( i-1 != j )
                    simplex[i][j] = 0.2;
            }
        }
        /*for ( int i = 0 ; i < simplex.length ; i++ ) {
            for ( int j = 0 ; j < simplex[i].length ; j++ ) {
                System.out.print(simplex[i][j]+" ");
            }
            System.out.println();
        }*/
        double best = 1E99;

        //////////////// initialize the funcvals ////////////////

        for (int i=0; i<NPTS; i++)
            simplex[i][FUNC] = func(simplex[i]);

        System.out.println("ncalls = "+fwi(ncalls,6));
        int iter=0;

        for (iter=1; iter<MAXITER; iter++)
        {
            /////////// identify lo, nhi, hi points //////////////

            double flo = simplex[0][FUNC];
            double fhi = flo;
            int  ilo=0, ihi=0, inhi = -1; // -1 means missing
            for (int i=1; i<NPTS; i++)
            {
                if (simplex[i][FUNC] < flo)
                {flo=simplex[i][FUNC]; ilo=i;}
                if (simplex[i][FUNC] > fhi)
                {fhi=simplex[i][FUNC]; ihi=i;}
            }
            double fnhi = flo;
            inhi = ilo;
            for (int i=0; i<NPTS; i++)
                if ((i != ihi) && (simplex[i][FUNC] > fnhi))
                {fnhi=simplex[i][FUNC]; inhi=i;}

            /*for (int j=0; j<=NDIMS; j++)
                System.out.print(fwd(simplex[ilo][j], 5, 2));
            System.out.println();*/

            for (int j = 0 ; j < NDIMS; j++)
                featureWeighting[j] = simplex[ilo][j];
            ////////// exit criterion //////////////
            //System.out.println("iter = "+iter+" NDIMS = "+NDIMS );
            //System.out.println("(iter % 4*NDIMS) = "+(iter % 4*NDIMS) );
            if ((iter % (4*NDIMS)) == 0)
            {
                if (simplex[ilo][FUNC] > (best - TOL))
                    break;
                best = simplex[ilo][FUNC];
            }

            ///// compute ave[] vector excluding highest vertex //////

            double ave[] = new double[NDIMS];
            for (int j=0; j<NDIMS; j++)
                ave[j] = 0;
            for (int i=0; i<NPTS; i++)
                if (i != ihi)
                    for (int j=0; j<NDIMS; j++)
                        ave[j] += simplex[i][j];
            for (int j=0; j<NDIMS; j++)
                ave[j] /= (NPTS-1);


            ///////// try reflect ////////////////

            double r[] = new double[NDIMS];
            for (int j=0; j<NDIMS; j++)
                r[j] = 2*ave[j] - simplex[ihi][j];
            double fr = func(r);

            if ((flo <= fr) && (fr < fnhi))  // in zone: accept
            {
                for (int j=0; j<NDIMS; j++)
                    simplex[ihi][j] = r[j];
                simplex[ihi][FUNC] = fr;
                continue;
            }

            if (fr < flo)  //// below zone; try expand, else accept
            {
                double e[] = new double[NDIMS];
                for (int j=0; j<NDIMS; j++)
                    e[j] = 3*ave[j] - 2*simplex[ihi][j];
                double fe = func(e);
                if (fe < fr)
                {
                    for (int j=0; j<NDIMS; j++)
                        simplex[ihi][j] = e[j];
                    simplex[ihi][FUNC] = fe;
                    continue;
                }
                else
                {
                    for (int j=0; j<NDIMS; j++)
                        simplex[ihi][j] = r[j];
                    simplex[ihi][FUNC] = fr;
                    continue;
                }
            }

            ///////////// above midzone, try contractions:

            if (fr < fhi)  /// try outside contraction
            {
                double c[] = new double[NDIMS];
                for (int j=0; j<NDIMS; j++)
                    c[j] = 1.5*ave[j] - 0.5*simplex[ihi][j];
                double fc = func(c);
                if (fc <= fr)
                {
                    for (int j=0; j<NDIMS; j++)
                        simplex[ihi][j] = c[j];
                    simplex[ihi][FUNC] = fc;
                    continue;
                }
                else   /////// contract
                {
                    for (int i=0; i<NPTS; i++)
                        if (i != ilo)
                        {
                            for (int j=0; j<NDIMS; j++)
                                simplex[i][j] = 0.5*simplex[ilo][j] + 0.5*simplex[i][j];
                            simplex[i][FUNC] = func(simplex[i]);
                        }
                    continue;
                }
            }

            if (fr >= fhi)   /// over the top; try inside contraction
            {
                double cc[] = new double[NDIMS];
                for (int j=0; j<NDIMS; j++)
                    cc[j] = 0.5*ave[j] + 0.5*simplex[ihi][j];
                double fcc = func(cc);
                if (fcc < fhi)
                {
                    for (int j=0; j<NDIMS; j++)
                        simplex[ihi][j] = cc[j];
                    simplex[ihi][FUNC] = fcc;
                    continue;
                }
                else    ///////// contraction
                {
                    for (int i=0; i<NPTS; i++)
                        if (i != ilo)
                        {
                            for (int j=0; j<NDIMS; j++)
                                simplex[i][j] = 0.5*simplex[ilo][j] + 0.5*simplex[i][j];
                            simplex[i][FUNC] = func(simplex[i]);
                        }
                }
            }

        }

        System.out.println("ncalls, iters, Best ="+fwi(ncalls,6)+fwi(iter,6) + fwd(best,16,9));

    }

    static double func(double v[])
    {
        ncalls++;
        return objective(v);
    }

    static double objective(double v[])
    {
        double penalty = 0;
        for ( int i = 0 ; i < NDIMS ; i++ ) {
            penalty += SQR(SQR(v[i]) - 1);
        }
        return LOOMSE(v) + LAMBDA * penalty;
    }

    static double LOOMSE(double v[])
    {

        double meanSquareError = 0;
        for ( int i = 0 ; i < rating.size() ; i++ ) {
            meanSquareError += SQR(rating.get(i) - prediction(i, v));
            //System.out.println("i = "+i+": Actual raing = "+rating.get(i)+" , Predicted raing = "+prediction(i, v));
        }
        meanSquareError /= NTVSHOW;

        return meanSquareError;
    }

    static double prediction(int indexPredicted, double v[])
    {
        double tempDistance = 0;

        for ( int i = 0 ; i < NTVSHOW ; i++ ) {
            if ( i == indexPredicted )
              continue;
            for ( int j = 0 ; j < NDIMS ; j++ ) {
                tempDistance += SQR(v[j] * (mat[indexPredicted][j] - mat[i][j]) );
            }
            distance[indexPredicted][i] = Math.sqrt(tempDistance);
            tempDistance = 0;
        }

        //buildNearnest();

        for ( int i = 0 ; i < NTVSHOW ; i++ ) {
            weightings[indexPredicted][i] = Math.exp(-1 * THETA * distance[indexPredicted][i]);
        }
        double sum = 0;
        for ( int i = 0 ; i < NTVSHOW ; i++ ) {
            if ( i != indexPredicted ) {
                sum += weightings[indexPredicted][i];
            }
        }
        for ( int i = 0 ; i < NTVSHOW ; i++ ) {
            if ( i != indexPredicted ) {
                weightings[indexPredicted][i] /= sum;
            }
        }

        double predictedResult = 0;
        for ( int i = 0 ; i < NTVSHOW ; i++ ) {
            if ( i != indexPredicted ) {
                predictedResult += weightings[indexPredicted][i] * rating.get(i);
            }
        }
        predictedRating.set(indexPredicted, predictedResult);
        return predictedResult;
    }

    static private void buildNearnest(){

        //System.out.println(closestDistance.length+" "+closestDistance[0].length);
        //System.out.println(indexClosestDistance.length+" "+indexClosestDistance[0].length);
        //System.out.println(distance.length);
        double tempSmall = Double.MAX_VALUE;
        int tempIndex = 0;
        for ( int i = 0 ; i < distance.length ; i++ ) {

            for ( int k = 0 ; k < indexClosestDistance[0].length ; k++) {

                for ( int j = 0 ; j < distance[i].length ; j++ ) {
                    if ( i == j ) {
                        // distance(i,i) = 0
                        continue;
                    }
                    else if ( k == 0 ) {
                        if ( tempSmall > distance[i][j] ) {
                            tempSmall = distance[i][j];
                            tempIndex = j;
                        }
                    }
                    else {
                        if ( tempSmall > distance[i][j] && distance[i][j] > closestDistance[i][k-1] ) {
                            tempSmall = distance[i][j];
                            tempIndex = j;
                        }
                    }
                }
                closestDistance[i][k] = tempSmall;
                indexClosestDistance[i][k] = tempIndex;

                tempSmall = Double.MAX_VALUE;
                tempIndex = 0;

            }

        }
        /*for ( int i = 0 ; i < closestDistance.length ; i++)
        {
            for ( int j = 0 ; j < closestDistance[i].length ; j++ )
            {
                System.out.print(closestDistance[i][j]+" ");
            }
            System.out.println();
        }*/
    }




    static double parab(double p[])
    // simple paraboloid
    {
        return SQR(p[0]-2) + SQR(p[1]-20);
    }


    /////////////////////////////////utilities ////////////////////

    static double SQR(double x)
    {
        return x*x;
    }

    static String fwi(int n, int w)
    // converts an int to a string with given width.
    {
        String s = Integer.toString(n);
        while (s.length() < w)
            s = " " + s;
        return s;
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
        if (s.length() > w)
        {
            s = "";
            for (int i=0; i<w; i++)
                s = s + "-";
        }
        return s;
    }
    public void printRating(int i)
    {
        try{
            //All your IO Operations
            FileWriter fw = new FileWriter("rating"+String.valueOf(i)+".csv");
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
    public void printPredictedRating(int i)
    {
        try{
            //All your IO Operations
            FileWriter fw = new FileWriter("PredictedRating"+String.valueOf(i)+".csv");

            for (int index = 0; index < predictedRating.size(); index++)
            {
                if (index == rating.size() - 1)
                {
                    fw.append(String.valueOf(predictedRating.get(index))+"\n");
                }
                else
                {
                    fw.append(String.valueOf(predictedRating.get(index))+"\n");
                    fw.append(" ");
                }
            }
            fw.append("\n");
            fw.close();
        }
        catch(IOException ioe){
            //Handle exception here, most of the time you will just log it.
        }
    }
    public void printMat(int i)
    {
        try{
            //All your IO Operations
            FileWriter fw = new FileWriter("mat"+String.valueOf(i)+".csv");
            for (double[] aMat : mat) {
                for (int j = 0; j < aMat.length; j++) {

                    if (j == aMat.length - 1) {
                        fw.append(String.valueOf(aMat[j]));
                    } else {
                        fw.append(String.valueOf(aMat[j]));
                        fw.append(" ");
                    }
                }
                fw.append("\n");
            }
            fw.close();
        }
        catch(IOException ioe){
            //Handle exception here, most of the time you will just log it.
        }

    }
    public void printDistance(int i)
    {
        try{
            //All your IO Operations
            FileWriter fw = new FileWriter("distance"+String.valueOf(i)+".csv");
            for (double[] aDistance : distance) {
                for (int j = 0; j < aDistance.length; j++) {

                    if (j == aDistance.length - 1) {
                        fw.append(String.valueOf(aDistance[j]));
                    } else {
                        fw.append(String.valueOf(aDistance[j]));
                        fw.append(" ");
                    }
                }
                fw.append("\n");
            }
            fw.close();
        }
        catch(IOException ioe){
            //Handle exception here, most of the time you will just log it.
        }

    }
    public void printWeightings(int i)
    {
        try{
            //All your IO Operations
            FileWriter fw = new FileWriter("weighting"+String.valueOf(i)+".csv");
            for (int index = 0; index < weightings.length; index++)
            {
                for ( int j = 0 ; j < weightings[index].length ; j++) {

                    if (j == weightings[index].length - 1)
                    {
                        fw.append(String.valueOf(weightings[index][j]));
                    }
                    else
                    {
                        fw.append(String.valueOf(weightings[index][j]));
                        fw.append(" ");
                    }
                }
                fw.append("\n");
            }
            fw.close();
        }
        catch(IOException ioe){
            //Handle exception here, most of the time you will just log it.
        }
    }

    public void printFeatureWeighting(int i) {

        try{
            //All your IO Operations
            FileWriter fw = new FileWriter("FeatureWeighting"+String.valueOf(i)+".csv");
            for (int index = 0; index < featureWeighting.length; index++)
            {
                if (index == rating.size() - 1)
                {
                    fw.append(String.valueOf(featureWeighting[index])+"\n");
                }
                else
                {
                    fw.append(String.valueOf(featureWeighting[index])+"\n");
                    fw.append(" ");
                }
            }
            fw.append("\n");
            fw.close();
        }
        catch(IOException ioe){
            //Handle exception here, most of the time you will just log it.
        }
    }

    public double getMSE()
    {

        double error = 0;
        for ( int i = 0 ; i < rating.size() ; i++ ) {
            error += SQR(rating.get(i) - predictedRating.get(i));
        }
        error /= rating.size();
        return error;
    }
    public double[][] getDistance()
    {
        return distance;
    }
    public double[][] getWeightings()
    {
        return weightings;
    }
    public double[]   getFeatureWeighting() { return featureWeighting; }
    public double        getTheta() { return THETA;}

    public ArrayList<Double> getRating() {
        return predictedRating;
    }
}


