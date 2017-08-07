package Minesweeper;

import java.math.BigInteger;
import java.util.*;

/**
 * Created by Philip on 3/23/2017.
 */
public class NumberSet {


    List<Tile> members;
    HashMap<Integer, BigInteger> numLocalSolutions;
    HashSet<TileSet> tileSets;
    int numsearched = 0;
    Set<TileSet> boundary;
    Board board;

    public NumberSet(List<Tile> numbers) {
        this.members = numbers;
        this.numLocalSolutions = new HashMap<Integer, BigInteger>();
        HashSet<TileSet> tileSets = new HashSet<TileSet>();
        this.boundary = new HashSet<TileSet>();
        for (Tile num : members) {
            for (TileSet ts : num.tileSetRadius()) {
                tileSets.add(ts);
            }
        }
        this.tileSets = tileSets;
    }

    public NumberSet(List<Tile> numbers, Board board) {
        this.members = numbers;
        this.numLocalSolutions = new HashMap<Integer, BigInteger>();
        HashSet<TileSet> tileSets = new HashSet<TileSet>();
        this.boundary = new HashSet<TileSet>();
        for (Tile num : members) {
            for (TileSet ts : num.tileSetRadius()) {
                tileSets.add(ts);
            }
        }
        this.tileSets = tileSets;
        this.board = board;
    }

    public void addToBoundary(Tile number) {
        if(number.searched){
            throw new IllegalArgumentException("Cannot add number which has already been searched to boundary");
        }
        for (TileSet ts : number.tileSetRadius()) {
            ts.numAdjacentExterior--;
            ts.numAdjacentInterior++;
            if (ts.numAdjacentExterior == 0) {
                boundary.remove(ts);
            }
            else {
                boundary.add(ts);
            }
        }
        number.searched = true;
    }

    public void removeFromBoundary(Tile number) {
        if(number.searched) {
            for (TileSet ts : number.tileSetRadius()) {
                if (ts.numAdjacentExterior == 0) {
                    boundary.add(ts);
                }
                if (ts.numAdjacentInterior == 1) {
                    boundary.remove(ts);
                }
                ts.numAdjacentExterior++;
                ts.numAdjacentInterior--;
            }
            number.searched = false;
        }
    }

    public List<List<TileSet>> boundarySuperGroups(){
        Map<Set<Tile>,List<TileSet>> map = new HashMap<Set<Tile>,List<TileSet>>();
        List<List<TileSet>> result = new ArrayList<List<TileSet>>();
        for (TileSet ts : boundary) {
            Set<Tile> exteriorNumbers = new HashSet<Tile>();
            for (Tile num : ts.adjacentNumbers) {
                if (!num.searched) {
                    exteriorNumbers.add(num);
                }
            }
            map.putIfAbsent(exteriorNumbers,new ArrayList<TileSet>());
            map.get(exteriorNumbers).add(ts);
        }
        for (List<TileSet> tsgroup : map.values()) {
            result.add(tsgroup);
        }
        return result;
    }

    public int metaBoundarySize(Tile num) {
        addToBoundary(num);
        int result = boundarySuperGroups().size();
        removeFromBoundary(num);
        return result;
    }

    public void dynamicFillLocalSolutions() throws Exception {
        int totalSearched = 0;
        Collection<LocalSubSolution> allPossibilities = new ArrayList<LocalSubSolution>();
        allPossibilities.add(new LocalSubSolution());
        if (members.size() == 0) {
            return;
        }

        while (totalSearched < members.size()) {
            int numToGo = allPossibilities.size();
            //System.out.println("Numbers to search: " + (members.size() - totalSearched) + " Possibilities: " + numToGo);
            Map<List<Integer>, LocalSubSolution> allPossibilitiesMap = new HashMap<List<Integer>, LocalSubSolution>();
            Tile toSearch = members.get(0);
            int smallestSuperGroupSize = 10000;
            List<List<TileSet>> smallestSuperGroup = new ArrayList<List<TileSet>>();
            for (Tile num : members) {
                if (!num.searched) {
                    addToBoundary(num);
                    List<List<TileSet>> metaBoundary = boundarySuperGroups();
                    if (metaBoundary.size() < smallestSuperGroupSize) {
                        toSearch = num;
                        smallestSuperGroupSize = metaBoundary.size();
                        smallestSuperGroup = metaBoundary;
                    }
                    removeFromBoundary(num);
                }
            }
            addToBoundary(toSearch);
            for (LocalSubSolution possibility : allPossibilities){
                possibility.addAllExtensions(toSearch,smallestSuperGroup, allPossibilitiesMap);
                numToGo--;
                if (numToGo % 5000 == 0) {
                    //System.out.println(numToGo);
                }
            }
            allPossibilities = allPossibilitiesMap.values();
            totalSearched++;
        }
        for (LocalSubSolution solution : allPossibilities){

                numLocalSolutions.putIfAbsent(solution.totalBombs, BigInteger.ZERO);
                BigInteger newvalue =  numLocalSolutions.get(solution.totalBombs).add(solution.combinations);
                numLocalSolutions.replace(solution.totalBombs, newvalue);
                for (Map.Entry<TileSet, BigInteger> runningTotalEntry : solution.runningtotals.entrySet()) {
                    runningTotalEntry.getKey().addToLocalSolutionBombs(solution.totalBombs, runningTotalEntry.getValue());
                }
        }
        for (Tile n: members) {
            n.searched = false;
        }

    }

    public void addLocalSolution(LocalSolution sol) {
        int numBombs = sol.numBombs();
        numLocalSolutions.putIfAbsent(numBombs, BigInteger.ZERO);
        BigInteger toAdd = numLocalSolutions.get(numBombs);
        numLocalSolutions.replace(numBombs, toAdd.add(sol.variations));
    }

    public void fillLocalSolutions() {

        //numsearched++;
        //int numSearched = getNumsearched();

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
                forcedNumbers.add(num);
                for (Assignment assignment : allPossibilities.get(0)) {
                    forcedAssignments.add(assignment);
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
            for (TileSet ts : this.tileSets) {
                assignments.add(new Assignment(ts, ts.numBombs()));
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

    boolean isAdjacentToSearchedTile(Tile t) {
        for (Tile adj : board.radius(t.getPosn(),tile -> tile.isCovered())){
            for (Tile adjnum : board.radius(adj.getPosn(),tile -> tile.searched && tile.isNumber())) {
                return true;
            }
        }
        return false;
    }

    int getNumsearched(){
        int result = 0;
        for (Tile num : members) {
            if (num.searched){
                result++;
            }
        }
        return result;
    }


    BigInteger numSolutionsWithBombs(int numbombs) {
        if (members.size() == 0 && !numLocalSolutions.containsKey(numbombs)) {
            for (TileSet ts : tileSets) {
                List<Assignment> assignments = new ArrayList<Assignment>();
                assignments.add(new Assignment(ts, numbombs));
                LocalSolution sol = new LocalSolution(assignments);
                addLocalSolution(sol);
                return sol.variations;
            }
        }

        if (numLocalSolutions.containsKey(numbombs)) {
            return numLocalSolutions.get(numbombs);
        } else {
            return BigInteger.ZERO;
        }
    }

    int minimum() {
        if (members.size() == 0) {
            return 0;
        }
        int result = 0;
        for (int numbombs : numLocalSolutions.keySet()) {
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
        for (int numbombs : numLocalSolutions.keySet()) {
            result = Math.max(result, numbombs);
        }
        return result;
    }

    public void pushGlobalCombinations(BigInteger combinations, int numbombs) {
        for (TileSet ts : tileSets) {
            ts.addToRunningTotal(numbombs, combinations);
        }
    }

    public String toString() {
        String result = "";
        for (Tile t : members) {
            result = result + t.getPosn() + ",";
        }
        return result;
    }

    public void setLocalProbability() throws Exception {
        if (numLocalSolutions.size() > 1) {
            throw new Exception("Local solution can have more than 1 amount of bombs");
        }
        int numbombs = 0;
        BigInteger totalcombinations = BigInteger.ZERO;
        for (Map.Entry<Integer, BigInteger> entry : numLocalSolutions.entrySet()) {
            numbombs = entry.getKey();
            totalcombinations = entry.getValue();
        }
        for (TileSet ts : tileSets) {
            ts.addToRunningTotal(numbombs, BigInteger.ONE);
            ts.setProbabilities(totalcombinations);
        }
    }


}
