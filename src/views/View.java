package views;

import controllers.Controller;
import titles.Tile;

import javax.swing.*;
import java.awt.*;


public class View extends JPanel {
    private static final Color BG_COLOR = new Color(0xbbada0);
    private static final String FONT_NAME = "Arial";
    private static final int TILE_SIZE = 94;
    private static final int TILE_MARGIN = 12;

    private Controller controller;

    boolean isGameWon = false;
    boolean isGameLost = false;

    public View(Controller controller) {
        setFocusable(true);
        this.controller = controller;
        addKeyListener(controller);
    }

    public boolean isGameWon() {
        return isGameWon;
    }

    public boolean isGameLost() {
        return isGameLost;
    }

    public void setGameWon(boolean gameWon) {
        isGameWon = gameWon;
    }

    public void setGameLost(boolean gameLost) {
        isGameLost = gameLost;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(BG_COLOR);
        isGameOver();
        g.fillRect(0, 0, this.getSize().width, this.getSize().height);
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                drawTile(g, controller.getGameTiles()[y][x], x, y);
            }
        }
        g.drawString("Score: " + controller.getScore(), 130, 460);

    }

    private void drawTile(Graphics g2, Tile tile, int x, int y) {
        Graphics2D g = ((Graphics2D) g2);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int value = tile.value;
        int xOffset = offsetCoors(x);
        int yOffset = offsetCoors(y);
        g.setColor(tile.getTileColor());
        g.fillRoundRect(xOffset, yOffset, TILE_SIZE, TILE_SIZE, 8, 8);
        g.setColor(tile.getFontColor());
        final int size = value < 100 ? 36 : value < 1000 ? 32 : 24;
        final Font font = new Font(FONT_NAME, Font.BOLD, size);
        g.setFont(font);

        String s = String.valueOf(value);
        final FontMetrics fm = getFontMetrics(font);

        final int w = fm.stringWidth(s);
        final int h = -(int) fm.getLineMetrics(s, g).getBaselineOffsets()[2];

        if (value != 0)
            g.drawString(s, xOffset + (TILE_SIZE - w) / 2, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2 - 2);
    }

    private static int offsetCoors(int arg) {
        return arg * (TILE_MARGIN + TILE_SIZE) + TILE_MARGIN;
    }

    private boolean isGameOver() {
        if (isGameWon || isGameLost) {
            String text = "";
            if (isGameWon) {
                text = "Congratulations, Champion! You've won!\n";
            }
            if (isGameLost)
                text = "You've lost :(\n";

            gameExit(text);
            return true;
        }
        return false;
    }

    private void gameExit(String text) {
        int result = JOptionPane.showConfirmDialog(this, text +
                        "Do you want to try again?", "Game 2048",
                JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            controller.resetGame();
        }
        if (result == JOptionPane.NO_OPTION)
            System.exit(0);
    }
}
