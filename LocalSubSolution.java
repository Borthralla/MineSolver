package Minesweeper;

import java.math.BigInteger;
import java.util.*;

public class LocalSubSolution {
    //Consider replacing all assignments field with only the assignments within the current boundary.
    Map<TileSet,Integer> assignments;
    Map<TileSet,BigInteger> runningtotals;
    BigInteger combinations;
    int totalBombs;

    public LocalSubSolution() {
        this.assignments = new HashMap<TileSet,Integer>();
        this.runningtotals = new HashMap<TileSet,BigInteger>();
        this.combinations = BigInteger.ONE;
        this.totalBombs = 0;
    }

    public LocalSubSolution(LocalSubSolution toCopy) {
        this.assignments = new HashMap<TileSet,Integer>(toCopy.assignments);
        this.runningtotals = new HashMap<TileSet,BigInteger>(toCopy.runningtotals);
        this.combinations = toCopy.combinations;
        this.totalBombs = toCopy.totalBombs;
    }

    public void merge(LocalSubSolution othersolution) {
        for (TileSet ts : this.assignments.keySet()){
            BigInteger sum = runningtotals.get(ts).add(othersolution.runningtotals.get(ts));
            runningtotals.replace(ts,sum);
        }
        combinations = combinations.add(othersolution.combinations);
    }

    public LocalSubSolution clone() {
        return new LocalSubSolution(this);
    }

    public void addAllExtensions(Tile num, List<List<TileSet>> metagroups, Map<List<Integer>,LocalSubSolution> toAdd) {
        for (TileSet ts : num.tileSetRadius()) {
            if (assignments.containsKey(ts)) {
                ts.assign(this.assignments.get(ts));
            }
        }
        for(List<Assignment> assignments : num.allPossibilities()){
            LocalSubSolution copy = this.clone();
            int numCombinations = 1;
            int newbombs = 0;
            for (Assignment assignment : assignments) {
                numCombinations *= Combinatorics.binomial(assignment.tileset.numTiles, assignment.numbombs);
                newbombs += assignment.numbombs;
            }
            for (TileSet ts : copy.runningtotals.keySet()) {
                BigInteger newval = copy.runningtotals.get(ts).multiply(BigInteger.valueOf(numCombinations));
                copy.runningtotals.replace(ts,newval);
            }
            copy.combinations = copy.combinations.multiply(BigInteger.valueOf(numCombinations));
            copy.totalBombs += newbombs;
            for(Assignment assignment : assignments) {
                copy.assignments.put(assignment.tileset,assignment.numbombs);
                copy.runningtotals.put(assignment.tileset,copy.combinations.multiply(BigInteger.valueOf(assignment.numbombs)));
            }
            List<Integer> metagroupcounts = new ArrayList<Integer>();
            metagroupcounts.add(copy.totalBombs);
            for (List<TileSet> metagroup : metagroups) {
                int metagroupcount = 0;
                for (TileSet ts : metagroup) {
                    metagroupcount += copy.assignments.get(ts);
                }
                metagroupcounts.add(metagroupcount);
            }

            if (!toAdd.containsKey(metagroupcounts)) {
                toAdd.put(metagroupcounts,copy);
            }
            else {
                toAdd.get(metagroupcounts).merge(copy);
            }
        }
        for (TileSet ts : num.tileSetRadius()) {
            ts.unassign();
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(assignments.toString());
        return builder.toString();
    }

}
