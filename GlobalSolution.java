package Minesweeper;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Philip on 3/27/2017.
 */
public class GlobalSolution {
    List<NumberSetAssignment> assignments;
    BigInteger combinations;

    public GlobalSolution(List<NumberSetAssignment> assignments) {
        this.assignments = assignments;
        combinations = BigInteger.ONE;
        HashMap<NumberSet, BigInteger> allcombinations = new HashMap<NumberSet, BigInteger>();
        for (NumberSetAssignment assignment : assignments) {
            BigInteger comb = assignment.numberset.numSolutionsWithBombs(assignment.numbombs);
            allcombinations.put(assignment.numberset,comb);
            combinations = combinations.multiply(comb);
            if (comb.equals(BigInteger.ZERO)) {
                System.out.println("Rare encounter with disjoint numberset!");
                return;
            }
        }
        for (NumberSetAssignment assignment : assignments) {
            assignment.numberset.pushGlobalCombinations(combinations.divide(allcombinations.get(assignment.numberset)), assignment.numbombs);
        }
    }

}
