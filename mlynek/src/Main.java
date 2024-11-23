import sac.game.GameState;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        Mlynek startState = new Mlynek();

        startState.setPiece(0, 0, (byte) 1);
        startState.setPiece(0, 3, (byte) 1);
        startState.setPiece(1, 4, (byte) 1);
        startState.setPiece(1, 5, (byte) 1);
        startState.setPiece(1, 6, (byte) 1);
        startState.setPiece(2, 3, (byte) 1);
        startState.setPiece(2, 7, (byte) 1);


        startState.setPiece(0, 1, (byte) 2);
        startState.setPiece(0, 5, (byte) 2);
        startState.setPiece(1, 0, (byte) 2);
        startState.setPiece(2, 5, (byte) 2);

        System.out.println("Początkowa plansza:");
        System.out.println(startState);

        for (int depth = 1; depth <= 6; depth++) {
            long statesCount = countStatesAtDepth(startState, depth);
            System.out.println("Głębokość: " + depth + ", liczba stanów: " + statesCount);
        }
    }

    /**
     * Liczy liczbę stanów w drzewie gry do określonej głębokości.
     *
     * @param state  Początkowy stan gry.
     * @param depth  Maksymalna głębokość.
     * @return Liczba stanów.
     */
    public static long countStatesAtDepth(Mlynek state, int depth) {
        if (depth == 0) {
            return 1; // Stan końcowy na bieżącym poziomie
        }

        List<GameState> children = state.generateChildren();
        long totalStates = 0;

        for (GameState child : children) {
            totalStates += countStatesAtDepth((Mlynek) child, depth - 1);
        }

        return totalStates;
    }
}