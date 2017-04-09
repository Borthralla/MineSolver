package Minesweeper;

import java.math.BigInteger;
import java.util.*;

/**
 * Created by Philip on 3/22/2017.
 */
public class TileSet {
    boolean isAssigned;
    int numBombs;
    List<Tile> members;
    Set<Tile> adjacentNumbers;
    int numTiles;
    BigInteger runningtotal;
    HashMap<Integer,BigInteger> localSolutionBombs;
    double probability;

    public TileSet(Set<Tile> adjacentNumbers, List<Tile> members) {
        this.members = members;
        this.adjacentNumbers = adjacentNumbers;
        this.numTiles = members.size();
        this.runningtotal = BigInteger.ZERO;
        this.probability = 0;
        localSolutionBombs = new HashMap<Integer, BigInteger>();
        numBombs = -1;
        isAssigned = false;

        for (Tile num : this.adjacentNumbers) {
            num.addTileSet(this);
        }
    }

    public void assign(int bombs) {
        this.isAssigned = true;
        numBombs = bombs;
    }

    public void unassign() {
        this.isAssigned = false;
        this.numBombs = -1;
    }

    public int numBombs() {
        return this.numBombs;
    }

    public int maximum() {
        int result = this.numTiles;
        for (Tile n : adjacentNumbers) {
            result = Math.min(result, n.remainingBombs());
        }
        return result;
    }

    public void addToLocalSolutionBombs(int numBombs, BigInteger combinations) {
        if (combinations.equals(BigInteger.ZERO)) {
            return;
        }
        localSolutionBombs.putIfAbsent(numBombs, BigInteger.ZERO);
        BigInteger toAdd = localSolutionBombs.get(numBombs);
        localSolutionBombs.replace(numBombs,toAdd.add(combinations));
    }

    public int minimum() {
        int result = 0;
        /*
        for (Tile num : adjacentNumbers) {
            int numbombs = 0;
            int unassigned = 0;
            for (TileSet ts : num.tileSetRadius()) {
                if (!ts.isAssigned) {
                    unassigned += 1;
                }
                else {
                    numbombs += ts.numBombs();
                }
            }
            if (unassigned == 1) {
                result = num.getValue() - numbombs;
            }
        }
        */
        return result;
    }

    public int hash() {
        return Objects.hash(adjacentNumbers);
    }


    public boolean equals(Object that) {
        if (that instanceof TileSet) {
            return ((TileSet) that).adjacentNumbers.equals(this.adjacentNumbers);
        }
        return false;
    }

    public void addToRunningTotal(int numBombs, BigInteger total) {
        if (localSolutionBombs.containsKey(numBombs)) {
            this.runningtotal = runningtotal.add(localSolutionBombs.get(numBombs).multiply(total));
        }
    }

    public void setProbabilities(BigInteger totalSolutions) {
        if (runningtotal.equals(BigInteger.ZERO)) {
            for (Tile t : members) {
                t.isSafe = true;
                t.setProbability(0.0);
            }
            return;
        }
        double probability = Combinatorics.approximateDivide(runningtotal,totalSolutions) / numTiles;
        for (Tile t : members) {
            t.setProbability(probability);
        }
    }

    public String toString() {
        String result = "[";
        for (Tile t : members) {
            result = result + t.getPosn() + ",";
        }
        return result + "]";
    }


}
