/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package edu.upc.epsevg.prop.oust;

import java.awt.Point;
import java.util.List;

/**
 *
 * @author pau
 */
public class GameSatusAlrurin extends GameStatus{
    
    public GameSatusAlrurin(GameStatus gs) {
        super(gs);
    }
    
        public int minmax(GameStatus gs, int prof) {
            return 6;
        }
    
}
