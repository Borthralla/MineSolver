package Minesweeper;

public class Main {

    public static void main(String[] args)  {

        BoardGui  gui = new BoardGui();
        gui.view();
        //online();
        //fourbyfour();

        //BoardTemplate test = new BoardTemplate(200,200,8000);
        //test.autoSolve(10);

        /*
        Board test = randomBoard(20,20,60,100,2);
        try {
            test.findProbabilities();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(test.toString());
        try {
            test.findBombSeparatedProbabilities();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(test.toString());
        */
        /*
        long start = System.nanoTime();
      Board test = randomAccurateBoard(30,16,100,80);


        System.out.println(test);

        try {
            test.findProbabilities();

        } catch (Exception e) {
            e.printStackTrace();
        }




        System.out.println(test);
        System.out.println(test.totalSolutions);
        long end = System.nanoTime();
        System.out.println((end-start)/1000000);
        */
    }
}
