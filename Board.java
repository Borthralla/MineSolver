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
    double expectedBombs;
    int bombsHit;
    int numFlags;

    public Board(int width, int height, int totalbombs) {
        this.expectedBombs = 0;
        this.bombsHit = 0;
        this.width = width;
        this.height = height;
        this.totalbombs = totalbombs;
        this.remainingBombs = totalbombs;
        this.totalSolutions = BigInteger.ZERO;
        this.tiles = new HashMap<Integer, Tile>();
        this.numFlags = 0;

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

    public void markBomb(int position) {tiles.get(position).markBomb();}

    public void unmarkBomb(int position) {tiles.get(position).unmarkBomb();}

    public void flagTile(int position) {
        Tile toFlag = tiles.get(position);
        if (!toFlag.isNumber() && !toFlag.isMarked) {
            if (toFlag.isFlagged) {
                numFlags--;
                toFlag.isFlagged = false;
            }
            else {
                numFlags++;
                toFlag.isFlagged = true;
            }
        }
    }

    public void chordTile(int position) {
        Tile toChord = tiles.get(position);
        if (toChord.isNumber() && radius(position,tile -> tile.isFlagged || tile.isMarked).size() == toChord.getValue()) {
            for (Tile c : radius(position, tile -> tile.isCovered())) {
                reveal(c.getPosn());
            }
        }
    }

    public void assignRandomValues() {
        List<Integer> tileposns = new ArrayList<Integer>();
        for (int i = 0; i < width * height; i++) {
            tileposns.add(i);
        }
        Collections.shuffle(tileposns);
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
    public void assignRandomValuesWithClear(int clickpos) {
        List<Integer> tileposns = new ArrayList<Integer>();
        for (int i = 0; i < width * height; i++) {
            tileposns.add(i);
        }
        tileposns.remove(clickpos);
        Collections.shuffle(tileposns);
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

        Collections.shuffle(tileposns);
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

    public void slowReveal(int posn) {
        Tile toClear = tiles.get(posn);
        if (toClear.isFlagged || toClear.isMarked) {
            return;
        }
        if (toClear.isCovered()) {
            expectedBombs = expectedBombs + toClear.probability;
        }
        if (toClear.isBomb() || !toClear.isAssigned()) {
            markBomb(posn);
            bombsHit++;
            return;
        }
        clearTile(posn);
        if (toClear.getValue() == 0) {
            for (Tile t : radius(posn,tile -> tile.isCovered())) {
                t.probability = 0;
                slowReveal(t.getPosn());
            }
        }
    }

    public void reveal(int posn) {
        Tile toClear = tiles.get(posn);
        if (toClear.isFlagged || toClear.isMarked || toClear.isNumber()) {
            return;
        }
        if (toClear.isCovered()) {
            expectedBombs = expectedBombs + toClear.probability;
        }
        if (toClear.isBomb() || !toClear.isAssigned()) {
            markBomb(posn);
            bombsHit++;
            return;
        }
        clearTile(posn);
        if (toClear.getValue() == 0) {
            Stack<Integer> toDo = new Stack<Integer>();
            toDo.add(posn);
            while (!toDo.isEmpty()) {
                int toReveal = toDo.pop();
                for (Tile t : radius(toReveal, tile -> tile.isCovered() && !tile.isFlagged)) {
                    t.probability = 0;
                    clearTile(t.getPosn());
                    if (t.getValue() == 0) {
                        toDo.add(t.getPosn());
                    }
                }
            }
        }
    }

    public int playerBombsLeft() {
        return totalbombs - numFlags - bombsHit;
    }

    public boolean adjacentPositions(int pos1, int pos2) {
        int x1 = pos1 % width;
        int x2 = pos2 % width;
        int y1 = pos1 / width;
        int y2 = pos2 / width;
        return Math.abs(x1 - x2) <= 1 && Math.abs(y1 - y2) <= 1;
    }


    public boolean revealLowest() {
        Tile lowest = null;
        double lowestProbability = 1.00;
        boolean hasZero = false;
        boolean hasAny = false;
        boolean result = true;
        for (Tile t : tiles.values()) {
            if (t.isNumber() || t.isFlagged || t.probability == 1.0) {
                continue;
            }

            if (t.isSafe ) {
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

    public boolean revealLowestNonDetermined() {
        Tile lowestDetermined = null;
        Tile lowestNonDetermined = null;
        double lowestDeterminedProbability = 1.00;
        double lowestNonDeterminedProbability = 1.00;
        boolean hasZero = false;
        boolean hasNonDetermined = false;
        boolean hasAny = false;
        boolean result = true;
        for (Tile t : tiles.values()) {
            if (t.isSafe && t.isCovered() && !t.isFlagged ) {
                hasZero = true;
                reveal(t.getPosn());
            }
        }
        if (!hasZero) {
            for (Tile t : tiles.values()) {
                if (t.isNumber() || t.isFlagged || t.probability == 1.0) {
                    continue;
                }
                 else {
                    if (!hasZero && t.probability < lowestNonDeterminedProbability) {
                        hasAny = true;
                        if (!isDetermined(t.getPosn())) {
                            hasNonDetermined = true;
                            lowestNonDeterminedProbability = t.probability;
                            lowestNonDetermined = t;
                        }
                        else {
                            if (t.probability < lowestDeterminedProbability) {
                                lowestDeterminedProbability = t.probability;
                                lowestDetermined = t;
                            }
                        }
                    }
                }
            }
        }
        if (!hasZero && hasAny) {
            Tile lowest = null;
            if (hasNonDetermined) {
                lowest = lowestNonDetermined;
            }
            else {
                lowest = lowestDetermined;
            }
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
            if (c.isMarked) {
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

    public String valueString() {
        StringBuilder result = new StringBuilder();
        for (Tile t : tiles.values()) {
            if (t.isBomb()) {
                result.append("x");
            }
            else {
                result.append(t.getValue());
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
                if (t.isMarked) {
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

    public void resetNumbers() {
        for (Tile t : tiles.values()) {
            t.resetTileSetRadius();
            t.isSafe = false;
            if (t.isAssigned()) {
                t.remainingValue = t.getValue();
            }
        }
    }

    public void softResetNumbers() {
        for (Tile t : tiles.values()) {
            t.resetTileSetRadius();
            if (t.isAssigned()) {
                t.remainingValue = t.getValue();
            }
        }
    }

    public void findProbabilities() throws Exception {
        resetNumbers();
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
        int remainingBombs = this.remainingBombs;
        List<NumberSet> nontrivials = new ArrayList<NumberSet>();
        BigInteger totalLocalSolutions = BigInteger.ONE;
        for (NumberSet ns : numbersets) {
            ns.dynamicFillLocalSolutions();
            if (ns.members.size() > 0 && ns.numLocalSolutions.keySet().size() == 0) {
                throw new Exception("Invalid Board " + Integer.toString(ns.members.get(0).getPosn()));
            }
            int min = ns.minimum();
            int max = ns.maximum();
            if (min == max) {
                remainingBombs -= min;
                ns.setLocalProbability();
                totalLocalSolutions = ns.numLocalSolutions.get(min).multiply(totalLocalSolutions);
            }
            else {
                nontrivials.add(ns);
                minmaxpairs.add(new MinMaxPair(min, max));
            }
        }

        //System.out.println("Search done!");
        //System.out.println(minmaxpairs + ", " + remainingBombs);
        List<List<Integer>> allcombinations = Combinatorics.fastSubsetSum(minmaxpairs,remainingBombs);
        int numcombinations = allcombinations.size();
        for (List<Integer> assignment : allcombinations) {
            List<NumberSetAssignment> assignments = new ArrayList<NumberSetAssignment>();
            for (int i = 0; i < nontrivials.size(); i++) {
                NumberSetAssignment nsa = new NumberSetAssignment(nontrivials.get(i),assignment.get(i + 1));
                assignments.add(nsa);
            }
            GlobalSolution globalsolution = new GlobalSolution(assignments);
            totalSolutions = totalSolutions.add(globalsolution.combinations);
            if (numcombinations % 100000 == 0) {System.out.println(numcombinations);}
            numcombinations--;
        }

        if (totalSolutions.equals(BigInteger.ZERO)) {
            throw new Exception("Board is invalid");
        }
        for (NumberSet nontrivial : nontrivials) {
            for (TileSet ts : nontrivial.tileSets) {
                ts.setProbabilities(totalSolutions);
            }
        }
        this.remainingBombs = totalbombs;
        totalSolutions = totalSolutions.multiply(totalLocalSolutions);
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
            numbersets.add(new NumberSet(tiles, this));
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
        //System.out.println("Search started...");
        this.totalSolutions = BigInteger.ZERO;
        resetNumbers();

        List<TileSet> tilesets = this.bombSeparatedtileSets();
        List<NumberSet> numbersets = this.bombSeparatedNumberSets();
        for (TileSet ts : tilesets) {
            if (ts.adjacentNumbers.size() == 0) {
                NumberSet blankNumberSet = new NumberSet(new ArrayList<Tile>(), this);
                blankNumberSet.tileSets.add(ts);

                numbersets.add(blankNumberSet);
                break;
            }
        }
        List<MinMaxPair> minmaxpairs = new ArrayList<MinMaxPair>();
        int remainingBombs = this.remainingBombs;
        List<NumberSet> nontrivials = new ArrayList<NumberSet>();
        for (NumberSet ns : numbersets) {
            ns.dynamicFillLocalSolutions();
            if (ns.members.size() > 0 && ns.numLocalSolutions.keySet().size() == 0) {
                throw new Exception("Invalid Board " + Integer.toString(ns.members.get(0).getPosn()));
            }
            int min = ns.minimum();
            int max = ns.maximum();
            if (min == max) {
                remainingBombs -= min;
                ns.setLocalProbability();
            }
            else {
                nontrivials.add(ns);
                minmaxpairs.add(new MinMaxPair(min, max));
            }
        }

        //System.out.println("Search done!");
        //System.out.println(minmaxpairs + ", " + remainingBombs);
        List<List<Integer>> allcombinations = Combinatorics.fastSubsetSum(minmaxpairs,remainingBombs);
        int numcombinations = allcombinations.size();
        //System.out.println("Total: " + numcombinations);

        for (List<Integer> assignment : allcombinations) {
            List<NumberSetAssignment> assignments = new ArrayList<NumberSetAssignment>();
            for (int i = 0; i < nontrivials.size(); i++) {
                NumberSetAssignment nsa = new NumberSetAssignment(nontrivials.get(i),assignment.get(i+1));
                assignments.add(nsa);
            }
            GlobalSolution globalsolution = new GlobalSolution(assignments);
            totalSolutions = totalSolutions.add(globalsolution.combinations);
            if (numcombinations % 10000 == 0) {System.out.println("To go: " + numcombinations);
                 }
            numcombinations--;
        }

        if (totalSolutions.equals(BigInteger.ZERO)) {
            throw new Exception("Board is invalid");
        }

        //System.out.println(totalSolutions);
        for (NumberSet nontrivial : nontrivials) {
            for (TileSet ts : nontrivial.tileSets) {
                ts.setProbabilities(totalSolutions);
            }
        }
        this.remainingBombs = totalbombs;
    }

    public boolean isDone() {
        boolean result = true;
        for (Tile t : tiles.values()) {
            if (t.isCovered() && !t.isBomb()) {
                result = false;
            }
        }
        return result;

    }

    public boolean isValid() {
        softResetNumbers();

        List<TileSet> tilesets = this.tileSets();
        List<NumberSet> numbersets = numberSets();
        for (TileSet ts : tilesets) {
            if (ts.adjacentNumbers.size() == 0) {
                NumberSet blankNumberSet = new NumberSet(new ArrayList<Tile>(), this);
                blankNumberSet.tileSets.add(ts);
                numbersets.add(blankNumberSet);
                break;
            }
        }
        List<MinMaxPair> minmaxpairs = new ArrayList<MinMaxPair>();
        int remainingBombs = this.remainingBombs;
        List<NumberSet> nontrivials = new ArrayList<NumberSet>();
        for (NumberSet ns : numbersets) {
            try {
                ns.dynamicFillLocalSolutions();
            } catch (Exception e) {
                this.remainingBombs = totalbombs;
                return false;
            }
            if (ns.members.size() > 0 && ns.numLocalSolutions.keySet().size() == 0) {
                this.remainingBombs = totalbombs;
                return false;
            }
            int min = ns.minimum();
            int max = ns.maximum();
            if (min == max) {
                remainingBombs -= min;
            }
            else {
                nontrivials.add(ns);
                minmaxpairs.add(new MinMaxPair(min, max));
            }
        }

        int numCombinations = Combinatorics.numSubsetSum(minmaxpairs,remainingBombs);
        if (numCombinations == 0) {
            this.remainingBombs = totalbombs;
            return false;
        }
        else {
            this.remainingBombs = totalbombs;
            return true;
        }
    }

    public boolean isDetermined(int pos) {
        int totalValid = 0;
        boolean wasBomb = tiles.get(pos).isBomb();
        int initialValue = 0;
        if (!wasBomb && tiles.get(pos).isAssigned()) {
            initialValue = tiles.get(pos).getValue();
        }
        for (int i = 0; i <= radius(pos, tile -> tile.isCovered()).size(); i++) {
            assignTile(pos,i);
            if (isValid()) {
                totalValid++;
                if (totalValid == 2) {
                    coverTile(pos);
                    if (wasBomb) {
                        assignBomb(pos);
                    }
                    else {
                        assignValue(pos, initialValue);
                    }
                    return false;
                }
            }
        }
        coverTile(pos);
        if (wasBomb && tiles.get(pos).isAssigned()) {
            assignBomb(pos);
        }
        else {
            assignValue(pos, initialValue);
        }
        return true;
    }

    public void makeBestMove() throws Exception {
        this.findProbabilities();
        int trueTotalSolutions = totalSolutions.intValue();
        System.out.println(toString());
        //TODO: Make totalSolutions include the trivial numbersets, because
        //TODO: Make the tile's probability reset.
        // they cannot be treated separately in this algorithm.
        //First: get the list of non-determined tiles or find a safe tile
        Boolean hasNonDetermined = false;
        List<Tile> nonDetermined = new ArrayList<>();
        Map<Tile,Double> probabilities = new HashMap<>();
        //Collect all the non-determined tiles while checking for safe tiles
        for (int pos = 0; pos < width * height; pos++) {
            Tile tile = tiles.get(pos);
            if (tile.isCleared() || tile.probability == 1.0) {
                continue;
            }
            if (tile.isSafe) {
                revealLowest(); //Clears only the safe tiles
                return;
            }
            if (!isDetermined(pos)) {
                hasNonDetermined = true;
                nonDetermined.add(tiles.get(pos));
                probabilities.put(tile,tile.probability);
            }
        }
        if (!hasNonDetermined){
            revealLowest(); //It doesn't matter where you go, might as well reveal the lowest probability tile
            return;
        }
        //Sort nonDetermined by probability
        Comparator<Tile> byProbability = Comparator.comparingDouble(tile -> tile.probability);
        Collections.sort(nonDetermined, byProbability);
        //Find the best non-determined tile
        int lowestLosses = totalSolutions.intValue();
        Tile bestTile = nonDetermined.get(0);
        for (Tile tile : nonDetermined) {
            int totalExpectedLosses = (int)Math.round(trueTotalSolutions * probabilities.get(tile));
            int pos = tile.getPosn();
            int trueValue = tile.getValue();
            for (int num = 0; num <= radius(pos, t -> t.isCovered()).size(); num++) {
                if (totalExpectedLosses >= lowestLosses) {
                    break;
                }
                tile.assignValue(num);
                tile.clear();
                if (isValid()) {
                    totalExpectedLosses += this.expectedLosses(lowestLosses, totalExpectedLosses);
                }
                tile.cover();
            }
            tile.assignValue(trueValue);
            if (totalExpectedLosses < lowestLosses) {
                lowestLosses = totalExpectedLosses;
                bestTile = tile;
                if (lowestLosses == 1) {
                    System.out.println(String.format("%d: %f", bestTile.getPosn(),  1 - lowestLosses * 1.0 / trueTotalSolutions));
                    reveal(bestTile.getPosn());
                    return;
                }
            }
        }
        System.out.println(String.format("%d: %f", bestTile.getPosn(),  1 - lowestLosses * 1.0 / trueTotalSolutions));
        reveal(bestTile.getPosn());

    }

    //maxLosses is the current number of losses that you need to beat. If you go over, then quit this branch,
    //totalLosses is the total accumulated losses in this branch.
    public int expectedLosses(int maxLosses, int totalLosses) {
        try {
            this.findProbabilities();
        } catch (Exception e) {
            return 0;
        }
        int trueTotalSolutions = totalSolutions.intValue();
        // they cannot be treated separately in this algorithm.
        //First: get the list of non-determined tiles or find a safe tile
        Boolean hasNonDetermined = false;
        Boolean hasSafe = false;
        Tile safeTile = tiles.get(0);
        Map<Tile,Double> probabilities = new HashMap<>();
        List<Tile> nonDetermined = new ArrayList<>();
        //Collect all the non-determined tiles while checking for safe tiles
        for (int pos = 0; pos < width * height; pos++) {
            Tile tile = tiles.get(pos);
            if (tile.isCleared() || tile.probability == 1.0) {
                continue;
            }
            if (tile.isSafe) {
                hasSafe = true;
                safeTile = tile;
                probabilities.put(tile,tile.probability);
            }
            if (!isDetermined(pos)) {
                hasNonDetermined = true;
                nonDetermined.add(tiles.get(pos));
                probabilities.put(tile,tile.probability);
            }
        }
        if (!hasNonDetermined){
            return trueTotalSolutions - 1;
        }
        //Sort nonDetermined by probability
        Comparator<Tile> byProbability = Comparator.comparingDouble(tile -> tile.probability);
        Collections.sort(nonDetermined, byProbability);
        //Find the best non-determined tile
        //The number of losses that can not be exceeded is the total number of losses minus all the losses accumulated
        //before and during this branch
        int lowestLosses = maxLosses - totalLosses;
        if (hasSafe) {
            Tile tile = safeTile;
            int totalExpectedLosses = (int)Math.round(trueTotalSolutions * probabilities.get(tile)); //Will be 0
            int pos = tile.getPosn();
            int trueValue = tile.getValue();
            for (int num = 0; num <= radius(pos, t -> t.isCovered()).size(); num++) {
                if (totalExpectedLosses >= lowestLosses) {
                    break;
                }
                tile.assignValue(num);
                tile.clear();
                if (isValid()) {
                    totalExpectedLosses += this.expectedLosses(lowestLosses, totalExpectedLosses);
                }
                tile.cover();
            }
            tile.assignValue(trueValue);
            return totalExpectedLosses;
        }

        for (Tile tile : nonDetermined) {
            //System.out.println(maxLosses);
            int totalExpectedLosses = (int)Math.round(trueTotalSolutions * probabilities.get(tile));
            int pos = tile.getPosn();
            int trueValue = tile.getValue();
            for (int num = 0; num <= radius(pos, t -> t.isCovered()).size(); num++) {
                if (totalExpectedLosses >= lowestLosses) {
                    break;
                }
                tile.assignValue(num);
                tile.clear();
                if (isValid()) {
                    totalExpectedLosses += this.expectedLosses(lowestLosses, totalExpectedLosses);
                }
                tile.cover();
            }
            tile.assignValue(trueValue);
            if (totalExpectedLosses < lowestLosses) {
                lowestLosses = totalExpectedLosses;
                if (lowestLosses == 1) {
                    return 1;
                }
            }
        }
        return lowestLosses;
    }
}