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

    // =========================
    // 5️⃣ Proximidad (suave)
    // =========================
    boolean strongerGlobally = myPieces >= oppPieces;
    int proximityScore = 0;

    for (Point m : gs.getPieceLocations(me)) {
        for (Point r : gs.getPieceLocations(opp)) {
            int dist = Math.abs(m.x - r.x) + Math.abs(m.y - r.y);
            if (dist == 1) {
                proximityScore += strongerGlobally ? 15 : -20;
            }
        }
    }

    score += proximityScore;

    // =========================
    // 6️⃣ Anti-suicidio LOCAL (CLAVE)
    // =========================
    int suicidePenalty = 0;

    int[][] dirs = {
        {-1, 0}, {1, 0},
        {0, -1}, {0, 1},
        {-1, 1}, {1, -1}
    };

    for (Point m : gs.getPieceLocations(me)) {

        boolean enemyAdjacent = false;
        int friendlyAdj = 0;

        for (int[] d : dirs) {
            int nx = m.x + d[0];
            int ny = m.y + d[1];

            if (nx < 0 || ny < 0 || nx >= size || ny >= size)
                continue;

            PlayerType c = gs.getColor(nx, ny);

            if (c == opp) enemyAdjacent = true;
            if (c == me)  friendlyAdj++;
        }

        // 🔴 ficha aislada en contacto enemigo → MUERTE CASI SEGURA
        if (enemyAdjacent && friendlyAdj == 0) {
            suicidePenalty -= 500;
        }
    }

    score += suicidePenalty;

    return score;
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
            int value = minmax(
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
