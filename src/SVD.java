//package com.company;

/**
 * Created by yangyaochia on 26/05/2017.
 */
import org.ejml.data.Matrix;
import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;

import javax.sound.midi.SysexMessage;
import java.util.ArrayList;

public class SVD {

    static private double[][] m;
    static private double[][] u;
    static private double[][] w;
    static private double[][] v;

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

        double sum = 0;
        for ( int i = 0 ; i < W.numCols() ; i++ )
            sum += Math.pow(W.get(i,i), 2);
        int accumuIndex = 0;
        double accumuSum = 0;
        for ( int i = 0 ; i < W.numCols() ; i++ ) {
            accumuSum += Math.pow(W.get(i,i), 2);
            if ( accumuSum/sum > 0.9 ) {
                accumuIndex = i;
                break;
            }
        }
        System.out.println("accum index = "+accumuIndex);

        u = new double[U.numRows()][accumuIndex];
        w = new double[accumuIndex][accumuIndex];
        v = new double[accumuIndex][V.numCols()];
        System.out.println("U row = "+u.length+" U col = "+u[0].length);
        System.out.println("W row = "+w.length+" W col = "+w[0].length);
        System.out.println("V row = "+v.length+" V col = "+v[0].length);
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
    }

    public double[][] getU()
    {
        return u;
    }

}