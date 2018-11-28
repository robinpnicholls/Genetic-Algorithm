/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataSet2;


import static DataSet2.GA.ruleNum;
import static DataSet2.Rule.ruleSize;
import java.util.Random;
/**
 *
 * @author Robin
 */
public class Individual {

    public static final int geneSize = (ruleSize * ruleNum); //gene size

    int[] gene = new int[geneSize];
    int fitness;

    Individual() {

        for (int j = 0; j < geneSize; j++) {

            if ((j + 1) % ruleSize == 0) {
                this.gene[j] = (int) Math.round(Math.random());
            } else {
                Random rn = new Random();
                this.gene[j] = rn.nextInt(2 - 0 + 1) + 0;
            }
        }
        this.fitness = 0;
    }

    public int getGene(int index) {
        return gene[index];
    }

    public int getGeneSize() {
        return geneSize;
    }

    public int getFitness() {
        return fitness;
    }

    public void setFitness(int value) {
        this.fitness = value;
    }

    public void setGene(int gene, int value) {
        this.gene[gene] = value;
    }

}
