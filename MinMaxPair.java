package Minesweeper;

import java.util.Objects;

/**
 * Created by Philip on 3/26/2017.
 */
public class MinMaxPair implements Comparable<MinMaxPair> {
    public int min;
    public int max;
    public MinMaxPair(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public String toString() {
        return "[" + min + "," + max + "]";
    }

    public int hash() {
        return Objects.hash(min,max);
    }

    public boolean equals(Object that) {
        if (that instanceof MinMaxPair) {
            MinMaxPair thatPair = ((MinMaxPair)that);
            return thatPair.min == this.min && thatPair.max == this.max;
        }
        else {
            return false;
        }
    }

    @Override
    public int compareTo(MinMaxPair o) {
        int range1 = this.max - this.min;
        int range2 = o.max - o.min;
        if (range1 < range2){
            return -1;
        }
        if (range1 == range2){
            return 0;
        }
        else{
            return 1;
        }
    }
}
