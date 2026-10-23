import java.util.*;


enum CELL_STATE {
    FILLED
}

enum GAME_STATE {
    WON, DRAW, RUNNING
}

abstract class Player {
    private int position;
    private String name;
    private int diceCount;
    private static final int total_valid_dice_count = 3;

    public Player(String name) {
        this.position = 0;
        this.name = name;
        this.diceCount = 0;
    }

    public String getName() {
        return name;
    }

    public int getTotalDiceCount() {
        return diceCount;
    }

    public void updateDiceCount() {
        diceCount++;
    }
    public boolean isJumpAllowed(int currRollValue, int diceSize) {
        if(currRollValue == diceSize) {
            updateDiceCount();
        } else {
            diceCount = 0;
        }
        return currRollValue != diceSize || diceCount != total_valid_dice_count - 1;
    }

    public void doJump(int currRollValue) {
        position += currRollValue;
    }

    public int getPosition() {
        return position;
    }

    public void updatePosition() {
        position = 0;
    }

    public void updateEntityPosition(int x) {
        position = x;
    }
}

class HumanPlayer extends Player {

    public HumanPlayer(String name) {
        super(name);
    }
}

class BotPlayer extends Player {

    public BotPlayer(String name) {
        super(name);
    }
}

abstract class BoardEntity {
    private int startPosition;
    private int endPosition;
    private String name;

    public BoardEntity(int startPosition, int endPosition, String name) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.name = name;
    }

    public int finalPoint(int point) {
        return endPosition;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public int getEndPosition() {
        return endPosition;
    }
}

class Snake extends BoardEntity {

    public Snake(int startPosition, int endPosition, String name) {
        super(startPosition, endPosition, name);
    }
}

class Ladder extends BoardEntity {

    public Ladder(int startPosition, int endPosition, String name) {
        super(startPosition, endPosition, name);
    }
}

class Dice {
    private int size;

    public Dice(int size) {
        this.size = size;
    }

    public int getDiceSize() {
        return size;
    }

    public int roll() {
        return new Random().nextInt(size) + 1;
    }
}

class Board {
    private List<BoardEntity> boardEntityList = new ArrayList<>();
    private int boardSize;
    private CELL_STATE[] board;
    private GAME_STATE gameState;
    private Map<Integer, Player> userPositions = new HashMap<>();

    public Board(List<BoardEntity> boardEntityList, int boardSize) {
        this.boardEntityList = boardEntityList;
        this.boardSize = boardSize;
        board = new CELL_STATE[boardSize];
        this.gameState = GAME_STATE.RUNNING;
    }

    public boolean isPositionEmpty(int x) {
        return board[x] == null;
    }

    public int checkSnakeAndladder(int position) {
        for(int i = 0;i<boardEntityList.size();i++) {
            if(boardEntityList.get(i).getStartPosition() == position) {
                return boardEntityList.get(i).getEndPosition();
            }
        }
        return -1;
    }

    public boolean updateGameState(int playerPosition) {
        if(playerPosition == boardSize - 1) {
            gameState = GAME_STATE.WON;
            return true;
        }
        return false;
    }

    public GAME_STATE getGameState() {
        return gameState;
    }

    public int getBoard_size() {
        return boardSize;
    }

    public boolean isValidMove(int diceRoll, int position) {
        if(diceRoll + position <= boardSize - 1) {
            return true;
        }
        return false;
    }

    public void updateUserPositionMapping(int previousPlayerPosition, Player player) {
        int position = player.getPosition();
        Player currentPlayer = userPositions.get(position);

        if(currentPlayer != null) {
            currentPlayer.updatePosition();
        }
        userPositions.put(position, player);
        userPositions.remove(previousPlayerPosition);
    }
}

class GameEngine {
    private List<Player> players;
    private Dice dice;
    private Board board;

    private GameEngine(Builder Builder) {
        this.players = Builder.players;
        this.dice = new Dice(Builder.diceSize);
        this.board = new Board(Builder.boardEntityList, Builder.boardSize);
    }

    public static class Builder {
        private int boardSize;
        private int diceSize;
        private List<Player> players = new ArrayList<>();
        private List<BoardEntity> boardEntityList = new ArrayList<>();

        public Builder setBoardSize(int boardSize) {
            this.boardSize = boardSize;
            return this;
        }

        public Builder setDiceSize(int diceSize) {
            this.diceSize = diceSize;
            return this;
        }

        public Builder addPlayer(Player player) {
            this.players.add(player);
            return this;
        }

        public Builder addSnake(int start, int end, String name) {
            this.boardEntityList.add(new Snake(start, end, name));
            return this;
        }

        public Builder addLadder(int start, int end, String name) {
            this.boardEntityList.add(new Ladder(start, end, name));
            return this;
        }

        public GameEngine build() {
            return new GameEngine(this);
        }
    }

    public void start() {
        int playerTurn = 0;
        while(board.getGameState() == GAME_STATE.RUNNING) {
            Player currentPlayer = players.get(playerTurn);
            int getNextRoll = dice.roll();
            int previousPlayerPosition = currentPlayer.getPosition();
            while(currentPlayer.isJumpAllowed(getNextRoll, dice.getDiceSize())) {
                if(board.isValidMove(getNextRoll, currentPlayer.getPosition())) {
                    currentPlayer.doJump(getNextRoll);

                    int boardNewPos = board.checkSnakeAndladder(currentPlayer.getPosition());
                    if(boardNewPos != -1) {
                        currentPlayer.updateEntityPosition(boardNewPos);
                    }
                    board.updateUserPositionMapping(previousPlayerPosition, currentPlayer);
                }

                if(board.updateGameState(currentPlayer.getPosition())) {
                    System.out.println("Player - " + currentPlayer.getName() + " has won the game!!");
                    break;
                }
                if(getNextRoll != dice.getDiceSize()) {
                    break;
                }
                getNextRoll = dice.roll();
            }

            playerTurn = (playerTurn + 1) % players.size();
        }
    }
}

public class Client {
    public static void main(String[] args) {
        GameEngine gameEngine = new GameEngine.Builder()
                .setBoardSize(100)
                .setDiceSize(6)
                .addLadder(3, 24, "LADDER-1")
                .addSnake(60, 10, "SNAKE-1")
                .addLadder(40, 79, "LADDER2")
                .addSnake(89, 79, "SNAKE-2")
                .addPlayer(new HumanPlayer("Priya"))
                .addPlayer(new HumanPlayer("Amit")).build();
        gameEngine.start();
    }
}