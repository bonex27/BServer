/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BServer;

import java.util.ArrayList;

/**
 * @author Bonex & Admir M.
 * @param iLunghezza
 * Boat lenght
 * @param bPosizione
 * Box occuped by this boat
 * 
 */
public class Boat {
    public  int iLunghezza ;
    public ArrayList<Box> bPosizione = new ArrayList<Box>();
    public String nome;

    public Boat(int iLunghezza,String nome) {
        this.iLunghezza=iLunghezza;
        this.nome = nome;
    }
    
    /**
     * @param a
     * Boat how need check
    *@code Check if a boat is alive and if is true it return  true
    */
    public boolean checkCondizione(Boat a)
    {
        for(int i = 0; i < a.iLunghezza;i++)
        {
            if(a.bPosizione.get(i).contenuto == 'm')
            {
                return true;
            }   
        }
        return false;
    }

    public int getiLunghezza() {
        return iLunghezza;
    }

    public void setiLunghezza(int iLunghezza) {
        this.iLunghezza = iLunghezza;
    }

    public ArrayList<Box> getbPosizione() {
        return bPosizione;
    }

    public void setbPosizione(ArrayList<Box> bPosizione) {
        this.bPosizione = bPosizione;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
}
