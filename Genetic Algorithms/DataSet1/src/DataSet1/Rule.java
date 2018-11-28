/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataSet1;

/**
 *
 * @author Robin
 */
public class Rule {

    public final static int ruleSize = 6;

    int[] condition = new int[ruleSize - 1];
    int action;

    Rule(int[] individualRule) {
        System.arraycopy(individualRule, 0, condition, 0, (ruleSize - 1));
        action = individualRule[ruleSize - 1];
        //System.out.println("rule = condition = " + Arrays.toString(condition) + " action = " + action);
    }

    public int getAction() {
        return action;
    }

    public int[] getCondition() {
        return condition;
    }

    public void setAction(int value) {
        this.action = value;
    }

    public void setCondition(int[] newCondition) {
        System.arraycopy(newCondition, 0, this.condition, 0, newCondition.length);
    }
}
