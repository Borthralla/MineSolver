package Minesweeper;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Predicate;

/**
 * Created by Philip on 3/19/2017.
 */
public class Board {

    HashMap<Integer, Tile> tiles;
    int width;
    int height;
    int totalbombs;
    int remainingBombs;
    BigInteger totalSolutions;

    public Board(int width, int height, int totalbombs) {
        this.width = width;
        this.height = height;
        this.totalbombs = totalbombs;
        this.remainingBombs = totalbombs;
        this.totalSolutions = BigInteger.ZERO;
        this.tiles = new HashMap<Integer, Tile>();

        for (int i = 0; i < width * height; i++) {
            tiles.put(i, new Tile(i));
        }

    }




    /**
     * Clears the tile at the given position
     *
     * @param position 0 valued index of the tile
     */
    public void clearTile(int position) {
        tiles.get(position).clear();
    }

    public void coverTile(int position) {tiles.get(position).cover();}

    public void assignTile(int position, int number) {
        tiles.get(position).assignNumber(number);
    }

    public void assignValue(int position, int number) {tiles.get(position).assignValue(number);}

    public void assignBomb(int position) {
        tiles.get(position).makeBomb();
    }

    public void assignRandomValues() {
        List<Integer> tileposns = new ArrayList<Integer>();
        for (int i = 0; i < width * height; i++) {
            tileposns.add(i);
        }
        Combinatorics.shuffle(tileposns);
        for (int i = 0; i < totalbombs; i++) {
            //System.out.println("result.assignBomb(" + tileposns.get(i) + ");");
            assignBomb(tileposns.get(i));
        }
        for (int i = 0; i < width * height; i++) {
            if (!tiles.get(i).isBomb()) {
                int numBombs = radius(i, tile -> tile.isBomb()).size();
               // System.out.println("result.assignValue(" + i + ", " + numBombs + ");" );
                assignValue(i, numBombs);
            }
        }
    }

    public void assignRandomValuesWithZero(int clickpos) {
        List<Integer> tileposns = new ArrayList<Integer>();
        for (int i = 0; i < width * height; i++) {
            tileposns.add(i);
        }
        tileposns.remove(clickpos);
        for (Tile adj : radius(clickpos)) {
            tileposns.remove(Integer.valueOf(adj.getPosn()));
        }

        Combinatorics.shuffle(tileposns);
        for (int i = 0; i < totalbombs; i++) {
            //System.out.println("result.assignBomb(" + tileposns.get(i) + ");");
            assignBomb(tileposns.get(i));
        }
        for (int i = 0; i < width * height; i++) {
            if (!tiles.get(i).isBomb()) {
                int numBombs = radius(i, tile -> tile.isBomb()).size();
                //System.out.println("result.assignValue(" + i + ", " + numBombs + ");" );
                assignValue(i, numBombs);
            }
        }
    }

    /**
     * returns a list of all tiles adjacent to this tile, including numbers.
     *
     * @param posn 0 valued index of the tile
     * @return list of tiles around the tile at the given 0-valued position
     */
    public ArrayList<Tile> radius(int posn) {
        return radius(posn, Optional.empty());
    }

    /**
     * Returns a list of tiles adjacent to a given tile that satisfy a predicate
     *
     * @param posn position of the tile
     * @param pred whether or not to add the tile
     * @return the list of tiles adjacent to the given tile that satisfy the predicate
     */
    public ArrayList<Tile> radius(int posn, Predicate<Tile> pred) {
        return radius(posn, Optional.of(pred));
    }

    public ArrayList<Tile> allnumbers() {
        ArrayList<Tile> result = new ArrayList<Tile>();

        for (Tile t : tiles.values()) {
            if (t.isNumber()) {
                result.add(t);
            }
        }
        return result;
    }


    private ArrayList<Tile> radius(int posn, Optional<Predicate<Tile>> pred) {
        ArrayList<Tile> result = new ArrayList<Tile>();

        //please note that column and row start at zero to match array index notation.
        int col = posn % width;
        int row = posn / width;

        if (col > 0) {
            Tile toAdd = tiles.get(posn - 1);
            if (!(pred.isPresent()) || pred.get().test(toAdd)) {
                result.add(toAdd);
            }
        }

        if (col < width - 1) {
            Tile toAdd = tiles.get(posn + 1);
            if (!(pred.isPresent()) || pred.get().test(toAdd)) {
                result.add(toAdd);
            }
        }

        if (row > 0) {
            Tile toAdd = tiles.get(posn - width);
            if (!(pred.isPresent()) || pred.get().test(toAdd)) {
                result.add(toAdd);
            }
            if (col > 0) {
                Tile toAdd2 = tiles.get(posn - width - 1);
                if (!(pred.isPresent()) || pred.get().test(toAdd2)) {
                    result.add(toAdd2);
                }
            }

            if (col < width - 1) {
                Tile toAdd3 = tiles.get(posn - width + 1);
                if (!(pred.isPresent()) || pred.get().test(toAdd3)) {
                    result.add(toAdd3);
                }
            }
        }

        if (row < height - 1) {
            Tile toAdd = tiles.get(posn + width);
            if (!(pred.isPresent()) || pred.get().test(toAdd)) {
                result.add(toAdd);
            }
            if (col > 0) {
                Tile toAdd2 = tiles.get(posn + width - 1);
                if (!(pred.isPresent()) || pred.get().test(toAdd2)) {
                    result.add(toAdd2);
                }
            }

            if (col < width - 1) {
                Tile toAdd3 = tiles.get(posn + width + 1);
                if (!(pred.isPresent()) || pred.get().test(toAdd3)) {
                    result.add(toAdd3);
                }
            }
        }

        return result;
    }

    public void reveal(int posn) {
        Tile toClear = tiles.get(posn);
        if (toClear.isBomb() || !toClear.isAssigned()) {
            tiles.get(posn).isFlagged = true;
            return;
        }
        clearTile(posn);
        if (toClear.getValue() == 0) {
            for (Tile t : radius(posn,tile -> tile.isCovered())) {
                reveal(t.getPosn());
            }
        }
    }

    public boolean revealLowest() {
        Tile lowest = null;
        double lowestProbability = 1.00;
        boolean hasZero = false;
        boolean hasAny = false;
        boolean result = true;
        for (Tile t : tiles.values()) {
            if (t.isNumber() || t.isFlagged) {
                continue;
            }

            if (t.probability == 0 ) {
                reveal(t.getPosn());
                hasZero = true;
            }
            else {
                if (t.probability < lowestProbability) {
                    hasAny = true;
                    lowestProbability = t.probability;
                    lowest = t;
                }
            }
        }

        if (!hasZero && hasAny) {
            reveal(lowest.getPosn());
            if (lowest.isFlagged) {
                return false;
            }
            return true;
        }
        if (hasZero) {
            return true;
        }
        return false;
    }



    public ArrayList<NumberSet> numberSets() {
        ArrayList<ArrayList<Tile>> result = new ArrayList<ArrayList<Tile>>();
        HashMap<Tile, Boolean> numberspassed = new HashMap<Tile, Boolean>();

        for (Tile t : tiles.values()) {
            if (t.isNumber()) {
                numberspassed.put(t, false);
            }
        }

        for (Tile n : numberspassed.keySet()) {
            if (!(numberspassed.get(n))) {
                ArrayList<Tile> connectedNumbers = new ArrayList<Tile>();
                Deque<Tile> tileStack = new ArrayDeque<Tile>();
                tileStack.addFirst(n);
                connectedNumbers.add(n);
                numberspassed.replace(n,true);
                while (tileStack.size() > 0) {
                    Tile branch = tileStack.removeFirst();
                    for (Tile adj : newAdjacentNumbers(branch, numberspassed)) {
                        connectedNumbers.add(adj);
                        tileStack.addFirst(adj);
                    }
                }
                result.add(connectedNumbers);
            }
        }

        ArrayList<NumberSet> numbersets = new ArrayList<NumberSet>();
        for (ArrayList<Tile> tiles : result) {
            numbersets.add(new NumberSet(tiles));
        }

        return numbersets;
    }


    private ArrayList<Tile> newAdjacentNumbers(Tile numTile, HashMap<Tile, Boolean> numberspassed) {
        ArrayList<Tile> result = new ArrayList<Tile>();
        for (Tile c : radius(numTile.getPosn(), tile -> !tile.isCleared())) {
            for (Tile n : radius(c.getPosn(), tile -> tile.isNumber() && !numberspassed.get(tile))) {
                numberspassed.replace(n, true);
                result.add(n);
            }
        }
        return result;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Tile t : tiles.values()) {
            if (t.isNumber()) {
                result.append("  " + Integer.toString(t.getValue()) + "   ");
            } else {
                result.append((" " + Double.toString(t.probability) + "00").substring(0,5)
                        + " ");
            }
            if (t.getPosn() % width == width - 1) {
                result.append("\n");
            }
        }
        return result.toString();
    }

    public List<TileSet> tileSets() {
        ArrayList<TileSet> result = new ArrayList<TileSet>();

        HashMap<HashSet<Tile>, List<Tile>> map = new HashMap<HashSet<Tile>, List<Tile>>();
        for (Tile t : tiles.values()) {
            if (t.isCovered()) {
                HashSet<Tile> adjacentNumbers = new HashSet<Tile>(radius(t.getPosn(), tile -> tile.isNumber()));
                map.putIfAbsent(adjacentNumbers,new ArrayList<Tile>());
                map.get(adjacentNumbers).add(t);
            }
        }

        for (Map.Entry<HashSet<Tile>, List<Tile>> entry : map.entrySet()) {
            TileSet toadd  = new TileSet(entry.getKey(),entry.getValue());
            result.add(toadd);
        }


        return result;
    }

    public void resetNumbers() {
        for (Tile t : tiles.values()) {
            t.resetTileSetRadius();
            if (t.isAssigned()) {
                t.remainingValue = t.getValue();
            }
        }
    }

    public void findProbabilities() throws Exception {
        this.totalSolutions = BigInteger.ZERO;

        List<TileSet> tilesets = this.tileSets();
        List<NumberSet> numbersets = this.numberSets();
        for (TileSet ts : tilesets) {
            if (ts.adjacentNumbers.size() == 0) {
                NumberSet blankNumberSet = new NumberSet(new ArrayList<Tile>());
                blankNumberSet.tileSets.add(ts);

                numbersets.add(blankNumberSet);
                break;
            }
        }
        List<MinMaxPair> minmaxpairs = new ArrayList<MinMaxPair>();
        int remainingBombs = totalbombs;
        List<NumberSetAssignment> defaultAssignments = new ArrayList<NumberSetAssignment>();
        List<NumberSet> nontrivials = new ArrayList<NumberSet>();
        for (NumberSet ns : numbersets) {
            ns.fillLocalSolutions();
            if (ns.members.size() > 0 && ns.numLocalSolutions.keySet().size() == 0) {
                throw new Exception("Invalid Board " + Integer.toString(ns.members.get(0).getPosn()));
            }
            int min = ns.minimum();
            int max = ns.maximum();
            if (min == max) {
                remainingBombs -= min;
                defaultAssignments.add(new NumberSetAssignment(ns,min));
            }
            else {
                nontrivials.add(ns);
                minmaxpairs.add(new MinMaxPair(min, max));
            }
        }

        System.out.println("Search done!");
        System.out.println(minmaxpairs + ", " + remainingBombs);
        List<List<Integer>> allcombinations = Combinatorics.subsetSum(minmaxpairs,remainingBombs);
        int numcombinations = allcombinations.size();
        for (List<Integer> assignment : allcombinations) {
            List<NumberSetAssignment> assignments = new ArrayList<NumberSetAssignment>(defaultAssignments);
            for (int i = 0; i < assignment.size(); i++) {
                NumberSetAssignment nsa = new NumberSetAssignment(nontrivials.get(i),assignment.get(i));
                assignments.add(nsa);
            }
            GlobalSolution globalsolution = new GlobalSolution(assignments);
            totalSolutions = totalSolutions.add(globalsolution.combinations);
            if (numcombinations % 10000 == 0) {System.out.println(numcombinations);}
            numcombinations--;
        }

        if (totalSolutions.equals(BigInteger.ZERO)) {
            throw new Exception("Board is invalid");
        }
        for (TileSet ts : tilesets) {
            ts.setProbabilities(totalSolutions);
        }
        resetNumbers();
    }

    public void tryFindProbabilities() {
        try {
            findProbabilities();
        }
        catch(Exception e) {
            reset();
        }
    }

    public void reset() {
        this.totalSolutions = BigInteger.ZERO;
        this.remainingBombs = totalbombs;
        resetNumbers();
    }

    public ArrayList<NumberSet> bombSeparatedNumberSets() {
        ArrayList<ArrayList<Tile>> result = new ArrayList<ArrayList<Tile>>();
        HashMap<Tile, Boolean> numberspassed = new HashMap<Tile, Boolean>();

        for (Tile t : tiles.values()) {
            if (t.isNumber()) {
                numberspassed.put(t, false);
            }
        }

        for (Tile n : numberspassed.keySet()) {
            if (!(numberspassed.get(n))) {
                ArrayList<Tile> connectedNumbers = new ArrayList<Tile>();
                Deque<Tile> tileStack = new ArrayDeque<Tile>();
                tileStack.addFirst(n);
                connectedNumbers.add(n);
                numberspassed.replace(n,true);
                while (tileStack.size() > 0) {
                    Tile branch = tileStack.removeFirst();
                    for (Tile adj : bombSeparatedAdjacentNumbers(branch, numberspassed)) {
                        connectedNumbers.add(adj);
                        tileStack.addFirst(adj);
                    }
                }
                result.add(connectedNumbers);
            }
        }

        ArrayList<NumberSet> numbersets = new ArrayList<NumberSet>();
        for (ArrayList<Tile> tiles : result) {
            numbersets.add(new NumberSet(tiles));
        }

        return numbersets;
    }

    private ArrayList<Tile> bombSeparatedAdjacentNumbers(Tile numTile, HashMap<Tile, Boolean> numberspassed) {
        ArrayList<Tile> result = new ArrayList<Tile>();
        for (Tile c : radius(numTile.getPosn(), tile -> !tile.isCleared())) {
            if (c.probability == 1.0) {
                numTile.remainingValue--;
                continue;
            }
            for (Tile n : radius(c.getPosn(), tile -> tile.isNumber() && !numberspassed.get(tile))) {
                numberspassed.replace(n, true);
                result.add(n);
            }
        }
        return result;
    }

    public List<TileSet> bombSeparatedtileSets() {
        ArrayList<TileSet> result = new ArrayList<TileSet>();

        HashMap<HashSet<Tile>, List<Tile>> map = new HashMap<HashSet<Tile>, List<Tile>>();
        for (Tile t : tiles.values()) {
            if (t.isCovered()) {
                if (t.probability == 1) {
                    remainingBombs--;
                    continue;
                }
                HashSet<Tile> adjacentNumbers = new HashSet<Tile>(radius(t.getPosn(), tile -> tile.isNumber()));
                map.putIfAbsent(adjacentNumbers,new ArrayList<Tile>());
                map.get(adjacentNumbers).add(t);
            }
        }

        for (Map.Entry<HashSet<Tile>, List<Tile>> entry : map.entrySet()) {
            TileSet toadd  = new TileSet(entry.getKey(),entry.getValue());
            result.add(toadd);
        }


        return result;
    }

    public void findBombSeparatedProbabilities() throws Exception {
        this.totalSolutions = BigInteger.ZERO;

        List<TileSet> tilesets = this.bombSeparatedtileSets();
        List<NumberSet> numbersets = this.bombSeparatedNumberSets();
        for (TileSet ts : tilesets) {
            if (ts.adjacentNumbers.size() == 0) {
                NumberSet blankNumberSet = new NumberSet(new ArrayList<Tile>());
                blankNumberSet.tileSets.add(ts);

                numbersets.add(blankNumberSet);
                break;
            }
        }
        List<MinMaxPair> minmaxpairs = new ArrayList<MinMaxPair>();
        int remainingBombs = this.remainingBombs;
        List<NumberSetAssignment> defaultAssignments = new ArrayList<NumberSetAssignment>();
        List<NumberSet> nontrivials = new ArrayList<NumberSet>();
        for (NumberSet ns : numbersets) {
            ns.fillLocalSolutions();
            if (ns.members.size() > 0 && ns.numLocalSolutions.keySet().size() == 0) {
                throw new Exception("Invalid Board " + Integer.toString(ns.members.get(0).getPosn()));
            }
            int min = ns.minimum();
            int max = ns.maximum();
            if (min == max) {
                remainingBombs -= min;
                defaultAssignments.add(new NumberSetAssignment(ns,min));
            }
            else {
                nontrivials.add(ns);
                minmaxpairs.add(new MinMaxPair(min, max));
            }
        }

        System.out.println("Search done!");
        System.out.println(minmaxpairs + ", " + remainingBombs);
        List<List<Integer>> allcombinations = Combinatorics.subsetSum(minmaxpairs,remainingBombs);
        int numcombinations = allcombinations.size();
        for (List<Integer> assignment : allcombinations) {
            List<NumberSetAssignment> assignments = new ArrayList<NumberSetAssignment>(defaultAssignments);
            for (int i = 0; i < assignment.size(); i++) {
                NumberSetAssignment nsa = new NumberSetAssignment(nontrivials.get(i),assignment.get(i));
                assignments.add(nsa);
            }
            GlobalSolution globalsolution = new GlobalSolution(assignments);
            totalSolutions = totalSolutions.add(globalsolution.combinations);
            if (numcombinations % 10000 == 0) {System.out.println(numcombinations);}
            numcombinations--;
        }

        if (totalSolutions.equals(BigInteger.ZERO)) {
            throw new Exception("Board is invalid");
        }
        for (TileSet ts : tilesets) {
            ts.setProbabilities(totalSolutions);
        }
        resetNumbers();
        this.remainingBombs = totalbombs;
    }
}