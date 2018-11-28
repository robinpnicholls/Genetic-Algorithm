/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UCI;

import static UCI.GA.ruleNum;
import static UCI.GA.ruleSize;
import java.util.Arrays;

/**
 *
 * @author Robin
 */
public class Individual {

    public static final int genomeSize = (ruleSize * ruleNum);                  //gene size

    Rule[] rule = new Rule[ruleNum];                                            //number of rules per individual
    int fitness;

    Individual() {
        for (int j = 0; j < ruleNum; j++) {
            this.rule[j] = new Rule();
        }
        this.fitness = 0;
    }
    
    //copy constructor
    Individual(Rule[] rule, int fitness) {
        this.rule = rule;
        this.fitness = fitness;
    }
    
    //copy constructor
    Individual(Individual individual) {
        this(helperMethod(individual), individual.getFitness());
    }
    
    //copy constructor helpMethod (error workaround)
    private static Rule[] helperMethod(Individual individual) {
        Rule[] tempRules = Arrays.stream(individual.getRule())
                .map(rule -> rule == null ? null : new Rule(rule))
                .toArray(Rule[]::new);
        return tempRules;
    }

    public Rule getRule(int index) {
        return rule[index];
    }

    public int getGeneSize() {
        return genomeSize;
    }

    public int getFitness() {
        return fitness;
    }

    public void setFitness(int value) {
        this.fitness = value;
    }

    public Rule[] getRule() {
        return rule;
    }
}
