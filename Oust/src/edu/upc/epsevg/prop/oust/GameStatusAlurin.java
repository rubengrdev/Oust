package edu.upc.epsevg.prop.oust;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Extensión de la clase {@link GameStatus} que añade métodos auxiliares
 * para facilitar la evaluación heurística del estado del juego Oust.
 * <p>
 * Esta clase permite obtener información adicional como:
 * </p>
 * <ul>
 *   <li>El número total de piezas de un jugador en el tablero</li>
 *   <li>Las posiciones ocupadas por las piezas de un jugador</li>
 * </ul>
 *
 * Está pensada para ser utilizada por jugadores automáticos
 * basados en algoritmos de búsqueda como Minimax.
 *
 * @author Rubén Gómez y Pau Espuń Ferrer
 */
public class GameStatusAlurin extends GameStatus {

    /**
     * Constructor que crea una copia del estado del juego recibido.
     * <p>
     * Se utiliza para generar estados sucesores sin modificar
     * el estado original durante la exploración del árbol de juego.
     * </p>
     *
     * @param gs Estado del juego a copiar
     */
    public GameStatusAlurin(GameStatus gs) {
        super(gs);
    }

    /**
     * Devuelve el número total de piezas de un jugador concreto
     * que se encuentran actualmente en el tablero.
     *
     * @param p Jugador del cual se desea contar las piezas
     * @return Número de piezas del jugador {@code p} en el tablero
     */
    public int getPieceCount(PlayerType p) {
        int count = 0;
        int size = this.getSize();

        for (int i = 0; i < 2 * size - 1; i++) {
            for (int j = 0; j < 2 * size - 1; j++) {
                Point current = new Point(i, j);
                if (isInBounds(current) && getColor(current) == p) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Devuelve una lista con las posiciones de todas las piezas
     * pertenecientes a un jugador concreto.
     *
     * @param p Jugador del cual se desean obtener las posiciones
     * @return Lista de {@link Point} con las coordenadas ocupadas
     *         por el jugador {@code p}
     */
    public List<Point> getPieceLocations(PlayerType p) {
        List<Point> locations = new ArrayList<>();
        int size = this.getSize();
        int dim = 2 * size - 1;

        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                Point current = new Point(i, j);

                if (isInBounds(current) && getColor(current) == p) {
                    locations.add(current);
                }
            }
        }
        return locations;
    }
}
