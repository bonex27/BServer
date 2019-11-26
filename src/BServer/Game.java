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
public class Game implements Runnable {

    private String sName;
    Socket socket;
    Scanner input;
    PrintWriter output;
    private ArrayList<Boat> Boats = new ArrayList<Boat>();
    private Box refGrid[][];
    private Box refOpponent[][];
    String comando;
    String sChk;
    String f;
    String[] arrOfStr;
    int boatState;
    String s;

    public Game(Socket socket, String sName, Box refGrid[][], Box refOpponent[][], ArrayList<Boat> Boats) {
        this.socket = socket;
        this.sName = sName;
        this.refGrid = refGrid;
        this.refOpponent = refOpponent;
        this.Boats = (ArrayList) Boats.clone();//Copia delle barche pronte a esssere inserite
    }

    @Override
    public void run() {
        try {
            input = new Scanner(socket.getInputStream());
            output = new PrintWriter(socket.getOutputStream(), true);
            System.out.println(sName);
            setup();
            play();
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
//        }
        }
    }

    private void play() {
        try {
            do {
                //Thread.sleep(100);

                if (sName.equals("1")) {
                    NavalBattleServer.turnPlay.acquire();

                } else {

                    NavalBattleServer.turnPlay2.acquire();

                }
                if (NavalBattleServer.state.equals("stop")) {
                    output.println("lose");
                } 
                else {
//                    if (true == NavalBattleServer.posi) {
//
//                        s = NavalBattleServer.x + "@" + NavalBattleServer.y + "@" + NavalBattleServer.Posbarca;
//                        output.println(this.sName + "@a" + "@" + s);
////                        NavalBattleServer.control1=true;
//
//                    } //                     else if(true ==NavalBattleServer.control1)
//                    //                     {
//                    //                         s= NavalBattleServer.x+"@"+ NavalBattleServer.y+ "@"+ NavalBattleServer.Posbarca;
//                    //                         output.println(this.sName+ "@a"+"@"+s);
//                    //                          NavalBattleServer.posi=false;
//                    //                     }
//                    else {
                      output.println(this.sName + "@a");
//                    }

                    comando = input.nextLine();
                    arrOfStr = comando.split("@", 10);
                    sChk = this.attackBoat(Integer.parseInt(arrOfStr[2]), Integer.parseInt(arrOfStr[3]), refOpponent);

//                        NavalBattleServer.x = Integer.parseInt(arrOfStr[2]);
//                        NavalBattleServer.y = Integer.parseInt(arrOfStr[3]);
//                        NavalBattleServer.Posbarca = sChk;
                    output.println(sChk);

                    if (sName.equals("1")) {
//                        NavalBattleServer.x = Integer.parseInt(arrOfStr[2]);
                        NavalBattleServer.y = Integer.parseInt(arrOfStr[3]);
                        NavalBattleServer.Posbarca = sChk;
                        NavalBattleServer.turnPlay2.release();
                        NavalBattleServer.posi = true;
                    } else //                    {if (NavalBattleServer.control1 == true) {
                    //                            NavalBattleServer.posi = true;
                    //                            NavalBattleServer.control1 =false;
                    //                        }
                    {
                        NavalBattleServer.y = Integer.parseInt(arrOfStr[3]);
                        NavalBattleServer.Posbarca = sChk;
                        NavalBattleServer.turnPlay2.release();
                        NavalBattleServer.posi = true;
                        NavalBattleServer.turnPlay.release();
                    }

                }

            } while (!NavalBattleServer.state.equals("stop"));
        } catch (Exception e) {
            System.out.println("errore attacco " + e);
        }

    }

    private void showMatrix() {

        String a = "";
        for (int i = 0; i < 21; i++) {
            for (int j = 0; j < 21; j++) {
                a += refGrid[j][i].contenuto + "|";
            }
            a += "#";
        }
        output.println(a);
    }

    public String checkBoat(Box refOpponent[][], int x, int y) {
        int iCounter = 0;
        int iLung = 0;
        int statBoat;
        for (int i = 0; i < Boats.size(); i++) {
            if (Boats.get(i).getNome().equals(refOpponent[x][y].getNomeBarca())) {
                iLung = Boats.get(i).iLunghezza;
            }
        }

        for (int i = 0; i < 21; i++) {
            for (int j = 0; j < 21; j++) {
                if (refOpponent[i][j].getNomeBarca() != null) {
                    if (refOpponent[i][j].getNomeBarca().equals(refOpponent[x][y].getNomeBarca()) && refOpponent[i][j].getContenuto() == 'c') {
                        iCounter++;
                    }
                }
            }
        }

        if (iCounter == 3) {
            this.boatState++;
            if (boatState == Boats.size()) {
                NavalBattleServer.state = "stop";
                return "win";
            }
            return "d";
        } else {
            return "c";
        }
    }

    private String setBoat(int l, char a, int x, int y) {
        int j = 0;
        if (this.checkSpazio(x, y, l, a) == true) {
            if (a == 'v') {
                for (int i = y; i < y + l; i++) {
                    refGrid[x][i].contenuto = 'b';

                }
                return "ADD";
            } else if (a == 'o') {
                for (int i = x; i < x + l; i++) {
                    refGrid[i][y].contenuto = 'b';
                }
                return "ADD";
            }
        }
        return "NEAR";
    }

    public boolean checkSpazio(int x, int y, int iLung, char orientation)//Controlla in tutte le 8 caselle vicine 
    {
        switch (orientation) {
            case 'v':
                for (int X = x - 1; X < x + 2; X++) {
                    if (x == 0) {
                        X++;
                    }
                    if (X == 21) {
                        X++;
                    }
                    for (int Y = y - 1; Y <= y + iLung; Y++) {
                        if (y == 0) {
                            Y++;
                        }
                        if (y + iLung == 21) {
                            Y++;
                        }
                        if (refGrid[X][Y].contenuto == 'b') {
                            return false;
                        }
                    }
                }
                return true;

            case 'o':
                for (int Y = y - 1; Y < y + 2; Y++) {
                    if (y == 0) {
                        Y++;
                    }
                    if (Y == 21) {
                        Y++;
                    }
                    for (int X = x - 1; X <= x + iLung; X++) {
                        if (x == 0) {
                            X++;
                        }
                        if (x + iLung == 21) {
                            X++;
                        }
                        if (refGrid[X][Y].contenuto == 'b') {
                            return false;
                        }
                    }
                }
                return true;
        }
        return false;
    }

    public String attackBoat(int x, int y, Box refOpponent[][]) {

        if (refOpponent[x][y].contenuto == 'b') {
            refOpponent[x][y].contenuto = 'c';
            // NavalBattleServer.pos+=x+"@"+y;
            return checkBoat(refOpponent, x, y);
        } else if (refOpponent[x][y].contenuto == 'c') {
            return "gc";//Gia colpita
        } else {
            return "m";
        }

    }

    public void setup() {
        int f = 0;
        try {
            for (int i = 0; i < Boats.size(); i++) {

                output.println(this.sName + "@p");//client

                do {
                    output.println(Boats.get(i).iLunghezza + "@" + Boats.get(i).nome);
                    comando = input.nextLine();
                    arrOfStr = comando.split("@", 10);
                    sChk = this.setBoat(Integer.parseInt(arrOfStr[0]), arrOfStr[1].charAt(0), Integer.parseInt(arrOfStr[2]), Integer.parseInt(arrOfStr[3]));
                    output.println(sChk);
                } while (sChk.equals("NEAR"));

            }
            output.println(this.sName + "@v");
            if (input.nextLine().equals("stampa")) {
                showMatrix();
            }
        } catch (Exception e) {
            System.out.println(e + "ci");
        }
    }

}
