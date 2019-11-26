/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Bonex & Admir M.
 */
public class NavalBattleServer { 
    /**
     * @param args
     */
    static Semaphore turnPlay = new Semaphore(1);
    static Semaphore turnPlay2 = new Semaphore(0);
    static String state="go";
    static Box bPlayerOne[][] = new Box[21][21];
    static Box bPlayerTwo[][] = new Box[21][21]; 
    static ArrayList<Boat> Boats = new ArrayList<Boat>();//Riempito e clonato per ogni giocatore
    static String pos;
    public static void main(String[] args) throws IOException 
    {
           initMatrix (bPlayerOne);//Inizializzazione matrice a m
                initMatrix (bPlayerTwo);
        
        
        try{
            Boat b1 = new Boat(1,"o");
            Boats.add(b1);
            Boat b2 = new Boat(2,"v");
           Boats.add(b2);
            Boat b3 = new Boat(3,"v");
            Boats.add(b3);
            Boat b4 = new Boat(3,"o");
            Boats.add(b4);
            Boat b5 = new Boat(3,"o");
            Boats.add(b5);
            Boat b6 = new Boat(4,"v");
            Boats.add(b6);
            Boat b7 = new Boat(5,"o");
            Boats.add(b7);
                
            ServerSocket server = new ServerSocket(6012);
            System.out.println("BServer is onine!");
            ExecutorService ListaConnessioni = Executors.newFixedThreadPool(2);
            while(true)
            {
             
                ListaConnessioni.execute(new Game(server.accept(),"1",bPlayerOne,bPlayerTwo,Boats));              
                ListaConnessioni.execute(new Game(server.accept(),"2",bPlayerTwo,bPlayerOne,Boats));
            }
        }
        catch(Exception e)
        {
            System.out.println("Errore server"+ e);
        }
        
        
    }
        static void initMatrix(Box a[][])
        {
           for(int i = 0; i<21;i++)
         {
             for(int j = 0; j<21;j++)
            {
             a[i][j] = new Box();
            }
         } 
        }
    
    
}
