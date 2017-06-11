import Jama.Matrix;
import Jama.QRDecomposition;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by yangyaochia on 10/06/2017.
 */
public class MultipleLinearRegression {

    private final int N;        // number of
    private final int p;        // number of dependent variables
    private final Matrix beta;  // regression coefficients
    private double SSE;         // sum of squared
    private double SST;         // sum of squared

    private double[] predictedRating;
    private double[][] mat;
    private double[] rating;

    public MultipleLinearRegression(double[][] x, double[] y) {

        this.mat = x;
        this.rating = y;

        if (x.length != y.length) throw new RuntimeException("dimensions don't agree");
        N = y.length;
        p = x[0].length;

        Matrix X = new Matrix(x);

        // create matrix from vector
        Matrix Y = new Matrix(y, N);

        // find least squares solution
        QRDecomposition qr = new QRDecomposition(X);
        beta = qr.solve(Y);


        // mean of y[] values
        double sum = 0.0;
        for (int i = 0; i < N; i++)
            sum += y[i];
        double mean = sum / N;

        // total variation to be accounted for
        for (int i = 0; i < N; i++) {
            double dev = y[i] - mean;
            SST += dev*dev;
        }

        // variation not accounted for
        Matrix residuals = X.times(beta).minus(Y);
        SSE = residuals.norm2() * residuals.norm2();

        predictedRating = new double[y.length];

    }

    public double beta(int j) {
        return beta.get(j, 0);
    }

    public double[] getBeta(){
        double[] result = new double[p];
        for ( int i = 0 ; i < result.length ; i++ )
        {
            result[i] = beta.get(i, 0);
        }
        return result;
    }

    public double R2() {
        return 1.0 - SSE/SST;
    }

    public void printPredictedRating() {
        for ( int i = 0 ; i < predictedRating.length ; i++ )
        {
            for ( int j = 0 ; j < p ; j++ ) {
                predictedRating[i] += beta.get(j,0) * mat[i][j];
            }
        }
        try{
            //All your IO Operations
            FileWriter fw = new FileWriter("PredictedRating_MLR.csv");

            for (int index = 0; index < predictedRating.length; index++)
            {
                fw.append(String.valueOf(predictedRating[index])+"\n");
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
        for ( int i = 0 ; i < rating.length ; i++ )
        {
            error += SQR(rating[i] - predictedRating[i]);
        }
        error /= rating.length;
        return error;
    }

    private double SQR(double x) { return x*x;}
}
