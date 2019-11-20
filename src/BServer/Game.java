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

    private String sName;
    Socket socket;
    Scanner input;
    PrintWriter output;
    private ArrayList<Boat> Boats = new ArrayList<Boat>();
    private Box refGrid[][];
    private Box refOpponent[][];
    String comando;
    String sChk;
    int boatState=0;
    String[] arrOfStr;
    
    public Game(Socket socket, String sName,Box refGrid[][],Box refOpponent[][],ArrayList<Boat> Boats) 
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
            play();
        }                 
        catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void play() throws InterruptedException
    {
        try
        {
            do
            {
                //Thread.sleep(100);

                if(sName.equals("1"))
                    NavalBattleServer.turnPlay.acquire();
                
                else
                    NavalBattleServer.turnPlay2.acquire();
                if(NavalBattleServer.state.equals("stop"))
                    output.println("lose");
                else
                {
                output.println(this.sName+"@a");
                comando = input.nextLine();
                arrOfStr= comando.split("@", 10);
                sChk = this.attackBoat(Integer.parseInt(arrOfStr[0]),Integer.parseInt(arrOfStr[1]));                
                output.println(sChk);                
                                  
                if(sName.equals("1"))                   
                    NavalBattleServer.turnPlay2.release();
                
                else                   
                    NavalBattleServer.turnPlay.release();
                }
            }while(!NavalBattleServer.state.equals("stop"));
        }
        catch(Exception e)
        {
            System.out.println("errore attacco "+ e);
        }
            
    }
    private void showMatrix()
    {
        
            String a = "";
            for(int i = 0; i<21;i++){
                for(int j = 0; j<21;j++){
                    a += refGrid[j][i].contenuto+"|";
                }
                a+="#";
            }
            output.println(a);
    }
    public String checkBoat(Box refOpponent[][],int x, int y)
    {
        int iCounter =0 ;
        int iLung = 0;
        int statBoat;
        for(int i = 0; i < Boats.size();i++)
            if(Boats.get(i).getNome().equals(refOpponent[x][y].getNomeBarca()))
                iLung = Boats.get(i).iLunghezza;
            


        for(int i = 0;i<21;i++)
            for(int j = 0; j < 21; j++)            
                if(refOpponent[i][j].getNomeBarca()!= null)                
                    if(refOpponent[i][j].getNomeBarca().equals(refOpponent[x][y].getNomeBarca())   && refOpponent[i][j].getContenuto() == 'c')
                    {
                        iCounter++;
                    }              
            
        
        
        if(iCounter == iLung)
        {
            this.boatState++;
            if(boatState == Boats.size())
            {
                NavalBattleServer.state = "stop";
                return "win";
            }
            return "d";
        }
            
        else
            return"c";
    }
            
    private String setBoat(int x,int y,char cOr,String boatName,Boat b)
    {
        int j=0;
        if(this.checkSpazio(x, y, b.iLunghezza, cOr) == true)
        {
            if(cOr == 'v')
            {
                for(int i = y; i < y+b.iLunghezza;i++,j++)
                   {
                       refGrid[x][i].contenuto = 'b';
                       refGrid[x][i].nomeBarca= boatName;;
                       this.Boats.get(j).bPosizione.add(refGrid[x][i]);

                   }
                return "ADD";
            }
            else if(cOr == 'o')
            {
                for(int i = x; i < x+b.iLunghezza;i++,j++)
                   {
                        refGrid[i][y].contenuto = 'b';
                        refGrid[i][y].nomeBarca= boatName;
                        this.Boats.get(j).bPosizione.add(refGrid[i][y]);
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
           
            for(iX = x; iX < x+iLung;iX++){
              
                    if(refGrid[iX][y].contenuto == 'b')
                        return false;
                
            }
            if(refGrid[x][y-1].contenuto == 'b')
                        return false;
            if(refGrid[x+iLung][y].contenuto == 'b')
                        return false;
        return true;
        }
        else if(cOr == 'v')
        {
           
            int iY;
            for(iY = y; iY < y+iLung;iY++){
               
                    if(refGrid[x][iY].contenuto == 'b')
                        return false;
               
            }
            if(refGrid[x-1][y].contenuto == 'b')
                        return false;
            if(refGrid[x][y+iLung].contenuto == 'b')
                        return false;
        return true;
        }       
        return false;
    }
    
    
    public String attackBoat(int x,int y)
    {
        //for(int i=0;i<)
    if(x <21 && x >=0 && y <21 && y >=0)//Controllo dati x e y
       if(refOpponent[x][y].contenuto=='b')
         {
            refOpponent[x][y].contenuto='c';
               return this.checkBoat(refOpponent, x, y);              
         }
        else if(refOpponent[x][y].contenuto=='c')
         {
                return "gc";//Gia colpita
          }
        else
        {
            return "m";
        }
    else
         return "f";
    
        
    }
  
    public void setup()
    {
            try{
                for(int i = 0; i < Boats.size();i++){
                    output.println(this.sName+"@p");//client
                    //input.nextLine();
                    do{
                        output.println(Boats.get(i).iLunghezza+"@"+Boats.get(i).nome);
                        comando = input.nextLine();
                        arrOfStr= comando.split("@", 10);
                        sChk = this.setBoat(Integer.parseInt(arrOfStr[0]),Integer.parseInt(arrOfStr[1]),arrOfStr[2].charAt(0),Boats.get(i).nome,Boats.get(i));
                        output.println(sChk);
                    }while(sChk.equals("NEAR") );
                }
                output.println(this.sName+"@v");
                if(input.nextLine().equals("stampa"))
                    showMatrix();
            }
            catch(Exception e){
                System.out.println(e);
            }
    }
    //controllo il turno del giocatore e se hai un avversario
}
    

