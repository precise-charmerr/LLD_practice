import javax.swing.*;
import java.util.*;

enum Symbol {
    X, O
}

enum GAME_STATE {
    DRAW, RUNNING, WON
}

enum PlayerType {
    HUMAN, BOT
}

record Cell(int row, int col) {}
abstract class Player {
    private final String name;
    private final Symbol symbol;

    public Player(String name, Symbol symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public abstract Cell getNextMove(Board board);
}

class HumanPlayer extends Player {
    private final Scanner scanner;
    public HumanPlayer(String name, Symbol symbol, Scanner scanner) {
        super(name, symbol);
        this.scanner = scanner;
    }

    @Override
    public Cell getNextMove(Board board) {
        int row = scanner.nextInt();
        int col = scanner.nextInt();

        return new Cell(row, col);
    }
}

class BotPlayer extends Player {
    public BotPlayer(String name, Symbol symbol) {
        super(name, symbol);
    }

    @Override
    public Cell getNextMove(Board board) {
        int n = board.getBoardSize();
        for(int i = 0;i<n;i++) {
            for(int j = 0;j<n;j++) {
                if(board.isCellEmpty(i, j)) {
                    return new Cell(i, j);
                }
            }
        }
        return null;
    }
}

class Board {
    private final int n;
    private int positionsFilled;
    private Symbol[][] board;
    private GAME_STATE state;

    public Board(int n) {
        this.n = n;
        board = new Symbol[n][n];
        state = GAME_STATE.RUNNING;
        positionsFilled = 0;
    }

    public GAME_STATE getBoardState() {
        return state;
    }

    public boolean makeMove(int x, int y, Symbol playerSymbol) {
        // validations
        if(x >= n || y >= n || x < 0 || y < 0) {
            System.out.println("Player cant fill positions outside the board size!");
            return false;
        }
        if(board[x][y] != null) {
            System.out.println("Position already filled, try some other position pls!");
            return false;
        }
        board[x][y] = playerSymbol;
        positionsFilled++;

        if (checkWinningCondition(x, y, playerSymbol)) {
            state = GAME_STATE.WON;
        } else if (positionsFilled == n * n) {
            state = GAME_STATE.DRAW;
        }
        return true;
    }

    public boolean checkWinningCondition(int x, int y, Symbol playerSymbol) {
        boolean rowCount = true, colCount = true,
                leftDiagonalCount = true, rightDiagonalCount = true;

        for(int i = 0;i<n;i++) {
            if(board[x][i] != playerSymbol) rowCount = false;
            if(board[i][y] != playerSymbol) colCount = false;
            if(board[i][i] != playerSymbol) leftDiagonalCount = false;
            if(board[i][n-1-i] != playerSymbol) rightDiagonalCount = false;
        }
        return rowCount || colCount || (x==y && leftDiagonalCount) ||
                (rightDiagonalCount && (x+y==n-1));
    }

    public int getBoardSize() {
        return n;
    }

    public boolean isCellEmpty(int x, int y) {
        return board[x][y] == null;
    }
}

class Game {
    private Board board;
    private List<Player> users;

    private Game(Builder builder) {
        this.users = builder.players;
        board = new Board(builder.boardSize);
    }

    public void gameStart() {
        int playerPosition = 0;
        while(board.getBoardState() == GAME_STATE.RUNNING) {

            Player currentPlayer = users.get(playerPosition);
            System.out.println("User " + currentPlayer.getName() + " pls try next move!");

            boolean valid = false;
            while(!valid) {
                Cell move = currentPlayer.getNextMove(board);
                valid = board.makeMove(move.row(), move.col(), currentPlayer.getSymbol());
            }

            if(board.getBoardState() == GAME_STATE.DRAW) {
                System.out.println("Draw happened, nobody won");
            } else if(board.getBoardState() == GAME_STATE.WON) {
                System.out.println("Player " +
                        users.get(playerPosition % users.size()).getName() +
                        " won the game"
                );
            }

            playerPosition = (playerPosition + 1) % users.size();
        }
    }

    public static class Builder {
        private int boardSize = 3;
        private List<Player> players = new ArrayList<>();

        public Builder setBoardSize(int boardSize) {
            this.boardSize = boardSize;
            return this;
        }

        public Builder addPlayer(Player player) {
            players.add(player);
            return this;
        }

        public Game build() {
            if(boardSize < 3) {
                throw new RuntimeException("Board size cant be any less than 3");
            }
            if(players.size() < 2) {
                throw  new RuntimeException("We need atleast two players to start the game!");
            }
            return new Game(this);
        }
    }
}

class PlayerFactory {
    public static Player createPlayer(String name, Symbol symbol,
                                      Scanner scanner, PlayerType playerType) {
        switch (playerType) {
            case HUMAN:
                return new HumanPlayer(name, symbol, scanner);
            case BOT:
                return new BotPlayer(name, symbol);
            default:
                throw new RuntimeException("Unsupported Player Type!");
        }
    }
}

public class Client {
    public static void main(String[] args) {
        // Initializations
        Scanner scanner = new Scanner(System.in);
        Game game = new Game.Builder()
                .setBoardSize(3)
                .addPlayer(PlayerFactory.createPlayer("Priya", Symbol.X, scanner, PlayerType.HUMAN))
                .addPlayer(PlayerFactory.createPlayer("Amit", Symbol.O, scanner, PlayerType.HUMAN))
                .build();

        //Game starts
        game.gameStart();
    }
}