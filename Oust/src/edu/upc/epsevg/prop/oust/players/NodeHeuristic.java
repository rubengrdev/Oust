/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.oust.players;

import java.awt.Point;
import java.util.List;

/**
 *
 * @author pau
 */
public class NodeHeuristic extends AlurinPlayer{
      
    public NodeHeuristic(String name, int prof) {
        super(name, prof);
    }
    
    public int minmax(int prof, List<Point> moves, int alpha, int beta, int turno) {
        
        System.out.println("lista: " + moves.size());
        return 6;
        
    }
}
