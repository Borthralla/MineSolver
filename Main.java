package Minesweeper;

import javafx.util.converter.BigIntegerStringConverter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Predicate;

public class Main {

    public static Board randomBoard(int width, int height, int totalNumbers, int numbombs, int num) {
        Board result = new Board(width, height, numbombs);
        ArrayList<Integer> shuffle = new ArrayList<Integer>();
        for (int i = 0; i < width * height; i++) {
            shuffle.add(i);
        }
        Collections.shuffle(shuffle);
        for (int i = 0; i < totalNumbers; i++) {
            result.assignTile(shuffle.get(i), num);
        }
        return result;
    }

    public static Board fromReddit() {
        Board test = new Board(12,10,20);
        test.assignTile(19,1);
        test.assignTile(30,2);
        test.assignTile(32,2);
        test.assignTile(33,1);
        test.assignTile(45,2);
        test.assignTile(41,4);
        test.assignTile(39,4);
        test.assignTile(38,3);
        test.assignTile(49,1);
        test.assignTile(55,4);
        test.assignTile(57,2);
        test.assignTile(70,2);
        test.assignTile(65,2);
        test.assignTile(63,3);
        test.assignTile(62,2);
        test.assignTile(76,1);
        test.assignTile(79,2);
        test.assignTile(80,3);
        test.assignTile(90,2);
        test.assignTile(88,3);
        test.assignTile(102,2);
        return test;
    }


    public static void main(String[] args) {
       Board test = randomBoard(20,20,30,60,2);



       System.out.print(test.toString());




        try {
            test.findProbabilities();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        System.out.print(test);
        System.out.print(test.totalSolutions);

    }
}
