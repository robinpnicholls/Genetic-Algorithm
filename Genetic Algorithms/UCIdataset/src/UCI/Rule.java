/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UCI;

import static UCI.GA.ruleSize;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Robin
 */
public class Rule {

    Gene[] condition = new Gene[ruleSize - 1];
    int action;

    Rule() {

        for (int j = 0; j < ruleSize-1; j++) {
            this.condition[j] = new Gene();
        }
        this.action = ThreadLocalRandom.current().nextInt(1, 4 + 1);

    }
    
    //copy constructor
    Rule(Gene[] condition, int action) {
        this.condition = condition;
        this.action = action;
    }

    //copy constructor
    Rule(Rule rule) {
        this(helperMethod(rule), rule.getAction());

    }

    //copy constructor helpMethod (error workaround)
    private static Gene[] helperMethod(Rule rule) {
        Gene[] tempCondition = Arrays.stream(rule.getCondition())
                .map(gene -> gene == null ? null : new Gene(gene))
                .toArray(Gene[]::new);
        return tempCondition;
    }

    public int getAction() {
        return action;
    }

    public Gene[] getCondition() {
        return condition;
    }

    public void setAction(int value) {
        this.action = value;
    }
    
    public int getConditionSize() {
        return condition.length;
    }

}
