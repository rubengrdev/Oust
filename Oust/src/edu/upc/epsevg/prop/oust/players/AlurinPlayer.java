package edu.upc.epsevg.prop.oust.players;

import edu.upc.epsevg.prop.oust.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class AlurinPlayer implements IPlayer, IAuto {

    private String name;
    private int profundidadMaxima;

    public AlurinPlayer(String name, int prof) {
        this.name = name;
        this.profundidadMaxima = prof;
    }

    @Override
    public void timeout() {
        // No hacemos nada
    }

   

   // ========================= MINIMAX + ALFA-BETA =========================

    public int minmax(GameStatus gs, int depth, boolean max, PlayerType me,
                      int alpha, int beta) {

        Heuristica h = new Heuristica(this.name, this.profundidadMaxima);
        
        if (depth == 0 || gs.isGameOver()) {
            return h.heuristica(gs, me);
        }

        List<Point> moves = gs.getMoves();

        if (moves.isEmpty()) {
            return h.heuristica(gs, me);
        }

        if (max) {
            int best = Integer.MIN_VALUE;

            for (Point m : moves) {
                GameStatusAlurin next = new GameStatusAlurin(gs);

            try {
                next.placeStone(m);
            } catch (Exception e) {
                continue;
            }

            if (next.isGameOver() && next.GetWinner() != me) {
                continue;
            }

                boolean nextMax = (me == next.getCurrentPlayer());
                int value = minmax(next, depth - 1, nextMax, me, alpha, beta);

                best = Math.max(best, value);
                alpha = Math.max(alpha, best);

                if (beta <= alpha) break;
            }
            return best;

        } else {
            int best = Integer.MAX_VALUE;

            for (Point m : moves) {
                GameStatusAlurin next = new GameStatusAlurin(gs);

        try {
            next.placeStone(m);
        } catch (Exception e) {
            continue;
        }

        if (next.isGameOver() && next.GetWinner() != me) {
            continue;
        }

                boolean nextMax = (me == next.getCurrentPlayer());
                int value = minmax(next, depth - 1, nextMax, me, alpha, beta);

                best = Math.min(best, value);
                beta = Math.min(beta, best);

                if (beta <= alpha) break;
            }
            return best;
        }
    }
    
    // ========================= MOVE =========================

@Override
public PlayerMove move(GameStatus gs) {

    PlayerType me = gs.getCurrentPlayer();
    GameStatusAlurin aux = new GameStatusAlurin(gs);
    List<Point> path = new ArrayList<>();

    // 🔴 JUGADA COMPLETA
    do {
        List<Point> moves = aux.getMoves();

        if (moves.isEmpty()) break;

        Point best = null;
        int bestValue = Integer.MIN_VALUE;

        for (Point m : moves) {
            GameStatusAlurin test = new GameStatusAlurin(aux);

            try {
                test.placeStone(m);
            } catch (Exception e) {
                continue;
            }

            boolean nextMax = (me == test.getCurrentPlayer());
            int value = this.minmax(
                test,
                profundidadMaxima - 1,
                nextMax,
                me,
                Integer.MIN_VALUE,
                Integer.MAX_VALUE
            );

            if (value > bestValue) {
                bestValue = value;
                best = m;
            }
        }

        if (best == null) break;

        aux.placeStone(best);
        path.add(best);

    } while (aux.getCurrentPlayer() == me);

    return new PlayerMove(path, 0, 0, SearchType.MINIMAX);
}


    @Override
    public String getName() {
        return "Alurin(" + name + ")";
    }
}
