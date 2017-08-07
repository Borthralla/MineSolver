package Minesweeper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Philip on 3/19/2017.
 */
public class Tile {
    public boolean isFlagged = false;
    public boolean isMarked = false;
    public boolean isSafe = false;
    private int posn;
    private int value;
    private boolean isbomb;
    private boolean iscleared;
    private boolean isassigned;
    boolean searched;
    private List<TileSet> tileSetRadius;
    public double probability;
    int remainingValue;

    /**
     * Creates a covered tile. Please note that value is not assigned yet.
     * A cover is chosen when the tile is uncovered.
     * @param position the position of the bomb, counting from top left horizontally across the board, starting at 1.
     */
    public Tile(int position) {
        this.posn = position;
        this.isbomb = false;
        this.iscleared = false;
        this.isassigned = false;
        this.tileSetRadius = new ArrayList<TileSet>();
        this.searched = false;
        this.probability = 0.0;
    }

    /**
     * Creates a clear tile with a number value referring to the number of bombs in the tile's radius
     * @param position the position of the bomb, counting from top left horizontally across the board, starting at 1.
     * @param value number of bombs adjacent to the tile
     */
    public Tile(int position, int value) {
        this.posn = position;
        this.value = value;
        this.remainingValue = value;
        this.isbomb = false;
        this.iscleared = true;
        this.isassigned = true;
        this.tileSetRadius = new ArrayList<TileSet>();
        this.searched = false;
        this.probability = 0.0;
    }

    public void resetTileSetRadius() {
        this.tileSetRadius = new ArrayList<TileSet>();
    }

    /**
     * gets the position of this tile
     * @return The position of the tile from top to bottem, left to right going horizontally.
     */
    public int getPosn() {
        return this.posn;
    }

    public boolean isCovered() {
        return !(this.isCleared()) ;
    }

    public int getValue() {
        if (!(this.isassigned)) {
            throw new IllegalArgumentException("This tile hasn't been assigned a number yet");
        }
        return this.value;
    }

    public boolean isNumber() {
        return this.isassigned && this.iscleared;
    }

    /**
     * Whether or not this tile is a bomb
     * @return Whether this tile is a bomb
     */
    public boolean isBomb() {
        return this.isbomb;
    }

    /**
     * Whether or not this tile has been cleared yet
     * @return Whether this tile has been cleared yet
     */
    public boolean isCleared() {
        return this.iscleared;
    }

    /**
     * Returns whether this tile has been assigned a value
     * @return Whether this tile has been assigned a number value
     */
    public boolean isAssigned() {
        return this.isassigned;
    }

    /**
     * Clears the tile, but does not assign a value. Useful for calculating probability.
     */
    public void clear() {
        this.iscleared = true;
    }

    /**
     * Clears the tile and gives it a value referring to the number of bombs adjacent to it
     * @param value Number of bombs adjacent to this tile
     */
    public void assignNumber(int value) {
        this.value = value;
        this.remainingValue = value;
        this.isassigned = true;
        this.isbomb = false;
        this.clear();
    }

    public void assignValue(int value) {
        this.value = value;
        this.remainingValue = value;
        this.isassigned = true;
    }

    public void markBomb() {
        this.isMarked = true;
        this.probability = 1.0;
    }

    public void unmarkBomb() {
        this.isMarked = false;
        this.probability  = 0.0;
    }

    public void makeBomb() {
        this.isbomb = true;
    }

    public void cover() {
        this.iscleared = false;
    }

    public void addTileSet(TileSet tileset) {
        this.tileSetRadius.add(tileset);
    }

    /**
     *
     * @param that
     * @return
     */
    public boolean equals(Object that) {
        if (that instanceof Tile) {
            return this.getPosn() == ((Tile) that).getPosn();
        }
        else {
            return false;
        }
    }

    public List<TileSet> tileSetRadius() {
        return this.tileSetRadius;
    }

    public int remainingBombs() {
        int result = remainingValue;
       for (TileSet tileset : tileSetRadius()) {
           if (tileset.isAssigned)
           result -= tileset.numBombs();
       }
       return result;
    }

    public int unassignedTileSetCount() {
        int result = 0;
        for (TileSet ts : tileSetRadius()) {
            if (!ts.isAssigned) {
                result += 1;
            }
        }
        return result;
    }

    public List<List<Assignment>> allPossibilities() {
        List<List<Assignment>> result = new ArrayList<List<Assignment>>();
        List<MinMaxPair> minmaxpairs = new ArrayList<MinMaxPair>();
        List<TileSet> unassigned = new ArrayList<TileSet>();
        int val = remainingValue;
        for (TileSet ts : this.tileSetRadius()) {
            if (!ts.isAssigned) {
                unassigned.add(ts);
                minmaxpairs.add(new MinMaxPair(ts.minimum(), ts.maximum()));
            }
            else {
                val = val - ts.numBombs();
            }
        }
        for (List<Integer> solution : Combinatorics.subsetSum(minmaxpairs,val)) {

            List<Assignment> possibility = new ArrayList<Assignment>();
            for (int i = 0; i < solution.size(); i++) {
                possibility.add(new Assignment(unassigned.get(i),solution.get(i)));
            }
            result.add(possibility);
        }
        return result;
    }

    public int hash() {
        return this.getPosn();
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public String toString() {
        return Integer.toString(this.posn);
    }
}
