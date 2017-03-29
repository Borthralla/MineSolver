package Minesweeper;

/**
 * Created by Philip on 3/26/2017.
 */
public class MinMaxPair {
    int min;
    int max;
    public MinMaxPair(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public String toString() {
        return "[" + min + "," + max + "]";
    }
}
