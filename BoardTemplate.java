package Minesweeper;

import java.time.Duration;
import java.time.Instant;

/**
 * Created by Philip on 4/2/2017.
 */
public class BoardTemplate {
    Board board;
    Mode mode;
    boolean firstclick;
    int currentCustomNumber;
    boolean showProbabilities;
    boolean gameStarted = false;
    Instant startTime;
    boolean gameFinished;
    Instant endTime;

    public BoardTemplate() {
        this.board = new Board(30,16,99);
        this.mode = Mode.PLAY;
        this.firstclick = true;
        this.currentCustomNumber = 0;
        this.showProbabilities = true;
        this.gameFinished = false;
        gameStarted = false;
    }

    public BoardTemplate(int width, int height, int numBombs) {
        this.board = new Board(width,height,numBombs);
        this.mode = Mode.PLAY;
        this.firstclick = true;
        this.currentCustomNumber = 0;
        this.showProbabilities = true;
        this.gameFinished = false;
        gameStarted = false;
    }

    public void resetBoard(int width, int height, int totalbombs) {
        this.board = new Board(width, height, totalbombs);
        firstclick = true;
        gameFinished = false;
        gameStarted = false;
    }

    public void toggleShowProbabilities() {
        showProbabilities = !showProbabilities;
        if (showProbabilities) {
            try {
                board.findBombSeparatedProbabilities();
            } catch (Exception e) {
                board.reset();
            }
        }
    }

    public void resetBoard() {
        this.board = new Board(board.width, board.height, board.totalbombs);
        firstclick = true;
        gameFinished = false;
        gameStarted = false;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setCurrentCustomNumber(int num) {
        if (num < 0 || num > 9) {
            throw new IllegalArgumentException("Number must be between 0 and 9");
        }
        currentCustomNumber = num;
    }

    public void onClick(int posn) {
        if (this.mode == Mode.PLAY && firstclick) {
            board.assignRandomValuesWithZero(posn);
            this.firstclick = false;
            this.gameStarted = true;
            this.startTime = Instant.now();
        }
        if (this.mode == Mode.PLAY) {
            if (board.tiles.get(posn).isNumber()) {
                chordTile(posn);
            }
            else {
                board.reveal(posn);
            }
            if (showProbabilities) {
                try {
                    board.findBombSeparatedProbabilities();
                } catch (Exception e) {
                    board.reset();
                }
            }
            if (board.isDone() && !gameFinished) {
                gameFinished = true;
                endTime = Instant.now();
            }
        }
        if (this.mode == Mode.CUSTOM) {
            boolean wasNumber = board.tiles.get(posn).isNumber();
            if (currentCustomNumber != 9) {
                board.unmarkBomb(posn);
                board.assignTile(posn, currentCustomNumber);
            }
            else {
                board.markBomb(posn);
            }
            try {
                if (wasNumber) {
                    board.findProbabilities();
                }
                else {
                    board.findBombSeparatedProbabilities();
                }
            } catch (Exception e) {
                board.reset();
            }
        }
    }

    public void switchMode() {
        if (this.mode == Mode.CUSTOM) {
            this.resetBoard();
            this.mode = Mode.PLAY;
        }
        else {
            this.mode = Mode.CUSTOM;
        }
    }

    public void onRightClick(int posn) {
        if (this.mode == Mode.PLAY) {
            board.flagTile(posn);
        }
        if (this.mode == Mode.CUSTOM) {
            board.coverTile(posn);
            board.unmarkBomb(posn);
            try {
                board.findProbabilities();
            } catch (Exception e) {
                board.reset();
            }
        }
    }

    public void chordTile(int position) {
        board.chordTile(position);
    }

    public Board getBoard() {
        return this.board;
    }

    public void onEnter() {
        if (!firstclick && mode == Mode.PLAY && showProbabilities) {
           revealLowest();
        }
        else {
            if(this.mode == Mode.PLAY) {
                this.onClick(board.width * (board.height / 2) + board.width / 2);
            }
        }
    }

    public void makeBestMove() {
        if (!board.isDone()) {
            try {
                board.makeBestMove();
                board.findProbabilities();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void revealLowest() {
        //Function returns true if the board changed.
        //The board will not change only when the game is finished.
        if (board.revealLowest()) {
            try {
                board.findBombSeparatedProbabilities();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            if (!gameFinished) {
                gameFinished = true;
                endTime = Instant.now();
            }
        }
    }

    public void autoSolve(int numTrials) {
        int totalhit = 0;
        double totalexpected = 0;
        int totalTrials = 0;
        int numWon = 0;
        for (int i = 0; i < numTrials; i++) {
            while(!board.isDone()) {
                onEnter();
            }
            totalTrials++;
            System.out.println("Solved board " + totalTrials + ": " + board.bombsHit + " bombs hit, " + board.expectedBombs + " bombs expected");
            totalhit += board.bombsHit;
            totalexpected += board.expectedBombs;
            if (board.bombsHit == 0) {
               numWon++;
               //break;
            }
            resetBoard();
        }
        double averageHit = ((double)totalhit) / ((double)totalTrials);
        double averageExpected = totalexpected / (double)totalTrials;
        double winrate = (double)numWon/(double)totalTrials;
        System.out.println("Average hit: " + averageHit + " Average expected: " + averageExpected + " Winrate: " + winrate);
    }

    public int gameDuration() {
        if (!gameStarted) {
            return 0;
        }
        if (gameFinished) {
            return (int) Duration.between(startTime, endTime).getSeconds();
        }
        else {
            return (int) Duration.between(startTime, Instant.now()).getSeconds();
        }
    }

    public int numBombsLeft() {
        return board.playerBombsLeft();
    }


}

enum Mode {
    CUSTOM, PLAY, NONE
}


