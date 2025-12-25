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

    // ========================= HEURÍSTICA =========================

public int heuristica(GameStatus gsx, PlayerType me) {

    PlayerType opp = (me == PlayerType.PLAYER1)
            ? PlayerType.PLAYER2
            : PlayerType.PLAYER1;

    GameStatusAlurin gs = (gsx instanceof GameStatusAlurin)
            ? (GameStatusAlurin) gsx
            : new GameStatusAlurin(gsx);

    // =========================
    // 1️⃣ Victoria / Derrota
    // =========================
    if (gs.isGameOver()) {
        if (gs.GetWinner() == me)  return Integer.MAX_VALUE;
        if (gs.GetWinner() == opp) return Integer.MIN_VALUE;
        return 0;
    }

    int score = 0;

    // =========================
    // 2️⃣ Diferencia de piezas
    // =========================
    int myPieces = gs.getPieceCount(me);
    int oppPieces = gs.getPieceCount(opp);
    score += (myPieces - oppPieces) * 1000;

    // =========================
    // 3️⃣ Movilidad
    // =========================
    score += gs.getMoves().size() * 20;

    // =========================
    // 4️⃣ Control del centro
    // =========================
    int size = gs.getSize();
    int center = size / 2;
    int centerScore = 0;

    for (Point p : gs.getPieceLocations(me)) {
        centerScore -= Math.abs(p.x - center) + Math.abs(p.y - center);
    }
    for (Point p : gs.getPieceLocations(opp)) {
        centerScore += Math.abs(p.x - center) + Math.abs(p.y - center);
    }

    score += centerScore * 5;

    
    
    int presion = 0;

    //per cada fitxa propia analitzem que hi ha al voltant de les nostres fitxes:
    for (Point m : gs.getPieceLocations(me)) {
        int myLocal = 0;
        int oppLocal = 0;
        
        //myLocal és un contador de fitxes on la distancia es mínima entre les fitxes del propi jugador
        for (Point p : gs.getPieceLocations(me)) {
            
            int d = Math.abs(m.x - p.x) + Math.abs(m.y - p.y);
            
            if (d <= 1) {
                myLocal++;
            }
        }

        for (Point p : gs.getPieceLocations(opp)) {
            int d = Math.abs(m.x - p.x) + Math.abs(m.y - p.y);
            if (d <= 1) {
                oppLocal++;
            }
        }
    
        /**
        * Si hi ha moltes peces enemigues valorem si hem de colocar fitxa o no, si hi ha més enemigues, no hem de colocar fitxa ja que perdem
        * Si hi ha més fitxes nostres coloquem una fitxa.
        */
        
        if (oppLocal > myLocal) {
            presion -= (oppLocal - myLocal) * 400;
        }else {
            presion += (myLocal - oppLocal) * 200;
        }
    }

    score += presion;

    return score;
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
            AlgoritmesJugador mh = new AlgoritmesJugador(this.name, this.profundidadMaxima);
            int value = mh.minmax(
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
