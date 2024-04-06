package Minesweeper;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Philip on 3/24/2017.
 */
public class LocalSolution {

   List<Assignment> assignments;
    BigInteger variations;
    int numbombs;

    public int numBombs() {
        return numbombs;
    }

    public LocalSolution(List<Assignment> assignments) {
        this.assignments = assignments;
        int numbombs = 0;
        variations = BigInteger.ONE;
        for (Assignment assignment : assignments) {
            numbombs += assignment.numbombs;
            variations = variations.multiply(Combinatorics.bigBinomial(assignment.tileset.numTiles, assignment.numbombs));
        }
        this.numbombs = numbombs;
        for (Assignment assignment : assignments) {
            assignment.tileset.addToLocalSolutionBombs(numbombs, variations.multiply(BigInteger.valueOf(assignment.numbombs)));
        }


    }


}
