//package com.company;

/**
 * Created by yangyaochia on 26/05/2017.
 */
import org.ejml.data.Matrix;
import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;

import javax.sound.midi.SysexMessage;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class SVD {

    static private double[][] m;
    static private double[][] u;
    static private double[][] w;
    static private double[][] v;
    static private double[][] uw;

    public SVD(double[][] mat) {

        this.m = mat;
    }

    public void buildSVD() {
        SimpleMatrix A = new SimpleMatrix(m);
        //Matrix AA = new Matrix(m);

        /*for( int i = 0; i < A.numRows(); i++) {
            for( int j = 0 ; j < A.numCols() ; j++) {
                System.out.print(A.get(i,j) + " ");
            }
            System.out.println();
        }*/
        //System.out.println("----------------------------------");
        //SimpleMatrix matA = new SimpleMatrix(matrixData);
        @SuppressWarnings("unchecked")
        SimpleSVD svd = A.svd();

        SimpleMatrix U = (SimpleMatrix) svd.getU();
        SimpleMatrix W = (SimpleMatrix) svd.getW();
        SimpleMatrix V = (SimpleMatrix) svd.getV();

        //System.out.println("U row = "+U.numRows()+" U col = "+U.numCols());
        //System.out.println("W row = "+W.numRows()+" W col = "+W.numCols());
        //System.out.println("V row = "+V.numRows()+" V col = "+V.numCols());

        double sum = 0;
        int indexW = ( W.numCols() < W.numRows() )? W.numCols():W.numRows();
        //System.out.println("indexW = "+indexW);
        for ( int i = 0 ; i < indexW ; i++ )
            sum += Math.pow(W.get(i,i), 2);
        int accumuIndex = 0;
        double accumuSum = 0;
        for ( int i = 0 ; i < W.numCols() ; i++ ) {
            accumuSum += Math.pow(W.get(i,i), 2);
            if ( accumuSum/sum >= 0.98 ) {
                accumuIndex = i;
                break;
            }
        }
        //System.out.println("accum index = "+accumuIndex);

        u = new double[U.numRows()][accumuIndex];
        w = new double[accumuIndex][accumuIndex];
        v = new double[accumuIndex][V.numCols()];
        uw = new double[U.numRows()][accumuIndex];

        //System.out.println("U row = "+u.length+" U col = "+u[0].length);
        //System.out.println("W row = "+w.length+" W col = "+w[0].length);
        //System.out.println("V row = "+v.length+" V col = "+v[0].length);
        for( int i = 0; i < u.length; i++) {
            for( int j = 0 ; j < u[0].length ; j++) {
                u[i][j] = U.get(i,j);
            }
        }
        //System.out.println("----------------------------------");
        for( int i = 0; i < w.length; i++) {
            for( int j = 0 ; j < w[0].length  ; j++){
                w[i][j] = W.get(i,j);
                //System.out.print(W.get(i,j) + " ");
            }

            //System.out.println();
        }
        //System.out.println("----------------------------------");
        for( int i = 0; i < v.length; i++) {
            for( int j = 0 ; j <  v[0].length  ; j++) {
                v[i][j] = V.get(i,j);
                //System.out.print(V.get(i,j) + " ");
            }

            //System.out.println();
        }
        for (int i = 0; i < u.length; i++) { // aRow
            for (int j = 0; j < w.length; j++) { // bColumn
                for (int k = 0; k < u[i].length; k++) { // aColumn
                    uw[i][j] += u[i][k] * w[k][j];
                }
            }
        }

    }
    public void printU()
    {
        try{
            //All your IO Operations
            FileWriter fw = new FileWriter("U.csv");
            for( int i = 0; i < u.length; i++) {
                for( int j = 0 ; j <  u[i].length  ; j++) {
                    //v[i][j] = V.get(i,j);
                    //System.out.print( fwd(u[i][j],11,4) );
                    fw.append(String.valueOf(u[i][j])+" ");

                }

                fw.append("\n");
            }
            fw.append("\n");
            fw.close();
        }
        catch(IOException ioe){
            //Handle exception here, most of the time you will just log it.
        }
    }
    public void printUW()
    {
        try{
            //All your IO Operations
            FileWriter fw = new FileWriter("UW.csv");
            for( int i = 0; i < uw.length; i++) {
                for( int j = 0 ; j <  uw[i].length  ; j++) {
                    //v[i][j] = V.get(i,j);
                    //System.out.print( fwd(u[i][j],11,4) );
                    fw.append(String.valueOf(uw[i][j])+" ");

                }

                fw.append("\n");
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
        if (s.length() > w)
        {
            s = "";
            for (int i=0; i<w; i++)
                s = s + "-";
        }
        return s;
    }

    public double[][] getU()
    {
        return u;
    }
    public double[][] getUW()
    {
        return uw;
    }

}