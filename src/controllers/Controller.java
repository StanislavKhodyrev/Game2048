package controllers;
import titles.Tile;
import views.View;
import models.Model;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Controller extends KeyAdapter {
    private Model model;
    private View view;
    private static final int WINNING_TILE = 2048;

    public Controller(Model model) {
        this.model = model;
        this.view = new View(this);
    }

    public Tile[][] getGameTiles() {
        return model.getGameTiles();
    }

    public int getScore() {
        return model.getScore();
    }

    public void resetGame() {
        model.setScore(0);
        model.setMaxTile(0);
        view.setGameLost(false);
        view.setGameWon(false);
        model.resetGameTiles();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            resetGame();

        if (!model.canMove())
            view.setGameLost(true);

        if (model.getMaxTile() == WINNING_TILE) {
            view.setGameWon(true);
        }

        if (view.isGameWon() == false && view.isGameLost() == false) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> model.left();
                case KeyEvent.VK_RIGHT -> model.right();
                case KeyEvent.VK_UP -> model.up();
                case KeyEvent.VK_DOWN -> model.down();
                case KeyEvent.VK_Z -> model.rollback();
                case KeyEvent.VK_R -> model.randomMove();
                case KeyEvent.VK_A -> model.autoMove();
            }
        }
        view.repaint();
    }


    public View getView() {
        return view;
    }
}
