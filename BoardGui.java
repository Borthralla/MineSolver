package Minesweeper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Philip on 4/2/2017.
 */
public class BoardGui extends JFrame {
    BoardTemplate boardTemplate;
    JPanel boardPanel;

    public BoardGui() {
        this.boardTemplate = new BoardTemplate();
        this.boardPanel = new BoardPanel(boardTemplate);
        this.add(boardPanel);
        this.pack();
        boardPanel.setFocusable(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public void view() {
        setVisible(true);
        boardPanel.requestFocus();
    }


}

class BoardPanel extends JPanel implements MouseListener, KeyListener {
    BoardTemplate boardTemplate;
    int tileSize;
    Image zero;
    Image one;
    Image two;
    Image three;
    Image four;
    Image five;
    Image six;
    Image seven;
    Image eight;
    Image covered;
    Image flagged;
    public BoardPanel (BoardTemplate boardtemplate) {
        this.boardTemplate = boardtemplate;
        setPreferredSize(new Dimension(1920,1000));

        this.tileSize = (int)Math.min(getPreferredSize().getHeight() / getBoard().height, getPreferredSize().getWidth()/getBoard().width);
        addMouseListener(this);
        addKeyListener(this);
        try {
            this.zero = ImageIO.read(getClass().getResource("Images/0.png"));
            this.one = ImageIO.read(getClass().getResource("Images/1.png"));
            this.two = ImageIO.read(getClass().getResource("Images/2.png"));
            this.three = ImageIO.read(getClass().getResource("Images/3.png"));
            this.four = ImageIO.read(getClass().getResource("Images/4.png"));
            this.five = ImageIO.read(getClass().getResource("Images/5.png"));
            this.six = ImageIO.read(getClass().getResource("Images/6.png"));
            this.seven = ImageIO.read(getClass().getResource("Images/7.png"));
            this.eight = ImageIO.read(getClass().getResource("Images/8.png"));
            this.covered = ImageIO.read(getClass().getResource("Images/facingDown.png"));
            this.flagged = ImageIO.read(getClass().getResource("Images/flagged.png"));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Board getBoard() {
        return boardTemplate.getBoard();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawTiles(g);
    }

    public void showProbabilities(Graphics g) {
        g.setColor(Color.black);
        g.setFont(g.getFont().deriveFont(10));
        for (Tile t : this.getBoard().tiles.values()) {
            if (!t.isNumber()) {
                double probability = t.probability * 100;
                String probabilityString = (Double.toString(probability) + "00").substring(0, 4);
                g.setColor(Color.black);
                g.drawString(probabilityString, xCoordinate(t) + tileSize / 3, yCoordinate(t) + tileSize / 2);
            }
        }
    }


    public void drawTiles(Graphics g) {
        for (Tile t : getBoard().tiles.values()) {
            drawTile(t,g);
        }
    }

    public void drawTile(Tile t, Graphics g) {
        int x = xCoordinate(t);
        int y = yCoordinate(t);
        if (t.isNumber()) {
            if (t.getValue() == 0) {
                g.drawImage(this.zero, x, y, x + tileSize, y + tileSize, 0, 0, 200, 200, null);
            }
            if (t.getValue() == 1) {
                g.drawImage(this.one, x, y, x + tileSize, y + tileSize, 0, 0, 200, 200, null);
            }
            if (t.getValue() == 2) {
                g.drawImage(this.two, x, y, x + tileSize, y + tileSize, 0, 0, 200, 200, null);
            }
            if (t.getValue() == 3) {
                g.drawImage(this.three, x, y, x + tileSize, y + tileSize, 0, 0, 200, 200, null);
            }
            if (t.getValue() == 4) {
                g.drawImage(this.four, x, y, x + tileSize, y + tileSize, 0, 0, 200, 200, null);
            }
            if (t.getValue() == 5) {
                g.drawImage(this.five, x, y, x + tileSize, y + tileSize, 0, 0, 200, 200, null);
            }
            if (t.getValue() == 6) {
                g.drawImage(this.six, x, y, x + tileSize, y + tileSize, 0, 0, 200, 200, null);
            }
            if (t.getValue() == 7) {
                g.drawImage(this.seven, x, y, x + tileSize, y + tileSize, 0, 0, 200, 200, null);
            }
            if (t.getValue() == 8) {
                g.drawImage(this.eight, x, y, x + tileSize, y + tileSize, 0, 0, 200, 200, null);
            }
        }
        else {
            if (t.isFlagged) {
                g.drawImage(this.flagged, x, y, x + tileSize, y + tileSize, 0, 0, 200, 200, null);
            }
            else {
                g.drawImage(this.covered, x, y, x + tileSize, y + tileSize, 0, 0, 200, 200, null);
            }
            g.setColor(tileColor(t));
            g.fillRect(x,y, tileSize, tileSize);
        }

    }



    public int yCoordinate(Tile t) {
        int index = t.getPosn();
        int row = index / getBoard().width;
        return row * tileSize;
    }

    public int xCoordinate(Tile t) {
        int index = t.getPosn();
        int column = index % getBoard().width;
        return column * tileSize;
    }

    public Color tileColor(Tile t) {
        double probability = t.probability;
        if (probability == 0.0) {
            return new Color(50, 118,255, 127);
        }
        if (probability == 1.0) {
            return new Color(255,0,0, 127);
        }
        else {
            int r = (int)(255 * (1 - probability));
            int g = 255;
            return new Color(r,g,0, 127);
        }
    }

    public int tileIndex(MouseEvent e) {
        int column = e.getX() / tileSize;
        int row = e.getY() / tileSize;
        int posn = row * getBoard().width + column;
        return posn;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            boardTemplate.onClick(tileIndex(e));
            repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            boardTemplate.onRightClick(tileIndex(e));
        }
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }



    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_0) {
            boardTemplate.setCurrentCustomNumber(0);
        }
        if (e.getKeyCode() == KeyEvent.VK_1) {
            boardTemplate.setCurrentCustomNumber(1);
        }
        if (e.getKeyCode() == KeyEvent.VK_2) {
            boardTemplate.setCurrentCustomNumber(2);
        }
        if (e.getKeyCode() == KeyEvent.VK_3) {
            boardTemplate.setCurrentCustomNumber(3);
        }
        if (e.getKeyCode() == KeyEvent.VK_4) {
            boardTemplate.setCurrentCustomNumber(4);
        }
        if (e.getKeyCode() == KeyEvent.VK_5) {
            boardTemplate.setCurrentCustomNumber(5);
        }
        if (e.getKeyCode() == KeyEvent.VK_6) {
            boardTemplate.setCurrentCustomNumber(6);
        }
        if (e.getKeyCode() == KeyEvent.VK_7) {
            boardTemplate.setCurrentCustomNumber(7);
        }
        if (e.getKeyCode() == KeyEvent.VK_8) {
            boardTemplate.setCurrentCustomNumber(8);
        }
        if (e.getKeyCode() == KeyEvent.VK_R) {
            boardTemplate.resetBoard();
            repaint();
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            System.out.println("Started search...");
            boardTemplate.revealLowest();
            repaint();
            System.out.println("Done!");
        }
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            boardTemplate.switchMode();
        }
        if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
            showProbabilities(this.getGraphics());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
            this.repaint();
        }
    }
}
