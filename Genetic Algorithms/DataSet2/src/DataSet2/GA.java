/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataSet2;

import static DataSet2.Individual.geneSize;
import static DataSet2.Rule.ruleSize;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author Robin
 */
public class GA {

    public static double crossoverRate = 60;                                    // crossover rate as a percentage
    public static double mutationRate  = 2.5;                                  //mutation rate as a percentage
    public static double shuffleChance = 10;                                    //chance for rules within a population to flip

    public static int gen = 400;                                                //number of generation
    public static int pop = 100;                                                //populaiton size

    public static int ruleNum = 5;                                              //number of rules within a individual

    public static void main(String[] args) throws IOException {

        Individual[] population = new Individual[pop];

        for (int i = 0; i < pop; i++) {                                           //loop through and create populaiton of P size
            population[i] = new Individual();
        }
        System.out.print("0, ");
        evaluateFitness(population);                                           //evaluate initial fitness of population
        bestFitness(population);
        meanFitness(population);
        

        for (int i = 0; i < gen; i++) {                                           //select, crossover, mutate population G times
            evolve(population, i);
        }
        getBestIndividual(population);
    }

    public static Individual[] evolve(Individual[] population, int iteration) throws IOException {

        Individual best = new Individual();
        int bestFitness = 0;

        for (int k = 0; k < pop; k++) {
            if (population[k].getFitness() > bestFitness) {
                System.arraycopy(population[k].gene, 0, best.gene, 0, population[k].getGeneSize());
                bestFitness = population[k].getFitness();
            }
        }
        selection(population);
        evaluateFitness(population);
        crossOver(population);
        mutation(population);
        shuffle(population);

        int worstFitness = 100;
        int pointer = 0;

        for (int k = 0; k < pop; k++) {
            if (population[k].getFitness() < worstFitness) {
                worstFitness = population[k].getFitness();
                pointer = k;
            }
        }
        System.arraycopy(best.gene, 0, population[pointer].gene, 0, population[pointer].getGeneSize());

        System.out.print("\n"+(iteration + 1)+", ");
        evaluateFitness(population);
        bestFitness(population);
        meanFitness(population);
        

        return population;
    }

    public static Individual[] selection(Individual[] population) {                //evolve the population  //should losing parent be discarded and offsrping replace it during the loop or should population be replaced with offspring population at the end of the loop?

        Individual[] offSpring = new Individual[population.length];             //create new temp offspring population 
        for (int i = 0; i < population.length; i++) {
            offSpring[i] = new Individual();
            Random rand = new Random();                                         //pick two random individuals from the population
            int parent1 = rand.nextInt(population.length);
            int parent2 = rand.nextInt(population.length);
            if (population[parent1].getFitness() >= population[parent2].getFitness()) { //if parent 1 has a better fitness than parent 2, add it to the offspring population
                System.arraycopy(population[parent1].gene, 0, offSpring[i].gene, 0, population[i].getGeneSize());
            } else {                                                            //else add parent two to the offSpring population
                System.arraycopy(population[parent2].gene, 0, offSpring[i].gene, 0, population[i].getGeneSize());
            }
        }
        System.arraycopy(offSpring, 0, population, 0, population.length);       //copy offSpring to population

        return population;
    }

    public static Individual[] crossOver(Individual[] population) {

        for (int i = 0; i < population.length; i++) {
            Random rand = new Random();
            int randomValue = rand.nextInt(100);
            if (randomValue < crossoverRate) {
                Individual tempOffspring = new Individual();
                int crossoverPoint = rand.nextInt(population[i].getGeneSize());
                System.arraycopy(population[i].gene, 0, tempOffspring.gene, 0, population[i].getGeneSize());    //set temp to individual i
                System.arraycopy(population[i + 1].gene, 0, population[i].gene, 0, crossoverPoint);     //copy head of i+1 to i
                System.arraycopy(tempOffspring.gene, 0, population[i + 1].gene, 0, crossoverPoint);     //copy head of i to i+1
            }
            i++;
        }
        return population;
    }

    public static Individual[] mutation(Individual[] population) {

        for (Individual population1 : population) {
            for (int j = 0; j < population1.getGeneSize(); j++) {
                //loop through individual
                Random rand = new Random();
                //int randomValue = rand.nextInt(100);

                if ((rand.nextDouble() * 100) < mutationRate) {                 //mutation action bit
                    if ((j + 1) % ruleSize == 0) {
                        if (population1.getGene(j) == 0) {
                            population1.setGene(j, 1);
                        } else {
                            population1.setGene(j, 0);
                        }

                    } else {

                        population1.setGene(j, (rand.nextInt(2 - 0 + 1) + 0));  //mutate condition bits
                    }
                }
            }

        }

        return population;
    }

    public static Individual[] evaluateFitness(Individual[] population) throws IOException {
        for (Individual population1 : population) {
            population1.setFitness(0);
            Rule[] rules = new Rule[ruleNum]; //ruleNum = number of rules within a individual
            int point = 0;
            for (int k = 0; k < ruleNum; k++) {
                int[] individualRule = new int[ruleSize];
                System.arraycopy(population1.gene, point, individualRule, 0, ruleSize);
                rules[k] = new Rule(individualRule);
                point += ruleSize;
            }
            population1.setFitness(compareToFile(rules, population1.fitness));
        }
        return population;
    }

    public static Individual[] bestFitness(Individual[] population) {
        int bestFitness = 0;
        for (Individual population1 : population) {
            //loop through population to find fitness of each individual
            if (population1.getFitness() > bestFitness) {
                //find best fitness
                bestFitness = population1.getFitness();
            }
        }
        System.out.print(bestFitness + ", ");
        return population;
    }

    public static Individual[] meanFitness(Individual[] population) {

        int meanFitness = 0;

        for (Individual population1 : population) {
            //loop through population to find fitness of each individual
            meanFitness += population1.getFitness(); //find total fitness
        }
        meanFitness = meanFitness / population.length;                          //divide total fitness by population size to find mean
        System.out.print(meanFitness);

        return population;
    }

    public static int compareToFile(Rule[] rules, int fitness) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader("G:\\DataSet2\\src\\DataSet2\\data2.txt"));

        fitness = 0;
        reader.readLine(); // this will read the first line
        String currentLine = null;
        while ((currentLine = reader.readLine()) != null) { //loop will run from 2nd line
            int[] condition = new int[ruleSize - 1];
            int action;
            action = (int) Character.getNumericValue(currentLine.charAt(currentLine.length() - 1));
            for (int i = 0; i < (currentLine.length() - 2); i++) {
                condition[i] = (int) Character.getNumericValue(currentLine.charAt(i));
            }
            boolean match;
            for (Rule rule : rules) {
                match = true;
                for (int i = 0; i < rule.condition.length; i++) {
                    if (rule.condition[i] == 2) {
                        //match, move on
                    } else if (rule.condition[i] == condition[i]) {
                        //match, move on     
                    } else {
                        //no match, break
                        match = false;
                        break;
                    }
                }
                if (rule.action == action && match == true) {
                    fitness++;
                    break;
                } else if (match == true) {
                    break;

                }
            }
        }
        return fitness;
    }

    public static Individual[] getBestIndividual(Individual[] population) throws IOException {
        int bestFitness = 0;
        int pointer = 0;
        for (int i = 0; i < population.length; i++) {
            if (population[i].getFitness() > bestFitness) {
                bestFitness = population[i].getFitness();
                pointer = i;
            }
        }
        System.out.println("best individual with fitness: " + population[pointer].getFitness() + " | " + Arrays.toString(population[pointer].gene));
        return population;
    }

    public static Individual[] shuffle(Individual[] population) {       //shuffle rules within individual

        for (Individual population1 : population) {

            Random rand = new Random();

            if ((rand.nextDouble() * 100) < shuffleChance) {

                Individual tempOffspring = new Individual();
                
                int position = rand.nextInt((ruleNum - 1) + 1);
                
                System.arraycopy(population1.gene, 0, tempOffspring.gene, (ruleSize * (ruleNum - position)), (position * ruleSize));    //copy tail of individual to head of temp
                
                System.arraycopy(population1.gene, (position * ruleSize), tempOffspring.gene, 0, (ruleNum - position) * ruleSize);      //copy head of individual to tail of temp
      
                System.arraycopy(tempOffspring.gene, 0, population1.gene, 0, geneSize);     //copy temp to individual
                
                
            }

        }

        return population;
    }
}
