package edu.upc.epsevg.prop.oust.players;

import edu.upc.epsevg.prop.oust.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Jugador automático para Oust que implementa Minimax con poda alfa-beta
 * combinado con Iterative Deepening Search (IDS).
 * <p>
 * Esta clase extiende {@link AlurinPlayer} y reutiliza su algoritmo Minimax,
 * añadiendo una exploración progresiva en profundidad para ajustarse
 * al tiempo máximo disponible por turno.
 * </p>
 *
 * El jugador devuelve siempre la mejor jugada completa encontrada
 * antes de que se produzca el timeout.
 *
 * @author Rubén Gómez y Pau Espuń Ferrer
 */
public class PlayerMiniMaxIDS extends PlayerMiniMax {

    /**
     * Flag que indica si se ha alcanzado el tiempo límite de ejecución.
     * <p>
     * Se declara como {@code volatile} para garantizar la visibilidad
     * entre hilos cuando el método {@link #timeout()} es invocado.
     * </p>
     */
    private volatile boolean timeoutFlag;

    /**
     * Constructor del jugador PlayerMiniMaxIDS.
     *
     * @param name Nombre identificativo del jugador
     * @param profMax Profundidad máxima que se permitirá alcanzar
     *                durante la búsqueda iterativa
     */
    public PlayerMiniMaxIDS(String name, int profMax) {
        super(name, profMax);
        this.timeoutFlag = false;
    }

    /**
     * Método invocado automáticamente cuando se supera el tiempo máximo
     * permitido para calcular un movimiento.
     * <p>
     * Marca el flag de timeout para detener la búsqueda iterativa
     * de forma segura.
     * </p>
     */
    @Override
    public void timeout() {
        timeoutFlag = true;
    }

    /**
     * Calcula la mejor jugada para el estado actual del juego utilizando
     * Iterative Deepening Search (IDS).
     * <p>
     * La búsqueda comienza con profundidad 1 y va incrementándose
     * progresivamente hasta alcanzar la profundidad máxima o
     * hasta que se produzca un timeout.
     * </p>
     *
     * En cada iteración se guarda la mejor jugada completa encontrada,
     * de modo que siempre se dispone de una solución válida aunque
     * la búsqueda se interrumpa.
     *
     * @param gs Estado actual del juego
     * @return Objeto {@link PlayerMove} con la mejor secuencia de movimientos
     *         encontrada antes de finalizar el tiempo
     */
    @Override
    public PlayerMove move(GameStatus gs) {

        PlayerType me = gs.getCurrentPlayer();
        timeoutFlag = false;

        List<Point> bestPathGlobal = new ArrayList<>();
        int profundidad = 1;

        // Búsqueda iterativa en profundidad
        while (!timeoutFlag && profundidad <= super.profundidadMaxima) {

            GameStatusAlurin aux = new GameStatusAlurin(gs);
            List<Point> currentPath = new ArrayList<>();

            try {
                // Construcción de jugada completa (capturas consecutivas)
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

                // Guardamos la mejor solución completa encontrada
                if (!timeoutFlag && !currentPath.isEmpty()) {
                    bestPathGlobal = new ArrayList<>(currentPath);
                }

            } catch (Exception e) {
                // En caso de interrupción inesperada, se devuelve el mejor resultado conocido
            }

            profundidad++;
        }

        // Fallback de seguridad si no se ha podido calcular ninguna jugada
        if (bestPathGlobal.isEmpty()) {
            List<Point> moves = gs.getMoves();
            if (!moves.isEmpty()) {
                bestPathGlobal.add(moves.get(0));
            }
        }

        return new PlayerMove(bestPathGlobal, 0, 0, SearchType.MINIMAX_IDS);
    }
}
