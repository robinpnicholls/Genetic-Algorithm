/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UCI;

import static UCI.GA.df;
import static UCI.GA.random;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Robin
 */
public class Gene {

    int upperBound;
    int lowerBound;
    boolean isHash;                                                             //indicates that the gene is currently set to #

    Gene() {
        this.upperBound = ThreadLocalRandom.current().nextInt(1, 99 + 1);
        this.lowerBound = ThreadLocalRandom.current().nextInt(1, this.upperBound + 1);
//        if (random.nextDouble() < 0.33) {
//            this.isHash = true;
//        } else {
//            this.isHash = false;
//        }
    }

    //copy constructor
    Gene(int upperBound, int lowerBound, boolean isHash) {
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
        this.isHash = isHash;
    }

    //copy constructor
    Gene(Gene gene) {
        this(gene.getUpperBound(), gene.getLowerBound(), gene.isHash());
    }

    public int getUpperBound() {
        return upperBound;
    }

    public int getLowerBound() {
        return lowerBound;
    }

    public boolean isHash() {
        return isHash;
    }

    public void setUpperBound(int newUpperBound) {
        this.upperBound = newUpperBound;
    }

    public void setLowerBound(int newLowerBound) {
        this.upperBound = newLowerBound;
    }

    public void setHash(boolean hashState) {
        this.isHash = hashState;
    }

}
