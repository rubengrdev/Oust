package edu.upc.epsevg.prop.oust.players;

import edu.upc.epsevg.prop.oust.GameStatus;
import edu.upc.epsevg.prop.oust.GameStatusAlurin;
import edu.upc.epsevg.prop.oust.PlayerType;
import java.awt.Point;
import java.util.List;

/**
 * Clase encargada de la evaluación heurística de los estados del juego Oust.
 * <p>
 * Centraliza la lógica de valoración para evitar la duplicación de código
 * en las clases que implementan algoritmos de búsqueda como Minimax o
 * Minimax con Iterative Deepening.
 * </p>
 *
 * La heurística combina distintos factores estratégicos como:
 * <ul>
 *   <li>Estados finales (victoria o derrota)</li>
 *   <li>Diferencia de piezas en el tablero</li>
 *   <li>Movilidad</li>
 *   <li>Control del centro</li>
 *   <li>Presión local entre piezas propias y rivales</li>
 * </ul>
 *
 * @author Rubén Gómez y Pau Espuń Ferrer
 */
public class Heuristica extends PlayerMiniMax {

    /**
     * Constructor de la clase Heuristica.
     *
     * @param name Nombre identificativo del jugador
     * @param prof Profundidad máxima usada por el jugador
     */
    public Heuristica(String name, int prof) {
        super(name, prof);
    }

    // ========================= HEURÍSTICA =========================

    /**
     * Evalúa heurísticamente un estado del juego desde el punto de vista
     * de un jugador concreto.
     * <p>
     * Devuelve un valor entero que representa la calidad del estado:
     * valores positivos indican situaciones favorables para el jugador,
     * mientras que valores negativos indican estados desfavorables.
     * </p>
     *
     * @param gsx Estado actual del juego
     * @param me Jugador para el que se realiza la evaluación
     * @return Valor entero que representa la puntuación heurística del estado
     */
    public int heuristica(GameStatus gsx, PlayerType me) {

        PlayerType opp = (me == PlayerType.PLAYER1)
                ? PlayerType.PLAYER2
                : PlayerType.PLAYER1;

        // Aseguramos trabajar con GameStatusAlurin
        GameStatusAlurin gs = (gsx instanceof GameStatusAlurin)
                ? (GameStatusAlurin) gsx
                : new GameStatusAlurin(gsx);

        // =========================
        // Victoria / Derrota
        // =========================
        if (gs.isGameOver()) {
            if (gs.GetWinner() == me)  return Integer.MAX_VALUE;
            if (gs.GetWinner() == opp) return Integer.MIN_VALUE;
            return 0;
        }

        int score = 0;

        // =========================
        // Diferencia de piezas
        // =========================
        int myPieces = gs.getPieceCount(me);
        int oppPieces = gs.getPieceCount(opp);
        score += (myPieces - oppPieces) * 1000;

        // =========================
        // Movilidad
        // =========================
        score += gs.getMoves().size() * 20;

        // =========================
        // Control del centro
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
        // Presión local
        // =========================
        int presion = 0;

        /*
         * Para cada pieza propia se analiza el entorno inmediato,
         * comparando el número de piezas propias y enemigas cercanas.
         */
        for (Point m : gs.getPieceLocations(me)) {
            int myLocal = 0;
            int oppLocal = 0;

            // Contamos piezas propias cercanas
            for (Point p : gs.getPieceLocations(me)) {
                int d = Math.abs(m.x - p.x) + Math.abs(m.y - p.y);
                if (d <= 1) {
                    myLocal++;
                }
            }

            // Contamos piezas enemigas cercanas
            for (Point p : gs.getPieceLocations(opp)) {
                int d = Math.abs(m.x - p.x) + Math.abs(m.y - p.y);
                if (d <= 1) {
                    oppLocal++;
                }
            }

            /*
             * Si hay más piezas enemigas que propias alrededor,
             * el estado se penaliza. En caso contrario, se recompensa
             * la concentración de piezas propias.
             */
            if (oppLocal > myLocal) {
                presion -= (oppLocal - myLocal) * 400;
            } else {
                presion += (myLocal - oppLocal) * 200;
            }
        }

        score += presion;

        return score;
    }
}
