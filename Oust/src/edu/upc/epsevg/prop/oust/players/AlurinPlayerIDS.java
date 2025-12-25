package edu.upc.epsevg.prop.oust.players;

import edu.upc.epsevg.prop.oust.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class AlurinPlayerIDS extends AlurinPlayer{

    private String name;
    private int profundidadMaxima;
    private long startTime;
    private final long TIME_LIMIT = 4500; // este 4500 son 4.5segunds en 
   
    public AlurinPlayerIDS(String name, int prof) {
        super(name, prof);
    }

    @Override
    public void timeout() {
        // No hacemos nada
    }


    public Point getMove(GameStatus gs) {
        startTime = System.currentTimeMillis();
        Point bestMove = null;
        int depth = 1;
        
       
        List<Point> moves = gs.getMoves();
        if (moves.isEmpty()){
            return null;
        }

        PlayerType me = gs.getCurrentPlayer();

        // Bucle IDS (fins a que s'acaba el temps
        try {
            while (true) {
                Point currentBest = null;
                int bestValue = Integer.MIN_VALUE;

                for (Point m : moves) {
                    checkTime(); //si s'acaba el temps peta :)

                    GameStatusAlurin next = new GameStatusAlurin(gs);
                    next.placeStone(m);
                    
                    boolean nextMax = (me == next.getCurrentPlayer());
                    int val = minmaxIDS(next, depth - 1, nextMax, me, Integer.MIN_VALUE, Integer.MAX_VALUE);

                    if (val > bestValue) {
                        bestValue = val;
                        currentBest = m;
                    }
                }
                
                bestMove = currentBest;
                depth++;
                
                //aquesta terroristada és per si guanyem, no té sentit continuar...
                if (bestValue == Integer.MAX_VALUE){ 
                    break;
                }
            }
        } catch (TimeoutException e) {
            //to do...
        }
        if(bestMove != null){
            return bestMove;
        }else{
            return moves.get(0);
        }
    }

    public int minmaxIDS(GameStatus gs, int depth, boolean max, PlayerType me, int alpha, int beta) throws TimeoutException {
        checkTime(); 
        Heuristica h = new Heuristica(this.name, this.profundidadMaxima);
        if (depth == 0 || gs.isGameOver()) {
            return h.heuristica(gs, me);
        }

        List<Point> moves = gs.getMoves();
        if (moves.isEmpty()) return h.heuristica(gs, me);

        if (max) {
            int best = Integer.MIN_VALUE;
            for (Point m : moves) {
                GameStatusAlurin next = new GameStatusAlurin(gs);
                
                try { 
                    next.placeStone(m); 
                } catch (Exception e) { 
                    continue; 
                }
                
                boolean nextMax = (me == next.getCurrentPlayer());
                
                int value = minmaxIDS(next, depth - 1, nextMax, me, alpha, beta);
                //alphabeta (poda)
                best = Math.max(best, value);
                alpha = Math.max(alpha, best);
                
                if (beta <= alpha){ 
                    break;
                }
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
                
                boolean nextMax = (me == next.getCurrentPlayer());
                int value = minmaxIDS(next, depth - 1, nextMax, me, alpha, beta);
                best = Math.min(best, value);
                beta = Math.min(beta, best);
                if (beta <= alpha){
                    break;
                }
            }
            return best;
        }
    }

    private void checkTime() throws TimeoutException {
        if (System.currentTimeMillis() - startTime > TIME_LIMIT) {
            throw new TimeoutException();
        }
    }

    private class TimeoutException extends Exception {}
}
