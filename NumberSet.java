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
        List<Assignment> forcedAssignments = new ArrayList<Assignment>();
        List<Tile> forcedNumbers = new ArrayList<Tile>();
        for (Tile num : members) {
            if (num.searched) {
                continue;
            }

            List<List<Assignment>> allPossibilities = num.allPossibilities();
            int numpossibilities = allPossibilities.size();
            if (numpossibilities == 0) {
                //numsearched--;
                for (Assignment assignment : forcedAssignments) {
                    assignment.tileset.unassign();
                }
                for (Tile forced : forcedNumbers) {
                    forced.searched = false;
                }
                return;
            }
            if (numpossibilities == 1) {
                for (Assignment assignment : allPossibilities.get(0)) {
                    forcedAssignments.add(assignment);
                    forcedNumbers.add(num);
                    assignment.tileset.assign(assignment.numbombs);
                }
                num.searched = true;
                continue;
            }
            allsearched = false;
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
            for (Assignment assignment : forcedAssignments) {
                assignment.tileset.unassign();
            }
            for (Tile forced : forcedNumbers) {
                forced.searched = false;
            }
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
        for (Assignment assignment : forcedAssignments) {
            assignment.tileset.unassign();
        }
        for (Tile forced : forcedNumbers) {
            forced.searched = false;
        }
        priority.searched = false;
    }


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
