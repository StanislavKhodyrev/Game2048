package models;

import move.Move;
import move.MoveEfficiency;
import titles.Tile;

import java.util.*;

public class Model {
    private static final int FIELD_WIDTH = 4;
    private Tile[][] gameTiles;
    private int maxTile = 0;
    private int score = 0;
    private Stack<Tile[][]> previousStates = new Stack<>();
    private Stack<Integer> previousScores = new Stack<>();
    private boolean isSaveNeeded = true;

    public Model() {
        resetGameTiles();
    }

    public int getMaxTile() {
        return maxTile;
    }

    public void setMaxTile(int maxTile) {
        this.maxTile = maxTile;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    private void addTile() {
        List<Tile> TileList = getEmptyTiles();
        if (TileList.isEmpty()) return;
        int randomTileId = (int) (Math.random() * TileList.size());
        TileList.get(randomTileId).value = Math.random() < 0.9 ? 2 : 4;
    }

    private List<Tile> getEmptyTiles() {
        List<Tile> emptyTiles = new ArrayList<>();
        for (Tile[] tiles : gameTiles) {
            for (Tile tile : tiles) {
                if (tile.isEmpty())
                    emptyTiles.add(tile);
            }
        }
        return emptyTiles;
    }

    public void resetGameTiles() {
        this.gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles[i].length; j++) {
                gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
    }

    private boolean compressTiles(Tile[] tiles) {
        boolean flag = false;
        int emptyPosition = 0;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (!tiles[i].isEmpty()) {
                if (i != emptyPosition) {
                    flag = true;
                    tiles[emptyPosition] = tiles[i];
                    tiles[i] = new Tile();
                }
                emptyPosition++;
            }
        }
        return flag;
    }

    private boolean mergeTiles(Tile[] tiles) {
        boolean flag = false;
        LinkedList<Tile> tilesList = new LinkedList<>();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (tiles[i].isEmpty()) continue;
            if (tiles[i].value != 0 && i == FIELD_WIDTH - 1) {
                tilesList.add(tiles[i]);
                break;
            }
            if (tiles[i].value == tiles[i + 1].value) {
                int sum = tiles[i].value * 2;
                if (sum > maxTile) maxTile = sum;
                tiles[i].value = sum;
                score += sum;
                tilesList.add(tiles[i]);
                i++;
                flag = true;
            } else tilesList.add(tiles[i]);
        }
        while (tilesList.size() < FIELD_WIDTH) {
            tilesList.add(new Tile());
        }

        for (int i = 0; i < tilesList.size(); i++) {
            tiles[i] = tilesList.get(i);
        }
        return flag;
    }

    public void left() {
        if (isSaveNeeded) {
            saveState(gameTiles);
        }
        boolean moveFlag = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                moveFlag = true;
            }
        }
        if (moveFlag) {
            addTile();
        }
        isSaveNeeded = true;
    }


    private void rotateMatrix() {
        final int N = gameTiles.length;
        Tile[][] newMatrix = new Tile[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                newMatrix[i][j] = gameTiles[j][N - 1 - i];
            }
        }
        gameTiles = newMatrix;
    }

    public void right() {
        saveState(gameTiles);
        rotateMatrix();
        rotateMatrix();
        left();
        rotateMatrix();
        rotateMatrix();
    }

    public void up() {
        saveState(gameTiles);
        rotateMatrix();
        left();
        rotateMatrix();
        rotateMatrix();
        rotateMatrix();
    }

    public void down() {
        saveState(gameTiles);
        rotateMatrix();
        rotateMatrix();
        rotateMatrix();
        left();
        rotateMatrix();
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    public boolean canMove() {
        if (getEmptyTiles().size() != 0) return true;
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles[i].length; j++) {
                int currentTitleSize = gameTiles[i][j].value;
                if ((i < FIELD_WIDTH - 1 && currentTitleSize == gameTiles[i + 1][j].value)
                        || ((j < FIELD_WIDTH - 1) && currentTitleSize == gameTiles[i][j + 1].value)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void saveState(Tile[][] tiles) {
        Tile[][] tempTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                tempTiles[i][j] = new Tile(tiles[i][j].value);
            }
        }
        previousStates.push(tempTiles);
        previousScores.push(score);
        isSaveNeeded = false;
    }

    public void rollback() {
        if (!previousStates.isEmpty() && !previousScores.isEmpty()) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
    }

    public void randomMove() {
        int n = ((int) (Math.random() * 100)) % 4;
        switch (n) {
            case 0:
                left();
                break;
            case 1:
                right();
                break;
            case 2:
                up();
                break;
            case 3:
                down();
                break;
        }
    }

    public void autoMove() {
        PriorityQueue<MoveEfficiency> reversePriorityQueue = new PriorityQueue<>(4, Collections.reverseOrder());
        reversePriorityQueue.offer(getMoveEfficiency(this::left));
        reversePriorityQueue.offer(getMoveEfficiency(this::right));
        reversePriorityQueue.offer(getMoveEfficiency(this::up));
        reversePriorityQueue.offer(getMoveEfficiency(this::down));

        reversePriorityQueue.peek().getMove().move();
    }

    public boolean hasBoardChanged() {
        Tile[][] previousTile = previousStates.peek();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].value != previousTile[i][j].value) {
                    return true;
                }
            }
        }
        return false;
    }

    public MoveEfficiency getMoveEfficiency(Move move) {
        move.move();
        MoveEfficiency moveEfficiency = new MoveEfficiency(getEmptyTiles().size(), score, move);
        if (!hasBoardChanged())
            moveEfficiency = new MoveEfficiency(-1, 0, move);
        rollback();
        return moveEfficiency;
    }
}
