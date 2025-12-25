/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.oust.players;

import edu.upc.epsevg.prop.oust.GameStatus;
import edu.upc.epsevg.prop.oust.GameStatusAlurin;
import edu.upc.epsevg.prop.oust.PlayerType;
import java.awt.Point;
import java.util.List;


public class AlgoritmesJugador extends AlurinPlayer{
      
    public AlgoritmesJugador(String name, int prof) {
        super(name, prof);
    }
    
   // ========================= MINIMAX + ALFA-BETA =========================

    public int minmax(GameStatus gs, int depth, boolean max, PlayerType me,
                      int alpha, int beta) {

        if (depth == 0 || gs.isGameOver()) {
            return heuristica(gs, me);
        }

        List<Point> moves = gs.getMoves();

        if (moves.isEmpty()) {
            return heuristica(gs, me);
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

}
