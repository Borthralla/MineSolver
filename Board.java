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
    BigInteger totalSolutions;

    public Board(int width, int height, int totalbombs) {
        this.width = width;
        this.height = height;
        this.totalbombs = totalbombs;
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

    public void assignTile(int position, int number) {
        tiles.get(position).assignNumber(number);
    }

    public void assignBomb(int position) {
        tiles.get(position).makeBomb();
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
            if (t.isAssigned()) {
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



    public ArrayList<NumberSet> numberSets() {
        ArrayList<ArrayList<Tile>> result = new ArrayList<ArrayList<Tile>>();
        HashMap<Tile, Boolean> numberspassed = new HashMap<Tile, Boolean>();

        for (Tile t : tiles.values()) {
            if (t.isAssigned()) {
                numberspassed.put(t, false);
            }
        }

        for (Tile n : numberspassed.keySet()) {
            if (!(numberspassed.get(n))) {
                ArrayList<Tile> connectedNumbers = new ArrayList<Tile>();
                Deque<Tile> tileStack = new ArrayDeque<Tile>();
                tileStack.addFirst(n);
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
            for (Tile n : radius(c.getPosn(), tile -> tile.isAssigned() && !numberspassed.get(tile))) {
                numberspassed.replace(n, true);
                result.add(n);
            }
        }
        return result;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Tile t : tiles.values()) {
            if (t.isAssigned()) {
                result.append(" " + Integer.toString(t.getValue()) + " ");
            } else {
                result.append(Double.toString(t.probability) + " ");
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
                HashSet<Tile> adjacentNumbers = new HashSet<Tile>(radius(t.getPosn(), tile -> tile.isAssigned()));
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

    public void findProbabilities() throws Exception {
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
        for (NumberSet ns : numbersets) {
            ns.fillLocalSolutions();
            if (ns.members.size() > 0 && ns.localSolutions.keySet().size() == 0) {
                throw new Exception("Invalid Board");
            }
            minmaxpairs.add(new MinMaxPair(ns.minimum(),ns.maximum()));
        }

        System.out.println("Search done!");
        List<List<Integer>> allcombinations = Combinatorics.subsetSum(minmaxpairs,totalbombs);
        int numcombinations = allcombinations.size();
        for (List<Integer> assignment : allcombinations) {
            List<NumberSetAssignment> assignments = new ArrayList<NumberSetAssignment>();
            for (int i = 0; i < assignment.size(); i++) {
                NumberSetAssignment nsa = new NumberSetAssignment(numbersets.get(i),assignment.get(i));
                assignments.add(nsa);
            }
            GlobalSolution globalsolution = new GlobalSolution(assignments);
            totalSolutions = totalSolutions.add(globalsolution.combinations);
            //System.out.println(numcombinations);
            numcombinations--;
        }

        if (totalSolutions.equals(BigInteger.ZERO)) {
            throw new Exception("Board is invalid");
        }
        for (TileSet ts : tilesets) {
            ts.setProbabilities(totalSolutions);
        }

    }
}