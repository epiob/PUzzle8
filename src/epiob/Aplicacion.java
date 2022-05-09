/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epiob;

import java.util.List;
public class Aplicacion {
    public static void main(String[] args) {
        Puzzle8 p = new Puzzle8();
        p.mezclar(70);  
        System.out.println("Estado Inicial:");
        p.imprimir();
        List<Puzzle8> solution;
        System.out.println("Estado Final con A*:");
        solution = p.aEstrella();
        Puzzle8.impriSol(solution);        
    }
}