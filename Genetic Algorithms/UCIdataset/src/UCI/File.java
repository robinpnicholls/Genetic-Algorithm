/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UCI;

import static UCI.GA.ruleSize;

/**
 *
 * @author r25-nicholls
 */
public class File {

    int[] line = new int[ruleSize];

    File(int[] line) {
        this.line = line;
    }

}
