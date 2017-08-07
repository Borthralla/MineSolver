package Minesweeper;

/**
 * Created by Philip on 4/2/2017.
 */
public class BoardTemplate {
    Board board;
    Mode mode;
    boolean firstclick;
    int currentCustomNumber;
    boolean showProbabilities;

    public BoardTemplate() {
        this.board = new Board(30,16,99);
        this.mode = Mode.PLAY;
        this.firstclick = true;
        this.currentCustomNumber = 0;
        this.showProbabilities = true;
    }

    public BoardTemplate(int width, int height, int numBombs) {
        this.board = new Board(width,height,numBombs);
        this.mode = Mode.PLAY;
        this.firstclick = true;
        this.currentCustomNumber = 0;
        this.showProbabilities = true;
    }

    public void resetBoard(int width, int height, int totalbombs) {
        this.board = new Board(width, height, totalbombs);
        firstclick = true;
    }

    public void toggleShowProbabilities() {
        showProbabilities = !showProbabilities;
    }

    public void resetBoard() {
        this.board = new Board(board.width, board.height, board.totalbombs);
        firstclick = true;
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
        }
        if (this.mode == Mode.PLAY) {
            if (board.tiles.get(posn).isNumber()) {
                chordTile(posn);
            }
            else {
                board.reveal(posn);
            }
            try {
                board.findBombSeparatedProbabilities();
            } catch (Exception e) {
               board.reset();
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
        if (!firstclick && mode == Mode.PLAY) {
            if (board.revealLowest()) {

                    try {
                        board.findBombSeparatedProbabilities();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

            }
        }
        else { if(this.mode == Mode.PLAY) {

            this.onClick(board.width * (board.height / 2) + board.width / 2);
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


}

enum Mode {
    CUSTOM, PLAY, NONE
}


