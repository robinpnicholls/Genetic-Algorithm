/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UCI;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Robin
 */
public class GA {

    public static double crossoverRate = 60;                                    // crossover rate as a percentage
    public static double mutationRate = 0.01;                                  //mutation rate as a percentage
    public static double shuffleChance = 10;                                    //chance for rules within a population to flip
    public static int gen = 1000;                                                //number of generation
    public static int pop = 100;                                                //populaiton size
    public static int ruleNum = 4;                                              //number of rules within a individual
    public static int ruleSize = 8;
    public static double step = 0.01;
    public static DecimalFormat df = new DecimalFormat("0.######");             //formatting of double to match decimal format on file
    public static Random random = new Random();
    public static double dataPoints = 2000;                                        //rows in file
    public static double training_testing_split = 50;
    public static File[] trainingSet = new File[(int) (dataPoints * (training_testing_split * 0.01))];                           //array of File[] to hold each row in file
    public static File[] testingSet = new File[(int) (dataPoints * (training_testing_split * 0.01))];
    private static Rule[] tempRules;                                            //tempory locations for copying
    private static Individual[] offSpring = new Individual[pop];                //create new temp offspring population 
    private static Individual bestIndividual;                                   //tempory locations for copying

    public static void main(String[] args) throws IOException {
        readFromFile();

        df.setRoundingMode(RoundingMode.DOWN);
        Individual[] population = new Individual[pop];                          //create array of individuals
        for (int i = 0; i < pop; i++) {                                         //loop through and create populaiton of pop size
            population[i] = new Individual();
        }

        //System.out.println("Initial Population");
        evaluateFitnessTraining(population);                                            //evaluate initial fitness of population
        meanFitness(population);
        bestFitness(population);
        System.out.println("");
        for (int i = 0; i < gen; i++) {                                         //select, crossover, mutate population Gen times
            evolve(population, i);
        }
        getBestIndividual(population);
    }

    public static Individual[] evolve(Individual[] population, int iteration) throws IOException {
        int fitness = 0;
        int p = 0;                                                              //pointer for best individual
        for (int k = 0; k < pop; k++) {
            if (population[k].getFitness() > fitness) {
                p = k;
                fitness = population[k].getFitness();                           //find pointer location for best individual
            }
        }

        bestIndividual = new Individual(population[p]);                         //copy best from population and save 
        selection(population);                                                  //run selection, crossover, mutation, and shuffle
        crossOver(population);
        if (iteration < 3500) {
            mutation(population);
        } else {
            mutationWithStep(population);
        }

        shuffle(population);
        System.out.print("\n" + (iteration + 1) + ": ");
        //evaluateFitness(population);                                            //evaluate fitness of population after evolution
        for (int k = 0; k < pop; k++) {
            if (population[k].getFitness() < fitness) {
                p = k;
                fitness = population[k].getFitness();                           //find pointer location for worst
            }
        }
        population[p] = new Individual(bestIndividual);                         //swap worst in populaiton for best saved before evolution
        evaluateFitnessTraining(population);

        bestFitness(population);
        meanFitness(population);
        evaluateFitnessTesting(population);
        return population;
    }

    public static Individual[] selection(Individual[] population) {                //evolve the population  

        for (int i = 0; i < population.length; i++) {
            int parent1 = random.nextInt(population.length);                    //pick two random individuals from the population
            int parent2 = random.nextInt(population.length);
            if (population[parent1].getFitness() >= population[parent2].getFitness()) { //if parent 1 has a better fitness than parent 2, add it to the offspring population
                offSpring[i] = new Individual(population[parent1]);
            } else {                                                            //else add parent two to the offSpring population
                offSpring[i] = new Individual(population[parent2]);
            }
        }
        for (int i = 0; i < pop; i++) {                                          //copy offSpring to population
            population[i] = new Individual(offSpring[i]);
        }
        return population;
    }

    public static Individual[] crossOver(Individual[] population) {             //crossover parent hread with following parent head(random crossover point)

        for (int i = 0; i < population.length; i++) {
            if (random.nextInt(100) < crossoverRate) {                          //with a chance equal to the crossover rate
                int crossoverPoint = random.nextInt(ruleNum - 1) + 1;            //identify point to crossover (head size)
                tempRules = new Rule[crossoverPoint];
                for (int k = 0; k < crossoverPoint; k++) {
                    tempRules[k] = new Rule(population[i].rule[k]);             //copy head of parent i to temp location
                }
                for (int k = 0; k < crossoverPoint; k++) {
                    population[i].rule[k] = new Rule(population[i + 1].rule[k]);//copy head of parent i+1 to head of parent i
                }
                for (int k = 0; k < crossoverPoint; k++) {
                    population[i + 1].rule[k] = new Rule(tempRules[k]);         //copy temp of parent i head to parent i+1 head
                }
            }
            if (i == population.length - 2) {
                break;                                                          //dont preform if parent i is last in population 
            }
        }
        return population;
    }

    public static Individual[] mutation(Individual[] population) {              //mutate a gene range or to hash
        for (Individual population1 : population) {                             //loop through individuals
            for (int j = 0; j < ruleNum; j++) {                                 //loop through individuals rules
                for (int k = 0; k < ruleSize - 1; k++) {                        //loop through individuals rules condition
                    if (random.nextDouble() < mutationRate) {                   //mutate based on mutation rate
                        if (random.nextDouble() < 0.33) {                       //33% chance gene mutates to/from a #
                            population1.rule[j].condition[k].isHash = population1.rule[j].condition[k].isHash != true;
                        }
//this upper/lower mutation will cause erratic jumping
                        int upperBound = ThreadLocalRandom.current().nextInt(population1.rule[j].condition[k].lowerBound, 99 + 1);
                        int lowerBound = ThreadLocalRandom.current().nextInt(1, population1.rule[j].condition[k].upperBound + 1);

                        population1.rule[j].condition[k].upperBound = upperBound;    //set gene upper bound to a value between the precious lower bound and max (1)
                        population1.rule[j].condition[k].lowerBound = lowerBound;    //set gene lower bound to a value between the precious upper bound and min (0)
                    }
                }
                if (random.nextDouble() < mutationRate) {

                    population1.rule[j].action = ThreadLocalRandom.current().nextInt(1, 4 + 1);

                }
            }
        }
        return population;
    }

    public static Individual[] mutationWithStep(Individual[] population) {              //mutate a gene range or to hash
        for (Individual population1 : population) {                             //loop through individuals
            for (int j = 0; j < ruleNum; j++) {                                 //loop through individuals rules
                for (int k = 0; k < ruleSize - 1; k++) {                        //loop through individuals rules condition
                    if (random.nextDouble() < mutationRate) {                   //mutate based on mutation rate
                        if (random.nextDouble() < 0.33) {                       //33% chance gene mutates to/from a #
                            population1.rule[j].condition[k].isHash = population1.rule[j].condition[k].isHash != true;
                        }
//this upper/lower mutation will cause small mutations within range step
                        int upperBound = ThreadLocalRandom.current().nextInt(population1.rule[j].condition[k].upperBound - 5, population1.rule[j].condition[k].upperBound + 5 + 1);
                        int lowerBound = ThreadLocalRandom.current().nextInt(population1.rule[j].condition[k].lowerBound - 5, population1.rule[j].condition[k].lowerBound + 5 + 1);

                        if (upperBound > 99) {
                            upperBound = 99;
                        }
                        if (lowerBound < 1) {
                            lowerBound = 1;
                        }
                        if (lowerBound > 99) {
                            lowerBound = 99;
                        }
                        if (upperBound < 1) {
                            upperBound = 1;
                        }
                        population1.rule[j].condition[k].upperBound = upperBound;    //set gene upper bound to a value between the precious lower bound and max (1)
                        population1.rule[j].condition[k].lowerBound = lowerBound;// bound to a value between the precious upper bound and min (0)
                    }
                }
                if (random.nextDouble() < mutationRate) {
                    if (population1.rule[j].action == 1) {
                        population1.rule[j].action = 0;
                    } else {
                        population1.rule[j].action = 1;
                    }
                }
            }
        }
        return population;
    }

    public static Individual[] evaluateFitnessTraining(Individual[] population) throws IOException {
        for (Individual population1 : population) {
            population1.setFitness(0);
            population1.setFitness(compareToFile(population1.rule, trainingSet));            //evaluate fitness for each individual
        }
        return population;
    }

    public static void evaluateFitnessTesting(Individual[] population) throws IOException {

        int[] allFitness = new int[pop];
        int bestFitness = 0;
        int meanFitness = 0;
        int p = 0;
        for (int i = 0; i < pop; i++) {
            allFitness[i] = compareToFile(population[i].rule, testingSet);            //evaluate fitness for each individual

            if (bestFitness < population[i].fitness) {
                bestFitness = population[i].fitness;
                p = i;
                
            }
            meanFitness = meanFitness + allFitness[i];
        }
        bestFitness = compareToFile(population[p].rule, testingSet);
        meanFitness = meanFitness / pop;
        System.out.print(bestFitness + "," + meanFitness);

        //return population;
    }

    public static Individual[] bestFitness(Individual[] population) {
        int bestFitness = 0;
        for (Individual population1 : population) {                             //loop through population to find fitness of each individual
            if (population1.getFitness() > bestFitness) {                       //find best fitness
                bestFitness = population1.getFitness();
            }
        }
        System.out.print(bestFitness + ",");
        return population;
    }

    public static Individual[] meanFitness(Individual[] population) {
        int meanFitness = 0;
        for (Individual population1 : population) {                             //loop through population to find fitness of each individual
            meanFitness += population1.getFitness(); //find total fitness
        }
        meanFitness = meanFitness / pop;                          //divide total fitness by population size to find mean
        System.out.print(meanFitness + ",");
        return population;
    }

    public static int compareToFile(Rule[] rules, File[] file) throws IOException {
        int fitness = 0;
        for (int j = 0; j < dataPoints * (training_testing_split * 0.01); j++) {                                  //for each row of the file

            boolean match;
            for (Rule rule : rules) {                                           //for each rule within the individual
                match = true;
                for (int i = 0; i < rule.condition.length; i++) {               //for each gene in the rule
                    if (rule.condition[i].isHash == true) {                     //if its a # move to next gene
                        //match, move on
                    } else if ((file[j].line[i] < (rule.condition[i].upperBound)) && (file[j].line[i] > (rule.condition[i].lowerBound))) {  //if the gene on file is within the upper and lower bound of the gene, then match and move on
                        //match, move on  
                    } else {                                                    //if there is no match then break and move to next rule within individual, as the individuals rule does not match the rule on file
                        //no match, break
                        match = false;
                        break;
                    }
                }
                if (rule.action == file[j].line[ruleSize - 1] && match == true) {          //if the condiitons match and the actions match then the rules match: increment fitness as one rule has passed
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
        System.out.println("best individual with fitness: " + population[pointer].getFitness() + " | ");
        for (Rule rule : population[pointer].rule) {
            for (Gene condition : rule.condition) {
                if (condition.isHash() == true) {
                    System.out.print(" #, ");
                } else {
                    System.out.print(" (" + condition.lowerBound + ":" + condition.upperBound + "), ");
                }
            }
            System.out.print(" " + rule.action);
        }
        return population;
    }

    public static Individual[] shuffle(Individual[] population) {               //shuffle the rules within a individual, this allows the GA to find better arrangments of rules and not get stuck
        tempRules = new Rule[ruleNum];
        for (Individual population1 : population) {
            if ((random.nextDouble() * 100) < shuffleChance) {                  //shuffle individual based on shuffleChance param
                int position = random.nextInt((ruleNum - 1) - 1) + 1;               //pick position to shuffle
                int k = 0;
                for (int i = position; i < ruleNum; i++) {
                    tempRules[k] = new Rule(population1.rule[i]);               //move tail of individual to head of temp
                    k++;
                }
                for (int i = 0; i < (position); i++) {
                    tempRules[k] = new Rule(population1.rule[i]);               //move head of individual to tail of temp
                    k++;
                    //i++;
                }
                for (int i = 0; i < (ruleNum); i++) {
                    population1.rule[i] = new Rule(tempRules[i]);               //replace individual with temp
                }
            }
        }
        return population;
    }

    public static void readFromFile() throws IOException {         //puts file into a Double[] object
        String strLine;
        BufferedReader reader = new BufferedReader(new FileReader("G:\\DataSet3\\src\\dataset3\\UCI_1.txt"));
        reader.readLine();
        strLine = reader.readLine();

        int i = 0;
        while (strLine != null && i < (dataPoints * (training_testing_split * 0.01))) {
            int line[] = new int[ruleSize];
            String[] strs = strLine.trim().split("\\s+");
            for (int j = 0; j < ruleSize; j++) {
                line[j] = Integer.parseInt(strs[j]);
            }
            trainingSet[i] = new File(line);
            i++;

            strLine = reader.readLine();
        }
        i = 0;
        while (strLine != null && i < (dataPoints * (training_testing_split * 0.01))) {
            int line[] = new int[ruleSize];
            String[] strs = strLine.trim().split("\\s+");
            for (int j = 0; j < ruleSize; j++) {
                line[j] = Integer.parseInt(strs[j]);
            }
            testingSet[i] = new File(line);
            i++;
            strLine = reader.readLine();
        }

    }

}
