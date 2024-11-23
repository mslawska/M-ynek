import sac.game.GameState;
import sac.game.GameStateImpl;

import java.util.ArrayList;
import java.util.List;

public class Mlynek extends GameStateImpl {
    private byte[][] board;
    private int w_player = 9; //biale
    private int b_player = 9; //czarne
    private boolean maximizingTurnNow = true;

    public Mlynek() {
        board = new byte[3][8]; //plansza
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = 0; //puste pola
            }
        }
    }

    private char getPieceSymbol(byte piece) {
        switch (piece) {
            case 1:
                return 'W';
            case 2:
                return 'B';
            default:
                return '.'; //puste pole
        }
    }

    //Konstruktor kopiujący
    public Mlynek(Mlynek p) {
        super();
        board = new byte[p.board.length][p.board[0].length];
        for (int i = 0; i < board.length; i++) {
            System.arraycopy(p.board[i], 0, board[i], 0, board[i].length);
        }
        this.w_player = p.w_player;
        this.b_player = p.b_player;
        this.maximizingTurnNow = p.maximizingTurnNow;
    }

    private boolean czyMlynek(int i, int j) {
        if (board[i][j] == '.') {
            return false; //puste pole które nie może zostać częśćia mlynka
        }

        byte currentPiece = board[i][j];
        boolean isMill = false;

        //srawdzanie młynka na tym samym kwadracie
        if (j % 2 == 0) { //pionek w linii
            isMill = (board[i][(j + 1) % 8] == currentPiece && board[i][(j + 2) % 8] == currentPiece) ||
                    (board[i][(j + 7) % 8] == currentPiece && board[i][(j + 6) % 8] == currentPiece);
        } else { //pionek miedzy wierszami
            isMill = (board[i][(j + 1) % 8] == currentPiece && board[i][(j + 7) % 8] == currentPiece) ||
                    (board[(i + 1) % 3][j] == currentPiece && board[(i + 2) % 3][j] == currentPiece);
        }

        return isMill;
    }

    private List<GameState> rozwiążMłynek() {
        List<GameState> children = new ArrayList<>();
        byte currentPlayerPiece = maximizingTurnNow ? (byte) 1 : (byte) 2;
        byte opponentPiece = maximizingTurnNow ? (byte) 2 : (byte) 1;

        //sprawdzenie ktore pionki sa w mlynku
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == currentPlayerPiece && czyMlynek(i, j)) {

                    //czy mozna usunac pionek?
                    for (int x = 0; x < 3; x++) {
                        for (int y = 0; y < 8; y++) {
                            if (board[x][y] == opponentPiece) {
                                Mlynek newState = new Mlynek(this);
                                newState.board[x][y] = 0; //Usuwamy pionek przeciwnika
                                newState.maximizingTurnNow = !maximizingTurnNow;
                                newState.setMoveName("Usunięcie pionka z [" + x + "," + y + "]");
                                children.add(newState);
                            }
                        }
                    }
                }
            }
        }
        return children;
    }

    // Funkcja ustawiająca pionek na planszy
    public void setPiece(int row, int col, byte piece) {
        board[row][col] = piece;
    }

    @Override
    public List<GameState> generateChildren() {
        List<GameState> children = new ArrayList<>();
        byte currentPlayerPiece = maximizingTurnNow ? (byte) 1 : (byte) 2;


        if (w_player > 0 || b_player > 0) {
            if (w_player > 0 && maximizingTurnNow) {

                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 8; j++) {
                        if (board[i][j] == 0) {
                            Mlynek newState = new Mlynek(this);
                            newState.board[i][j] = 1;
                            newState.w_player--;
                            newState.maximizingTurnNow = false;
                            newState.setMoveName( i + "," + j);
                            children.add(newState);

                            if (newState.czyMlynek(i, j)) {
                                List<GameState> millMoves = newState.rozwiążMłynek();
                                children.addAll(millMoves);
                            }
                        }
                    }
                }
            } else if (b_player > 0 && !maximizingTurnNow) {

                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 8; j++) {
                        if (board[i][j] == 0) {
                            Mlynek newState = new Mlynek(this);
                            newState.board[i][j] = 2;
                            newState.b_player--;
                            newState.maximizingTurnNow = true;
                            newState.setMoveName(i + "," + j);
                            children.add(newState);


                            if (newState.czyMlynek(i, j)) {
                                List<GameState> millMoves = newState.rozwiążMłynek();
                                children.addAll(millMoves);
                            }
                        }
                    }
                }
            }
        }
        return children;
    }



        private void addChild(List<GameState> children, int fromSquare, int fromIndex, int toSquare, int toIndex) {
        if (board[toSquare][toIndex] == 0) { //Ruch możliwy tylko na puste pole
            Mlynek newState = new Mlynek(this);
            newState.board[toSquare][toIndex] = board[fromSquare][fromIndex];
            newState.board[fromSquare][fromIndex] = 0;
            newState.maximizingTurnNow = !maximizingTurnNow;
            newState.setMoveName(fromSquare + "," + fromIndex + "] na [" + toSquare + "," + toIndex + "]");
            children.add(newState);
        }
    }


    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(getPieceSymbol(board[0][6])).append("--------------").append(getPieceSymbol(board[0][5])).append("-------------").append(getPieceSymbol(board[0][4])).append("\n");
        sb.append("|              |             |\n");

        sb.append("|    ").append(getPieceSymbol(board[1][6])).append("---------")
                .append(getPieceSymbol(board[1][5])).append("---------")
                .append(getPieceSymbol(board[1][4])).append("   |\n");
        sb.append("|    |         |         |   |\n");

        sb.append("|    |   ");
        sb.append(getPieceSymbol(board[2][6])).append("-----")
                .append(getPieceSymbol(board[2][5])).append("-----")
                .append(getPieceSymbol(board[2][4])).append("   |   |\n");


        sb.append("|    |   |           |   |   |\n");

        sb.append(getPieceSymbol(board[0][7])).append("----")
                .append(getPieceSymbol(board[1][7])).append("---")
                .append(getPieceSymbol(board[2][7])).append("           ")
                .append(getPieceSymbol(board[2][3])).append("---")
                .append(getPieceSymbol(board[1][3])).append("---")
                .append(getPieceSymbol(board[0][3])).append("\n");

        sb.append("|    |   |           |   |   |\n");

        sb.append("|    |   ");
        sb.append(getPieceSymbol(board[2][0])).append("-----")
                .append(getPieceSymbol(board[2][1])).append("-----")
                .append(getPieceSymbol(board[2][2])).append("   |   |\n");

        sb.append("|    |         |         |   |\n");



        sb.append("|    ");

        sb.append(getPieceSymbol(board[1][0])).append("---------")
                .append(getPieceSymbol(board[1][1])).append("---------")
                .append(getPieceSymbol(board[1][2])).append("   |\n");

        sb.append("|              |             |\n");

        sb.append(getPieceSymbol(board[0][0])).append("--------------")
                .append(getPieceSymbol(board[0][1])).append("------------")
                .append(getPieceSymbol(board[0][2]));

        return sb.toString();
    }


}


