package Minesweeper;

import java.math.BigInteger;
import java.util.*;

/**
 * Created by Philip on 3/23/2017.
 */
public class NumberSet {


    List<Tile> members;
    HashMap<Integer,ArrayList<LocalSolution>> localSolutions;
    HashSet<TileSet> tileSets;
    int numsearched = 0;

    public NumberSet(List<Tile> numbers) {
        this.members = numbers;
        this.localSolutions = new HashMap<Integer,ArrayList<LocalSolution>>();
        HashSet<TileSet> tileSets = new HashSet<TileSet>();
        for (Tile num : members) {
            for (TileSet ts : num.tileSetRadius()) {
                tileSets.add(ts);
            }
        }
        this.tileSets = tileSets;
    }

    public void addLocalSolution(LocalSolution sol) {
        int numBombs = sol.numBombs();
        localSolutions.putIfAbsent(numBombs,new ArrayList<LocalSolution>());
        localSolutions.get(numBombs).add(sol);
    }

    public void fillLocalSolutions() {

        //numsearched++;
        //int numSearched = this.numsearched;

        if (members.size() == 0) {
            return;
        }
        boolean allsearched = true;
        int lowestNumPossibilities = 10000;
        Tile priority = members.get(0);
        for (Tile num : members) {
            if (num.searched) {
                continue;
            }
            allsearched = false;
            int numpossibilities = num.allPossibilities().size();
            if (numpossibilities == 0) {
                numsearched--;
                return;
            }
            if (numpossibilities < lowestNumPossibilities) {
                priority = num;
                lowestNumPossibilities = numpossibilities;
            }
        }
        if (allsearched) {
            List<Assignment> assignments = new ArrayList<Assignment>();
            for (TileSet ts: this.tileSets) {
                assignments.add(new Assignment(ts,ts.numBombs()));
            }
            addLocalSolution(new LocalSolution(assignments));
            return;
        }

        priority.searched = true;
        for (List<Assignment> possibility : priority.allPossibilities()) {
            //numsearched = numSearched;
            for (Assignment assignment : possibility) {
                assignment.tileset.assign(assignment.numbombs);
            }
            fillLocalSolutions();
            for (Assignment assignment : possibility) {
                assignment.tileset.unassign();
            }
        }
        //System.out.println(numSearched);
        //numsearched--;
        priority.searched = false;
    }
    /*
    public void dumbFillLocalSolutions() {


        if (members.size() == 0) {
            return;
        }
        boolean allsearched = (numsearched == members.size() - 1);
        if (allsearched) {
            HashMap<TileSet, Integer> assignment = new HashMap<TileSet,Integer>();
            for (TileSet ts: this.tileSets) {
                assignment.put(ts,ts.numBombs());
            }
            addLocalSolution(new LocalSolution(assignment));
            return;
        }
        Tile priority = members.get(numsearched);
        numsearched++;


        List<TileSet> toAssign = new ArrayList<TileSet>();
        for (TileSet ts: priority.tileSetRadius()) {
            if (!ts.isAssigned) {
                toAssign.add(ts);
            }
        }
        priority.searched = true;
        for (HashMap<TileSet, Integer> possibility : priority.allPossibilities()) {
            for (Map.Entry<TileSet, Integer> pair : possibility.entrySet()) {
                pair.getKey().assign(pair.getValue());
            }
            dumbFillLocalSolutions();
            for (TileSet ts : toAssign) {
                ts.unassign();
            }
        }
        numsearched--;
        priority.searched = false;
    }
    */

    BigInteger numSolutionsWithBombs(int numbombs) {
        if (members.size() == 0 && !localSolutions.containsKey(numbombs)) {
            for (TileSet ts : tileSets) {
                List<Assignment> assignments = new ArrayList<Assignment>();
                assignments.add(new Assignment(ts, numbombs));
                LocalSolution sol = new LocalSolution(assignments);
                addLocalSolution(sol);
                return sol.variations;
            }
        }
        BigInteger result = BigInteger.ZERO;
        if (localSolutions.containsKey(numbombs)) {
            for (LocalSolution sol : localSolutions.get(numbombs)) {
                result = result.add(sol.variations);
            }
        }

        return result;
    }

    int minimum() {
        if (members.size() == 0) {
            return 0;
        }
        int result = 0;
        for (int numbombs : localSolutions.keySet()) {
            if (result == 0) {
                result = numbombs;
            }
            result = Math.min(result, numbombs);
        }
        return result;
    }

    int maximum() {
        if (members.size() == 0) {
            for (TileSet ts : tileSets) {
                return ts.members.size();
            }
        }
        int result = 0;
        for (int numbombs : localSolutions.keySet()) {
            result = Math.max(result, numbombs);
        }
        return result;
    }

    public void pushGlobalCombinations(BigInteger combinations, int numbombs) {
        for (LocalSolution solution : localSolutions.get(numbombs)) {
            solution.pushGlobalCombinations(combinations);
        }
    }



}
