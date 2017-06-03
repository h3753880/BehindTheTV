//package com.company;

/**
 * Created by yangyaochia on 26/05/2017.
 */
import org.ejml.data.Matrix;
import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;
import java.util.ArrayList;

public class SVD {

    static private double[][] m;
    static private double[][] u;
    static private double[][] w;
    static private double[][] v;

    public SVD(double[][] mat) {
        this.m = mat;
    }

    static void buildSVD() {
        SimpleMatrix A = new SimpleMatrix(m);
        //Matrix AA = new Matrix(m);

        for( int i = 0; i < A.numRows(); i++) {
            for( int j = 0 ; j < A.numCols() ; j++) {
                System.out.print(A.get(i,j) + " ");
            }
            System.out.println();
        }
        System.out.println("----------------------------------");
        //SimpleMatrix matA = new SimpleMatrix(matrixData);
        @SuppressWarnings("unchecked")
        SimpleSVD svd = A.svd();

        SimpleMatrix U = (SimpleMatrix) svd.getU();
        SimpleMatrix W = (SimpleMatrix) svd.getW();
        SimpleMatrix V = (SimpleMatrix) svd.getV();

        for( int i = 0; i < U.numRows(); i++) {
            for( int j = 0 ; j < U.numCols() ; j++)
                System.out.print(U.get(i,j) + " ");
            System.out.println();
        }
        System.out.println("----------------------------------");
        for( int i = 0; i < W.numRows(); i++) {
            for( int j = 0 ; j < W.numCols() ; j++)
                System.out.print(W.get(i,j) + " ");
            System.out.println();
        }
        System.out.println("----------------------------------");
        for( int i = 0; i < V.numRows(); i++) {
            for( int j = 0 ; j < V.numCols() ; j++)
                System.out.print(V.get(i,j) + " ");
            System.out.println();
        }
    }

}