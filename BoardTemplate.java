package Minesweeper;

/**
 * Created by Philip on 4/2/2017.
 */
public class BoardTemplate {
    Board board;
    Mode mode;
    boolean firstclick;
    int currentCustomNumber;

    public BoardTemplate() {
        this.board = new Board(30,16,99);
        this.mode = Mode.PLAY;
        this.firstclick = true;
        this.currentCustomNumber = 0;
    }

    public void resetBoard(int width, int height, int totalbombs) {
        this.board = new Board(width, height, totalbombs);
        firstclick = true;
    }

    public void resetBoard() {
        this.board = new Board(board.width, board.height, board.totalbombs);
        firstclick = true;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setCurrentCustomNumber(int num) {
        if (num < 0 || num > 8) {
            throw new IllegalArgumentException("Number must be between 0 and 8");
        }
        currentCustomNumber = num;
    }

    public void onClick(int posn) {
        if (this.mode == Mode.PLAY && firstclick) {
            board.assignRandomValuesWithZero(posn);
            this.firstclick = false;
        }
        if (this.mode == Mode.PLAY) {
            board.reveal(posn);
            try {
                board.findProbabilities();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (this.mode == Mode.CUSTOM) {
            board.assignTile(posn,currentCustomNumber);
        }
    }

    public void onRightClick(int posn) {
        if (this.mode == Mode.CUSTOM) {
            board.coverTile(posn);
        }
    }

    public Board getBoard() {
        return this.board;
    }

    public void revealLowest() {
        if (!firstclick) {
            if (board.revealLowest()) {
                try {
                    board.findProbabilities();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}

enum Mode {
    CUSTOM, PLAY, NONE
}


