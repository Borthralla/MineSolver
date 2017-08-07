package Minesweeper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Philip on 3/25/2017.
 */
public class Combinatorics {

    public static double binomial(int n, int k) {
        if (k > n/2) {
            return binomial(n,n-k);
        }
        if (k > n || k < 0 || n < 0) {
            return 0;
        }
        if (k == 0 || n == k) {
            return 1;
        }
        if (k == n - 1 || k == 1) {
            return n;
        }
        else {

            double result =  ((n - k + 1) * binomial(n,k - 1))/k;
            return result;
        }
    }

    public static BigInteger  bigBinomial(int n, int k) {
        if (k > n/2) {
            return bigBinomial(n,n-k);
        }
        if (k > n || k < 0 || n < 0) {
            return BigInteger.ZERO;
        }
        if (k == 0 || n == k) {
            return BigInteger.ONE;
        }
        if (k == n - 1 || k == 1) {
            return BigInteger.valueOf(n);
        }
        else {

            BigInteger result =  bigBinomial(n,k-1).multiply(BigInteger.valueOf(n - k + 1)).divide(BigInteger.valueOf(k));

            return result;
        }
    }

    public static double approximateDivide(BigInteger numerator, BigInteger divisor) {
        return new BigDecimal(numerator).divide(new BigDecimal(divisor), 15, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static List<List<Integer>> subsetSum(List<MinMaxPair> minmaxpairs,int sum) {
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        if (sum < 0) {
            return result;
        }
        if (minmaxpairs.size() == 0) {
            if (sum == 0) {
                result.add(new ArrayList<Integer>());
                return result;
            }
            else {
                return result;
            }
        }
        if (minmaxpairs.size() == 1) {
            if (minmaxpairs.get(0).min <= sum && minmaxpairs.get(0).max >= sum) {
                ArrayList<Integer> solution = new ArrayList<Integer>();
                solution.add(sum);
                result.add(solution);
                return result;
            }
            return result;
        }
        int totalmax = 0;
        int totalmin = 0;
        for (MinMaxPair p : minmaxpairs) {
            totalmax += p.max;
            totalmin += p.min;
        }
        if (totalmax == sum) {
            ArrayList<Integer> solution = new ArrayList<Integer>();
            for(MinMaxPair p : minmaxpairs) {
                solution.add(p.max);
            }
            result.add(solution);
            return result;
        }

        if (totalmin == sum) {
            ArrayList<Integer> solution = new ArrayList<Integer>();
            for(MinMaxPair p : minmaxpairs) {
                solution.add(p.min);
            }
            result.add(solution);
            return result;
        }

        List<MinMaxPair> restpairs = new ArrayList<MinMaxPair>();
        for (int i = 1; i < minmaxpairs.size(); i++) {
            restpairs.add(minmaxpairs.get(i));
        }
        for (int val = minmaxpairs.get(0).min; val <= minmaxpairs.get(0).max; val++) {


            for (List<Integer> solution : subsetSum(restpairs, sum - val)) {
                solution.add(0,val);
                result.add(solution);
            }
        }
        return result;
    }

    public static List<List<Integer>> fastSubsetSum(List<MinMaxPair> pairs,int sum){
        List<List<Integer>> possibilities = new ArrayList<List<Integer>>();
        int index = 0;
        if (sum < 0) {
            return possibilities;
        }
        if (pairs.size() == 0) {
            if (sum == 0) {
                possibilities.add(new ArrayList<Integer>());
                return possibilities;
            }
            else {
                return possibilities;
            }
        }
        int min = 0;
        int max = 0;
        for (MinMaxPair pair : pairs) {
            min += pair.min;
            max += pair.max;
        }
        if (sum < min || sum > max) {
            return possibilities;
        }
        if (sum == min) {
            ArrayList<Integer> possibility = new ArrayList<Integer>();
            possibility.add(sum);
            for (MinMaxPair pair: pairs) {
                possibility.add(pair.min);
            }
            possibilities.add(possibility);
            return possibilities;
        }
        if (sum == max) {
            ArrayList<Integer> possibility = new ArrayList<Integer>();
            possibility.add(sum);
            for (MinMaxPair pair: pairs) {
                possibility.add(pair.max);
            }
            possibilities.add(possibility);
            return possibilities;
        }
        int lowerbound = sum - max + pairs.get(0).max;
        int upperbound = sum - min + pairs.get(0).min;
        for (int val = Math.max(pairs.get(0).min,lowerbound); val <= Math.min(pairs.get(0).max,upperbound); val++){
            ArrayList<Integer> possibility = new ArrayList<Integer>();
            possibility.add(val); //First value will be the running total of the possibility.
            possibility.add(val);
            possibilities.add(possibility);
        }
        index++;
        while (index < pairs.size()) {
            upperbound += pairs.get(index).min;
            lowerbound += pairs.get(index).max;
            for (List<Integer> possibility : (ArrayList<List<Integer>>)((ArrayList<List<Integer>>)possibilities).clone()) {
                int smallest = 0;
                int biggest = 0;
                if (possibility.get(0) + pairs.get(index).min >= lowerbound){
                    smallest = pairs.get(index).min;
                }
                else{
                    smallest = lowerbound - possibility.get(0);
                }
                if (possibility.get(0) + pairs.get(index).max <= upperbound) {
                    biggest = pairs.get(index).max;
                }
                else {
                    biggest = upperbound - possibility.get(0);
                }
                ArrayList<Integer> possibilityCopy = ((ArrayList<Integer>)((ArrayList<Integer>)possibility).clone());
                for (int val = smallest; val <= biggest; val++){
                    if (val==smallest) {
                        possibility.set(0,possibility.get(0) + val);
                        possibility.add(val);
                    }
                    else {
                        ArrayList<Integer> newcopy = (ArrayList<Integer>)possibilityCopy.clone();
                        newcopy.set(0, newcopy.get(0) + val);
                        newcopy.add(val);
                        possibilities.add(newcopy);
                    }
                }
            }
            index++;
        }
        return possibilities;
    }

    public static void shuffle(List<Integer> integers) {
        Random rnd = ThreadLocalRandom.current();
        for (int i = integers.size() - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            int a = integers.get(index);
            integers.set(index, integers.get(i));
            integers.set(i,a);
        }
    }


}
