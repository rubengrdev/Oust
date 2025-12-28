package edu.upc.epsevg.prop.oust.players;

import edu.upc.epsevg.prop.oust.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class AlurinPlayerIDS extends AlurinPlayer {

    private volatile boolean timeoutFlag;

    public AlurinPlayerIDS(String name, int profMax) {
        super(name, profMax);
        this.timeoutFlag = false;
    }

    @Override
    public void timeout() {
        timeoutFlag = true;
    }

    @Override
    public PlayerMove move(GameStatus gs) {

        PlayerType me = gs.getCurrentPlayer();
        timeoutFlag = false;

        List<Point> bestPathGlobal = new ArrayList<>();
        int profundidad = 1;

        while (!timeoutFlag && profundidad <= super.profundidadMaxima) {

            GameStatusAlurin aux = new GameStatusAlurin(gs);
            List<Point> currentPath = new ArrayList<>();

            try {
                do {
                    List<Point> moves = aux.getMoves();
                    if (moves.isEmpty()) break;

                    Point bestMove = null;
                    int bestValue = Integer.MIN_VALUE;

                    for (Point m : moves) {

                        if (timeoutFlag) break;

                        GameStatusAlurin test = new GameStatusAlurin(aux);

                        try {
                            test.placeStone(m);
                        } catch (Exception e) {
                            continue;
                        }

                        boolean nextMax = (me == test.getCurrentPlayer());
                        int value = super.minmax(
                            test,
                            profundidad - 1,
                            nextMax,
                            me,
                            Integer.MIN_VALUE,
                            Integer.MAX_VALUE
                        );

                        if (value > bestValue) {
                            bestValue = value;
                            bestMove = m;
                        }
                    }

                    if (bestMove == null || timeoutFlag) break;

                    aux.placeStone(bestMove);
                    currentPath.add(bestMove);

                } while (aux.getCurrentPlayer() == me && !timeoutFlag);

                if (!timeoutFlag && !currentPath.isEmpty()) {
                    bestPathGlobal = new ArrayList<>(currentPath);
                }

            } catch (Exception e) {
                // Si salta cualquier cosa por timeout, simplemente salimos
            }

            profundidad++;
        }

        if (bestPathGlobal.isEmpty()) {
            List<Point> moves = gs.getMoves();
            if (!moves.isEmpty()) {
                bestPathGlobal.add(moves.get(0));
            }
        }

        return new PlayerMove(bestPathGlobal, 0, 0, SearchType.MINIMAX_IDS);
    }
}
