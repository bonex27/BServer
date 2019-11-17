/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bonex & Admir M.
 */
public class Game implements Runnable{

    private char sName;
    Socket socket;
    Scanner input;
    PrintWriter output;
    private ArrayList<Boat> Boats = new ArrayList<Boat>();
    private Box refGrid[][];
    private Box refOpponent[][];
    

    String[] arrOfStr;
    
    public Game(Socket socket, char sName,Box refGrid[][],Box refOpponent[][],ArrayList<Boat> Boats) 
    {
        this.socket = socket;
        this.sName = sName;
        this.refGrid = refGrid;
        this.refOpponent = refOpponent;
        this.Boats = (ArrayList)Boats.clone();//Copia delle barche pronte a esssere inserite
    }

    @Override
    public void run() 
    {
        try {
            input = new Scanner(socket.getInputStream());
            output = new PrintWriter(socket.getOutputStream(),true);
            System.out.println(sName);
            setup();           
        }                 
        catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    private void showMatrix()
    {
        String a = "";
        for(int i = 0; i<21;i++)
         {
             for(int j = 0; j<21;j++)
            {
                a += refGrid[i][j].contenuto+"|";
                
            }
             
             a+="#";
         } 
       // output.println(a);
        output.println(a);
    }
    
            
    private String setBoat(int x,int y,char cOr,String boatName,Boat b)
    {
        if(this.checkSpazio(x, y, b.iLunghezza, cOr) == true)
        {
            int iL;
            if(cOr == 'v')
            {
                iL = x;
                for(int i = y; i < y+b.iLunghezza;i++)
                   {
                       refGrid[x][i].contenuto = 'b';
                       refGrid[x][i].nomeBarca= boatName;
                       //System.out.println("Add");

                   }
                return "ADD";
            }
            else if(cOr == 'o')
            {
                iL = y;
                for(int i = x; i < x+b.iLunghezza;i++)
                   {
                       refGrid[iL][y].contenuto = 'b';
                       refGrid[iL][y].nomeBarca= boatName;
                       //System.out.println("Add");

                   }
                return "ADD";
            }           
        }
        return "NEAR";
    }
    public boolean checkSpazio(int x,int y,int iLung,char cOr)//Controlla in tutte le 8 caselle vicine 
    {
        if(cOr == 'o')
        {
            int iX;
            int iY;
            for(iX = x-1; iX < x+iLung;iX++){
                for(iY = y-1; iY < y+2; iY+=2){
                    if(refGrid[iX][iY].contenuto == 'b')
                        return false;
                }
            }
            if(refGrid[x][y-1].contenuto == 'b')
                        return false;
            if(refGrid[x+iLung][y].contenuto == 'b')
                        return false;
        return true;
        }
        else if(cOr == 'v')
        {
            int iX;
            int iY;
            for(iY = y-1; iY < y+iLung;iY++){
                for(iX = x-1; iX < x+2; iX+=2){
                    if(refGrid[iX][iY].contenuto == 'b')
                        return false;
                }
            }
            if(refGrid[x-1][y].contenuto == 'b')
                        return false;
            if(refGrid[x][y+iLung].contenuto == 'b')
                        return false;
        return true;
        }       
        return false;
    }
    
    
    public boolean attackBoat(int x,int y)
    {
        //for(int i=0;i<)
       if(refOpponent[x][y].contenuto=='b'&& refOpponent[x][y].contenuto=='m')
         {
            refOpponent[x][y].contenuto='d';
            
               return true;
         }
        else if(refOpponent[x][y].contenuto=='d')
         {
                return false;
          }
           
        return false;
    }
  
    public void setup()
    {
        String comando;
        String chkAdd;
        //System.out.println(this.sName+ " connesso!");
         
            try{
            for(int i = 0; i < Boats.size();i++)
                {
                        output.println(this.sName+"@p");//client
                        //input.nextLine();

                        do
                        {
                            output.println(Boats.get(i).iLunghezza+"@"+Boats.get(i).nome);
                            comando = input.nextLine();
                            arrOfStr= comando.split("@", 10);
                            chkAdd = this.setBoat(Integer.parseInt(arrOfStr[0]),Integer.parseInt(arrOfStr[1]),arrOfStr[2].charAt(0),Boats.get(i).nome,Boats.get(i));
                            output.println(chkAdd);
                        }while(chkAdd.equals("NEAR") );
                        //showMatrix();
                    }
                }
            catch(Exception e)
            {
                System.out.println(e);
            }
    }
    //controllo il turno del giocatore e se hai un avversario


}
    

