/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epiob;
import java.util.*;
public class Puzzle8 {

    private class posFicha {
        public int x;
        public int y;
        public posFicha(int x, int y){
            this.x = x;
            this.y = y; 
        }
    }
    public final static int DIMS=3;
    private int[][] tiles;
    private int display_width;
    private posFicha blank;
    
    public Puzzle8() {
        tiles = new int[DIMS][DIMS];
        int cnt=1;
        for(int i=0; i<DIMS; i++) {
            for(int j=0; j<DIMS; j++) {
                tiles[i][j]=cnt;
                cnt++;
            }
        }
        display_width=Integer.toString(cnt).length();
        blank = new posFicha(DIMS-1,DIMS-1);
        tiles[blank.x][blank.y]=0;
    }
    
    public final static Puzzle8 SOLVED=new Puzzle8();
    public Puzzle8(Puzzle8 toClone) {
        this();  
        for(posFicha p: todasPosFichas()) { 
            tiles[p.x][p.y] = toClone.ficha(p);
        }
        blank = toClone.getBlank();
    }
    
    public List<posFicha> todasPosFichas() {
        ArrayList<posFicha> out = new ArrayList<posFicha>();
        for(int i=0; i<DIMS; i++) {
            for(int j=0; j<DIMS; j++) {
                out.add(new posFicha(i,j));
            }
        }
        return out;
    }
    
    public int ficha(posFicha p) {
        return tiles[p.x][p.y];
    }
    
    public posFicha getBlank() {
        return blank;
    }
    
    public posFicha whereIs(int x) {
        for(posFicha p: todasPosFichas()) { 
            if( ficha(p) == x ) {
                return p;
            }
        }
        return null;
    }
    
    @Override
    
    public boolean equals(Object o) {
        if(o instanceof Puzzle8) {
            for(posFicha p: todasPosFichas()) { 
                if( this.ficha(p) != ((Puzzle8) o).ficha(p)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int out=0;
        for(posFicha p: todasPosFichas()) {
            out= (out*DIMS*DIMS) + this.ficha(p);            
        }
        return out;
    }
    
    public void imprimir() {
        System.out.println("-------------");
        for(int i=0; i<DIMS; i++) {
            System.out.print("| ");
            for(int j=0; j<DIMS; j++) {
                int n = tiles[i][j];
                String s;
                if( n>0) {
                    s = Integer.toString(n);    
                }else{
                    s = "";
                }while( s.length() < display_width ) {
                    s += " ";
                }
                System.out.print(s + "| ");
            }
            System.out.print("\n");
        }
        System.out.print("-------------\n\n");
    }    
    
    public List<posFicha> todosMovVali() {
        ArrayList<posFicha> out = new ArrayList<posFicha>();
        for(int dx=-1; dx<2; dx++) {
            for(int dy=-1; dy<2; dy++) {
                posFicha tp = new posFicha(blank.x + dx, blank.y + dy);
                if( movVali(tp) ) {
                    out.add(tp);
                }
            }
        }
        return out;
    }
    
    public boolean movVali(posFicha p) {
        if( ( p.x < 0) || (p.x >= DIMS) ) {
            return false;
        }
        if( ( p.y < 0) || (p.y >= DIMS) ) {
            return false;
        }
        int dx = blank.x - p.x;
        int dy = blank.y - p.y;
        if( (Math.abs(dx) + Math.abs(dy) != 1 ) || (dx*dy != 0) ) {
            return false;
        }
          return true;  
    }
    
    public void move(posFicha p) {
        if( !movVali(p) ) {
            throw new RuntimeException("Invalid move");
        }
        assert tiles[blank.x][blank.y]==0;
        tiles[blank.x][blank.y] = tiles[p.x][p.y];
        tiles[p.x][p.y]=0;
        blank = p;
    }
    
    public Puzzle8 movFicha(posFicha p) {
        Puzzle8 out = new Puzzle8(this);
        out.move(p);
        return out;
    }
    
    public void mezclar(int howmany) {
        for(int i=0; i<howmany; i++) {
            List<posFicha> possible = todosMovVali();
            int which =  (int) (Math.random() * possible.size());
            posFicha move = possible.get(which);
            this.move(move);
        }
    }
    
    public void mezclar() {
        mezclar(DIMS*DIMS*DIMS*DIMS*DIMS);
    }
    
    public int numTableros() {
        int wrong=0;
        for(int i=0; i<DIMS; i++) {
            for(int j=0; j<DIMS; j++) {
                if( (tiles[i][j] >0) && ( tiles[i][j] != SOLVED.tiles[i][j] ) ){
                    wrong++;
                }
            }
        }
        return wrong;
    }
    
    public boolean esResuelto() {
        return numTableros() == 0;
    }
    
    public int distManhattan() {
        int sum=0;
        for(posFicha p: todasPosFichas()) {
            int val = ficha(p);
            if( val > 0 ) {
                posFicha correct = SOLVED.whereIs(val);
                sum += Math.abs( correct.x = p.x );
                sum += Math.abs( correct.y = p.y );
            }
        }
        return sum;
    }
    
    public int errorEsti() {
        return this.numTableros();
    }
    
    public List<Puzzle8> movAdyacen() {
        ArrayList<Puzzle8> out = new ArrayList<Puzzle8>();
        for( posFicha move: todosMovVali() ) {        
            out.add( movFicha(move) );
        }
        return out;
    }
    
    public List<Puzzle8> aEstrella() {
        HashMap<Puzzle8,Puzzle8> predecessor = new HashMap<Puzzle8,Puzzle8>();
        HashMap<Puzzle8,Integer> depth = new HashMap<Puzzle8,Integer>();
        final HashMap<Puzzle8,Integer> score = new HashMap<Puzzle8,Integer>();
        Comparator<Puzzle8> comparator = new Comparator<Puzzle8>() {
            @Override
            public int compare(Puzzle8 a, Puzzle8 b) {
                return score.get(a) - score.get(b);
            }
        };
        PriorityQueue<Puzzle8> toVisit = new PriorityQueue<Puzzle8>(10000,comparator);
        predecessor.put(this, null);
        depth.put(this,0);
        score.put(this, this.errorEsti());
        toVisit.add(this);
        int cnt=0;
        while( toVisit.size() > 0) {
            Puzzle8 candidate = toVisit.remove();
            cnt++;
            if( cnt % 10000 == 0) {
                System.out.printf("Considerado %,d posiciones. Cola = %,d\n", cnt,toVisit.size());
            }
            if( candidate.esResuelto() ) {
                System.out.printf("Solución considerada con %d tableros\n", cnt);
                LinkedList<Puzzle8> solution = new LinkedList<Puzzle8>();
                Puzzle8 backtrace=candidate;
                while( backtrace != null ) {
                    solution.addFirst(backtrace);
                    backtrace = predecessor.get(backtrace);
                }
                return solution;
            }
            for(Puzzle8 fp: candidate.movAdyacen()) {
                if( !predecessor.containsKey(fp) ) {
                    predecessor.put(fp,candidate);
                    depth.put(fp, depth.get(candidate)+1);
                    int estimate = fp.errorEsti();
                    score.put(fp, depth.get(candidate)+1 + estimate);
                    toVisit.add(fp);
                }
            }
        }
        return null;
    }
    
    public static void impriSol(List<Puzzle8> solution) {
        if (solution != null ) {
            System.out.printf("Resuelto en %d pasos:\n", solution.size());
            for( Puzzle8 sp: solution) {
                sp.imprimir();
            }
        }else{
            System.out.println("No resuelto: (");            
        }
    }
    
}