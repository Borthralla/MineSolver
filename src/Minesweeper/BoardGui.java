package Minesweeper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Philip on 4/2/2017.
 */
public class BoardGui extends JFrame implements ComponentListener {
    BoardTemplate boardTemplate;
    BoardPanel boardPanel;
    JPanel settingsPanel;

    public BoardGui() {
        this.boardTemplate = new BoardTemplate();
        this.boardPanel = new BoardPanel(boardTemplate);
        this.settingsPanel = new SettingsPanel(boardTemplate, boardPanel);
        this.setLayout(new BorderLayout());
        this.add(settingsPanel, BorderLayout.PAGE_START);
        this.addComponentListener(this);

        this.add(boardPanel);
        this.pack();
        boardPanel.setFocusable(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public void view() {
        setVisible(true);
        boardPanel.requestFocus();
    }


    @Override
    public void componentResized(ComponentEvent e) {
        boardPanel.setSize(new Dimension(this.getWidth() - 20, this.getHeight() - settingsPanel.getHeight() - 45));
        boardPanel.setTileSize();
        boardPanel.repaint();
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

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
    Image bomb;
    ColorGradient gradient;
    boolean shiftDown = false;
    long timeOfLastSearch = 0;
    boolean ctrlDown = false;
    boolean leftMouseDown = false;
    boolean rightMouseDown = false;
    public BoardPanel (BoardTemplate boardtemplate) {
        this.boardTemplate = boardtemplate;
        setPreferredSize(new Dimension(30*33 - 10,16*33));

        this.tileSize = (int)Math.min(getPreferredSize().getHeight() / getBoard().height, getPreferredSize().getWidth()/getBoard().width);
        addMouseListener(this);
        addKeyListener(this);
        this.gradient = new ColorGradient(Color.WHITE, Color.BLACK);
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
            this.bomb = ImageIO.read(getClass().getResource("Images/bomb.png"));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Board getBoard() {
        return boardTemplate.getBoard();
    }

    public void setTileSize() {
        this.tileSize = (int)Math.min(getSize().getHeight() / getBoard().height, getSize().getWidth()/getBoard().width);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawTiles(g);
        if (boardTemplate.showProbabilities) {
            if (!shiftDown) {
                drawNumericProbability(g);
            }
            if (ctrlDown) {
                showProbabilities(g);
            }
        }
    }

    public void showProbabilities(Graphics g) {
        g.setColor(Color.black);
        g.setFont(g.getFont().deriveFont(5));
        for (Tile t : this.getBoard().tiles.values()) {
            if (!t.isNumber()) {
                double probability = t.probability * 100;
                String probabilityString = (Double.toString(probability) + "00").substring(0, 4);
                g.setColor(Color.black);
                g.drawString(probabilityString, xCoordinate(t) + tileSize / 8, yCoordinate(t) + tileSize / 2 + 2);
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
            else if (t.isMarked) {
                g.drawImage(this.zero, x, y, x + tileSize, y + tileSize, 0, 0, 200, 200, null);
                if (boardTemplate.showProbabilities) {
                    g.setColor(tileColor(t));
                    g.fillRect(x, y, tileSize, tileSize);
                }
                g.drawImage(this.bomb, x, y, x + tileSize, y + tileSize, 0, 0, 48, 48, null);
                return;
            }

            else if (leftMouseDown) {
                //This is where we make the cool mouse-down effects.
                Point mouse = getMousePosition();
                int position = tileIndex(mouse.x, mouse.y);
                //If we're currently depressing this tile, and it's covered, then we should always draw a 0-tile.
                //If we're chording (hence rightmousedown as well) then depress any tile adjacent to the one we're
                //chording (again, by drawing the 0-tile).
                if (t.getPosn() == position || (rightMouseDown && getBoard().adjacentPositions(position, t.getPosn()))) {
                    g.drawImage(this.zero, x, y, x + tileSize, y + tileSize, 0, 0, 200, 200, null);
                }
                //If the mouse was down but it's not next to the tile we're depressing, draw it normally.
                else {
                    g.drawImage(this.covered, x, y, x + tileSize, y + tileSize, 0, 0, 200, 200, null);
                }
            }
            //This is the case where it's a vanilla tile and no mouse-thingies are going on.
            else {
                g.drawImage(this.covered, x, y, x + tileSize, y + tileSize, 0, 0, 200, 200, null);
            }
            //Drawing the actual colors.
            if (boardTemplate.showProbabilities) {
                g.setColor(tileColor(t));
                g.fillRect(x, y, tileSize, tileSize);
            }
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
        if (t.isSafe) {
            return new Color(50, 118,255, 127);
        }
        if (probability == 1.0) {
            return new Color(255,0,0, 127);
        }
        else {

            return gradient.getColor(probability);
        }
    }

    public void drawNumericProbability() {
        Point mouse = getMousePosition();
        if (mouse == null || !inBounds(mouse.x, mouse.y)) {
            return;
        }
        int column = (int)(mouse.getX() / tileSize);
        int row = (int)(mouse.getY() / tileSize);
        int posn = row * getBoard().width + column;

        Tile t = getBoard().tiles.get(posn);
        double probability = t.probability;
        Graphics g = this.getGraphics();
        g.setColor(new Color(255,255,0));
        g.fillRect((int)mouse.getX() + 20, (int)mouse.getY() - 10, 135/16 * Double.toString(probability).length(), 30);

        g.setColor(Color.black);
        g.drawString(Double.toString(probability), (int)mouse.getX() + 25, (int)mouse.getY() + 10);

    }

    public void drawNumericProbability(Graphics g) {
        Point mouse = getMousePosition();
        if (mouse == null || !inBounds(mouse.x, mouse.y)) {
            return;
        }
        int posn = tileIndex(mouse.x, mouse.y);

        Tile t = getBoard().tiles.get(posn);
        double probability = t.probability;
        g.setColor(new Color(255,255,0, 127));
        g.fillRect((int)mouse.getX() + 20, (int)mouse.getY() - 10, 135/16 * Double.toString(probability).length(), 30);

        g.setColor(Color.black);
        g.drawString(Double.toString(probability), (int)mouse.getX() + 25, (int)mouse.getY() + 10);

    }

    public int tileIndex(int x, int y) {
        int column = x / tileSize;
        int row = y / tileSize;
        int posn = row * getBoard().width + column;
        return posn;
    }

    public boolean inBounds(int x, int y) {
        int maxWidth = getBoard().width * tileSize;
        int maxHeight = getBoard().height * tileSize;
        return 0 <= x && x < maxWidth && 0 <= y && y < maxHeight;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.requestFocus();
        if (!inBounds(e.getX(), e.getY())) {
            return;
        }


        if (SwingUtilities.isLeftMouseButton(e)) {
            leftMouseDown = true;
            //Tile is only cleared once the mouse is released. No Model change.
        }

        if (SwingUtilities.isRightMouseButton(e)) {
            rightMouseDown = true;
            //If both left and right mouse down, then it's just a graphical effect so no model change
            if (!leftMouseDown) {
                boardTemplate.onRightClick(tileIndex(e.getX(), e.getY()));
            }
        }
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!inBounds(e.getX(), e.getY())) {
            return;
        }
        boolean left = SwingUtilities.isLeftMouseButton(e);
        boolean right = SwingUtilities.isRightMouseButton(e);
        //For whatever reason, if even though we're in the mouseReleased method, an event will register as
        //being left or right even if it's really mousedown and not released.
        //So the first case is where one is down and one is up. Simultaneous release isn't really possible (LOL).
        if (left && rightMouseDown || right && leftMouseDown) {
            //If one is being released and another is down, we're chording this tile.
            boardTemplate.onChord(tileIndex(e.getX(), e.getY()));
            repaint();
            //If we decide not to chord, then we don't want undoing the chord in a bad order make the tile clear.
            //That's why we release the left/right mouse even if its technically still down.
            rightMouseDown = false;
            leftMouseDown = false;
            return;
        }
        if (SwingUtilities.isRightMouseButton(e)) {
            rightMouseDown = false;
            //If just the right mouse is released, nothing happens since flagging is on mouse-down

        }
        if (SwingUtilities.isLeftMouseButton(e)) {
            //It's possible that the mouse is released coming out of a chord. In that case, it will have been
            //Released automatically. We can see that by checking if it's actually down.
            if (!leftMouseDown) {
                return;
            }
            leftMouseDown = false;

            //Since we're not chording, just reveal the tile.

            boardTemplate.onClick(tileIndex(e.getX(), e.getY()));

            repaint();
        }
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
        if (e.getKeyCode() == KeyEvent.VK_9) {
            boardTemplate.setCurrentCustomNumber(9);
        }
        if (e.getKeyCode() == KeyEvent.VK_R) {
            boardTemplate.resetBoard();
            repaint();
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            boardTemplate.toggleStart();
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER && System.nanoTime() - timeOfLastSearch > 12500000) {
            boardTemplate.onEnter();
            repaint();
            timeOfLastSearch = System.nanoTime();
        }
        if (!shiftDown && e.getKeyCode() == KeyEvent.VK_SHIFT ) {
            //shiftDown = true;
            //drawNumericProbability();
        }

        if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
            //ctrlDown = true;
            //showProbabilities(this.getGraphics());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
            this.ctrlDown = !ctrlDown;
            this.repaint();
        }
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            this.shiftDown = !shiftDown;
            this.repaint();
        }
    }
}

class ColorGradient {
    Color start;
    Color end;
    public ColorGradient(Color start, Color end) {
        this.start = start;
        this.end = end;
    }

    public Color getColor(double p) {
        int r = (int)(start.getRed() + (end.getRed()-start.getRed())*p);
        int g = (int)(start.getGreen() + (end.getGreen()-start.getGreen())*p);
        int b = (int)(start.getBlue() + (end.getBlue()-start.getBlue())*p);
        return new Color(r,g,b,127);
    }
}

class SettingsPanel extends JPanel implements ActionListener {
    BoardTemplate template;
    TextField width;
    TextField height;
    TextField totalbombs;
    Button resetButton;
    JCheckBox showProbabilities;
    BoardPanel boardPanel;
    JRadioButton play;
    JRadioButton custom;
    Timer timer=new Timer(7, this);

    public SettingsPanel(BoardTemplate template, BoardPanel boardPanel) {
        this.template = template;
        this.setMinimumSize(new Dimension(1000,100));
        this.width = new TextField("30", 3);
        this.height = new TextField("16",3);
        this.totalbombs = new TextField("99",4);
        this.resetButton = new Button("Reset Board");
        resetButton.setActionCommand("Reset");
        resetButton.addActionListener(this);
        this.showProbabilities = new JCheckBox("Show Probabilities", true);
        showProbabilities.setActionCommand("Toggle Show Probabilities");
        showProbabilities.addActionListener(this);
        this.add(new JLabel("Width"));
        this.add(width);
        this.add(new JLabel("Height"));
        this.add(height);
        this.add(new JLabel("Total Bombs"));
        this.add(totalbombs);
        this.add(resetButton);
        this.add(showProbabilities);
        this.boardPanel = boardPanel;
        this.play = new JRadioButton("Play", true);
        play.addActionListener(this);
        play.setActionCommand("toggle mode");
        this.custom = new JRadioButton("Custom");
        custom.addActionListener(this);
        custom.setActionCommand("toggle mode");
        ButtonGroup mode = new ButtonGroup();
        mode.add(play);
        mode.add(custom);
        this.add(new JLabel("Mode:"));
        this.add(play);
        this.add(custom);
        timer.start();

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        try {
            if (e.getActionCommand() == null) {
                if(e.getSource()==this.timer){
                    this.repaint();
                    boardPanel.repaint();
                }
            }
            else {
                if (e.getActionCommand().equals("Reset")) {
                    int width = Integer.parseInt(this.width.getText());
                    int height = Integer.parseInt(this.height.getText());
                    int totalBombs = Integer.parseInt(this.totalbombs.getText());
                    template.resetBoard(width, height, totalBombs);
                    boardPanel.setTileSize();
                    boardPanel.repaint();
                    boardPanel.requestFocus();
                }
                if (e.getActionCommand().equals("Toggle Show Probabilities")) {
                    template.toggleShowProbabilities();
                    boardPanel.requestFocus();
                    boardPanel.repaint();
                }
                if (e.getActionCommand().equals("toggle mode")) {
                    System.out.print("mode switch registered");
                    template.switchMode();
                    boardPanel.requestFocus();
                    boardPanel.repaint();
                }
            }
        }
        catch(Exception exception) {
            System.out.println(exception.getMessage());
            return;
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawDuration(g);
        drawNumBombsLeft(g);
    }

    public void drawDuration(Graphics g) {
        Dimension dimension = this.getSize();
        int width = dimension.width;
        int height = dimension.height;
        int duration = template.gameDuration();
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString(Integer.toString(duration),  50, height - 12);
    }

    public void drawNumBombsLeft(Graphics g) {
        Dimension dimension = this.getSize();
        int width = dimension.width;
        int height = dimension.height;
        int numBombsLeft = template.numBombsLeft();
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Bombs: " + Integer.toString(numBombsLeft),  width - 100, height - 12);
    }


}
