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
import edu.upc.epsevg.prop.oust.GameStatusAlurin;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


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
    
  
    
    
    public int heuristica(GameStatus gsx, PlayerType MiJugador) {

        //identificació heuristica
        PlayerType Rival;
        if (MiJugador == PlayerType.PLAYER1) {
            Rival = PlayerType.PLAYER2;
        } else {
            Rival = PlayerType.PLAYER1;
        }
        
        /**
         * aixó és una terroristada, però: 
         * Com a moves (1a iteració, l'objecte és GameStatus) no em puc fiar de que sempre arribi un GameStatusAlurin
         */
        GameStatusAlurin gs;
        if(gsx instanceof GameStatusAlurin){
            gs = (GameStatusAlurin) gsx;
        }else{
            //cas en el que no és el tipus complex
            gs = new GameStatusAlurin(gsx);
        }

        //mira si el joc acaba o continua
        if (gs.isGameOver()) {
            if (gs.GetWinner() == MiJugador) {
                return Integer.MAX_VALUE;
            } else if (gs.GetWinner() == Rival) {
                return Integer.MIN_VALUE;
            } else {
                return 0; // Empate
            }
        }

        // conteo de peçes meves i les del rival
        int mypieces = gs.getPieceCount(MiJugador);
        int rivalpieces = gs.getPieceCount(Rival);

        // si té un multiplicador de pes, aixó dona més prioritat a l'heursítica
        int valorPeces = (mypieces - rivalpieces) * 1000;

  
      
        int boardSize = gs.getSize();
        int centerCoord = boardSize - 1; 
        int valorCentre = 0;
        int pesCentre = 5;
        
        for (Point p : gs.getPieceLocations(MiJugador)) {
            //calcul de la dist de manhattan al centre (0,0)
            int dist = Math.abs(p.x - centerCoord) + Math.abs(p.y - centerCoord);
            valorCentre -= dist; 
        }

        // peçes del rival, en quant més lluny estigui del centre millor
        for (Point p : gs.getPieceLocations(Rival)) {
            int dist = Math.abs(p.x - centerCoord) + Math.abs(p.y - centerCoord);
            valorCentre+= dist; 
        }


        return valorPeces + valorCentre * pesCentre;
    }
 
    
    public int minmax(GameStatus gs, int prof, boolean max, PlayerType MiJugador, int alpha, int beta) {
        List<Point> moves = gs.getMoves();
        
        if (prof == 0 || moves.isEmpty() || gs.isGameOver()) {
            //return heuristica();
            return heuristica(gs, MiJugador);
        }
        
        //BRANCA QUE MAXIMITZA
        if (max) {
            int millorValor = Integer.MIN_VALUE;
            for (Point m : moves) {
                GameStatusAlurin seguent = new GameStatusAlurin(gs);
                seguent.placeStone(m);

                boolean maximitza = (MiJugador == seguent.getCurrentPlayer());
                

                int valor = minmax(seguent, prof - 1, maximitza, MiJugador, alpha, beta);

                millorValor = Math.max(valor, millorValor);
                
                // tall (poda)
                alpha = Math.max(alpha, millorValor);
                if (beta <= alpha) {  
                    break; 
                }
                
            }
            return millorValor;
        } else {
            //BRANCA QUE MINIMITZA
            int millorValor = Integer.MAX_VALUE;
            for (Point m : moves) {
                GameStatusAlurin seguent = new GameStatusAlurin(gs);;
                seguent.placeStone(m);
                
                boolean maximitza = !(MiJugador == seguent.getCurrentPlayer());
                
                int valor = minmax(seguent, prof - 1, maximitza, MiJugador, alpha, beta);
                
                millorValor = Math.min(valor, millorValor);
                
                // tall (poda)
                beta = Math.min(beta, millorValor);
                if (beta <= alpha) {
                    break; 
                }
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
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        
        PlayerMove movimentFinal = null;
        List<Point> moves = gs.getMoves();
        PlayerType MiJugador = gs.getCurrentPlayer();
        
        for (Point m : moves) {
            GameStatusAlurin aux = new GameStatusAlurin(gs);
            List<Point> path = new ArrayList<>();
            
            aux.placeStone(m);
            path.add(m);
            
            boolean MiTurno = (MiJugador == aux.getCurrentPlayer());
            
            int valor = minmax(aux, this.profundidadMaxima - 1, MiTurno, MiJugador, alpha,beta);
            
            if (valor > millorValor) {
                millorValor = valor;
                movimentFinal = new PlayerMove(path, 0, 0, SearchType.MINIMAX);
                alpha = millorValor;
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
