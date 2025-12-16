/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.oust.players;

import edu.upc.epsevg.prop.oust.GameStatus;
import edu.upc.epsevg.prop.oust.IAuto;
import edu.upc.epsevg.prop.oust.IPlayer;
import edu.upc.epsevg.prop.oust.PlayerMove;
import edu.upc.epsevg.prop.oust.PlayerType;
import edu.upc.epsevg.prop.oust.SearchType;
import edu.upc.epsevg.prop.oust.GameSatusAlrurin;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author pau
 */
public class AlurinPlayer implements IPlayer, IAuto {

    private String name;

    private int profundidadMaxima;

    public AlurinPlayer(String name, int prof) {
        this.name = name;
        this.profundidadMaxima = prof;
    }

    @Override
    public void timeout() {
        // Nothing to do! I'm so fast, I never timeout 8-)

    }
    
    public int heuristica() {
        //heuristica return 6 ftw
        /**
         * Habrá que pasarle los params cómo mínimo de gs y MiJugador
         */
        return 6;
    }
    
    
 
    
    public int minmax(GameStatus gs, int prof, boolean max, PlayerType MiJugador) {
        List<Point> moves = gs.getMoves();
        
        if (prof == 0 || moves.isEmpty()) {
            return heuristica();
        }
        
        if (max) {
            int millorValor = Integer.MIN_VALUE;
            for (Point m : moves) {
                GameStatus seguent = new GameStatus(gs);
                seguent.placeStone(m);

                boolean maximitza = (MiJugador == seguent.getCurrentPlayer());

                int valor = minmax(seguent, prof - 1, maximitza, MiJugador);

                millorValor = Math.max(valor, millorValor);
            }
            return millorValor;
        } else {
            int millorValor = Integer.MAX_VALUE;
            for (Point m : moves) {
                GameStatus seguent = new GameStatus(gs);
                seguent.placeStone(m);
                
                boolean maximitza = !(MiJugador == seguent.getCurrentPlayer());
                
                int valor = minmax(seguent, prof - 1, maximitza, MiJugador);
                
                millorValor = Math.min(valor, millorValor);
            }
            return millorValor;
        }
       
    }

    /**
     * Decideix el moviment del jugador donat un tauler i un color de peça que
     * ha de posar.
     *
     * @param gs Tauler i estat actual de joc.
     * @return el moviment que fa el jugador.
     */
    @Override
    public PlayerMove move(GameStatus gs) {
        int millorValor = Integer.MIN_VALUE;
        PlayerMove movimentFinal = null;
        List<Point> moves = gs.getMoves();
        PlayerType MiJugador = gs.getCurrentPlayer();
        
        for (Point m : moves) {
            GameStatus aux = new GameStatus(gs);
            List<Point> path = new ArrayList<>();
            
            aux.placeStone(m);
            path.add(m);
            
            boolean MiTurno = (MiJugador == aux.getCurrentPlayer());
            
            int valor = minmax(aux, this.profundidadMaxima - 1, MiTurno, MiJugador);
            
            if (valor > millorValor) {
                millorValor = valor;
                movimentFinal = new PlayerMove(path, 0, 0, SearchType.MINIMAX);
            }
        }
        return movimentFinal;
    }

    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public String getName() {
        return "Random(" + name + ")";
    }
}
