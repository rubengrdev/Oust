/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package edu.upc.epsevg.prop.oust;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pau
 */
public class GameStatusAlurin extends GameStatus{
    
    public GameStatusAlurin(GameStatus gs) {
        super(gs);
    }
    
    //recompte de peces al tauler segons el color
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
