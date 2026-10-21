import javax.swing.*;
import java.util.*;

enum Symbol {
    X, O
}

enum GAME_STATE {
    DRAW, RUNNING, WON
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

    public abstract Cell getNextMove();
}

class HumanPlayer extends Player {
    private final Scanner scanner;
    public HumanPlayer(String name, Symbol symbol, Scanner scanner) {
        super(name, symbol);
        this.scanner = scanner;
    }

    @Override
    public Cell getNextMove() {
        int row = scanner.nextInt();
        int col = scanner.nextInt();

        return new Cell(row, col);
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
}

class Game {
    private Board board;
    private List<Player> users;

    public Game(int boardSize, List<Player> users) {
        this.users = users;
        board = new Board(boardSize);
    }

    public void gameStart() {
        int playerPosition = 0;
        while(board.getBoardState() == GAME_STATE.RUNNING) {

            Player currentPlayer = users.get(playerPosition);
            System.out.println("User " + currentPlayer.getName() + " pls try next move!");

            boolean valid = false;
            while(!valid) {
                Cell move = currentPlayer.getNextMove();
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
}

public class Client {
    public static void main(String[] args) {
        // Initializations
        List<Player> player = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        player.add(new HumanPlayer("Priya", Symbol.X, scanner));
        player.add(new HumanPlayer("Amit", Symbol.O, scanner));
        Game game = new Game(3, player);

        //Game starts
        game.gameStart();
    }
}