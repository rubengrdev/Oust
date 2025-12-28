package edu.upc.epsevg.prop.oust.players;

import edu.upc.epsevg.prop.oust.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Jugador automático para el juego Oust basado en el algoritmo
 * Minimax con poda alfa-beta y una función heurística.
 * <p>
 * Este jugador es capaz de realizar jugadas completas, incluyendo
 * capturas consecutivas en el mismo turno.
 * </p>
 *
 * Implementa las interfaces {@link IPlayer} y {@link IAuto}.
 *
 * @author Rubén Gómez y Pau Espuń Ferrer
 */
public class PlayerMiniMax implements IPlayer, IAuto {

    /** Nombre identificativo del jugador */
    private String name;

    /** Profundidad máxima de exploración del algoritmo Minimax */
    protected int profundidadMaxima;

    /**
     * Constructor del jugador PlayerMiniMax.
     *
     * @param name Nombre identificativo del jugador
     * @param prof Profundidad máxima que explorará el algoritmo Minimax
     */
    public PlayerMiniMax(String name, int prof) {
        this.name = name;
        this.profundidadMaxima = prof;
    }

    /**
     * Método invocado cuando se supera el tiempo máximo permitido
     * para realizar un movimiento.
     * <p>
     * En esta versión base no se realiza ninguna acción.
     * </p>
     */
    @Override
    public void timeout() {
        // No hacemos nada
    }

    /**
     * Implementación del algoritmo Minimax con poda alfa-beta.
     * <p>
     * Evalúa recursivamente el árbol de juego hasta una profundidad
     * máxima o hasta alcanzar un estado final del juego.
     * </p>
     *
     * @param gs Estado actual del juego
     * @param depth Profundidad restante de exploración
     * @param max Indica si el nodo actual es de maximización (true)
     *            o de minimización (false)
     * @param me Jugador para el cual se evalúa la heurística
     * @param alpha Valor alfa para la poda
     * @param beta Valor beta para la poda
     * @return Valor heurístico estimado del estado {@code gs}
     */
    public int minmax(GameStatus gs, int depth, boolean max, PlayerType me,
                      int alpha, int beta) {

        Heuristica h = new Heuristica(this.name, this.profundidadMaxima);

        // Caso base: profundidad máxima o fin de la partida
        if (depth == 0 || gs.isGameOver()) {
            return h.heuristica(gs, me);
        }

        List<Point> moves = gs.getMoves();

        // Si no hay movimientos disponibles, evaluamos el estado
        if (moves.isEmpty()) {
            return h.heuristica(gs, me);
        }

        // Nodo de maximización
        if (max) {
            int best = Integer.MIN_VALUE;

            for (Point m : moves) {
                GameStatusAlurin next = new GameStatusAlurin(gs);

                try {
                    next.placeStone(m);
                } catch (Exception e) {
                    continue;
                }

                // Se ignoran estados finales perdedores
                if (next.isGameOver() && next.GetWinner() != me) {
                    continue;
                }

                boolean nextMax = (me == next.getCurrentPlayer());
                int value = minmax(next, depth - 1, nextMax, me, alpha, beta);

                best = Math.max(best, value);
                alpha = Math.max(alpha, best);

                // Poda alfa-beta
                if (beta <= alpha) break;
            }
            return best;

        } 
        // Nodo de minimización
        else {
            int best = Integer.MAX_VALUE;

            for (Point m : moves) {
                GameStatusAlurin next = new GameStatusAlurin(gs);

                try {
                    next.placeStone(m);
                } catch (Exception e) {
                    continue;
                }

                // Se ignoran estados finales perdedores
                if (next.isGameOver() && next.GetWinner() != me) {
                    continue;
                }

                boolean nextMax = (me == next.getCurrentPlayer());
                int value = minmax(next, depth - 1, nextMax, me, alpha, beta);

                best = Math.min(best, value);
                beta = Math.min(beta, best);

                // Poda alfa-beta
                if (beta <= alpha) break;
            }
            return best;
        }
    }

    /**
     * Calcula y devuelve la mejor jugada para el estado actual del juego.
     * <p>
     * Este método construye una jugada completa, teniendo en cuenta
     * posibles capturas consecutivas en el mismo turno.
     * </p>
     *
     * @param gs Estado actual del juego
     * @return Objeto {@link PlayerMove} con la secuencia óptima de movimientos
     */
    @Override
    public PlayerMove move(GameStatus gs) {

        PlayerType me = gs.getCurrentPlayer();
        GameStatusAlurin aux = new GameStatusAlurin(gs);
        List<Point> path = new ArrayList<>();

        // Mientras sigamos teniendo el turno (capturas consecutivas)
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

    /**
     * Devuelve el nombre del jugador.
     *
     * @return Nombre identificativo del jugador
     */
    @Override
    public String getName() {
        return "Alurin(" + name + ")";
    }
}
