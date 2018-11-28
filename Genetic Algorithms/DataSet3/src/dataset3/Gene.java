/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataset3;

import static dataset3.GA.df;
import static dataset3.GA.random;

/**
 *
 * @author Robin
 */
public class Gene {

    double upperBound;
    double lowerBound;
    boolean isHash;                                                             //indicates that the gene is currently set to #

    Gene() {
        this.upperBound = Double.parseDouble(df.format(random.nextDouble()));
        this.lowerBound = Double.parseDouble(df.format(Math.random() * upperBound));
//        if (random.nextDouble() < 0.33) {                                     //removed explicit hashing
//            this.isHash = true;
//        } else {
//            this.isHash = false;
//        }
    }

    //copy constructor
    Gene(double upperBound, double lowerBound, boolean isHash) {
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
        this.isHash = isHash;
    }

    //copy constructor
    Gene(Gene gene) {
        this(gene.getUpperBound(), gene.getLowerBound(), gene.isHash());
    }

    public double getUpperBound() {
        return upperBound;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public boolean isHash() {
        return isHash;
    }

    public void setUpperBound(double newUpperBound) {
        this.upperBound = newUpperBound;
    }

    public void setLowerBound(double newLowerBound) {
        this.upperBound = newLowerBound;
    }

    public void setHash(boolean hashState) {
        this.isHash = hashState;
    }

}
