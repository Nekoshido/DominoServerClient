package clientp1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author hector
 */
public class Partida {
    private ArrayList<Fitxa> maClient;//Ma del client
    private ArrayList<Fitxa> taulell = new ArrayList();//Taulell del domino

    public Partida() {
        this.maClient = new ArrayList();//Ma del client
        this.taulell = new ArrayList();//Taulell del domino
        
    }

    public ArrayList<Fitxa> getMaClient() {
        return maClient;
    }

    public void setMaClient(ArrayList<Fitxa> maClient) {
        this.maClient = maClient;
    }

    public ArrayList<Fitxa> getTaulell() {
        return taulell;
    }

    public void setTaulell(ArrayList<Fitxa> taulell) {
        this.taulell = taulell;
    }
    
    public void afegirFitxaTaulellDreta(Fitxa fitxa){
        taulell.add(fitxa);
    }
    
    public void afegirFitxaTaulellEsquerra(Fitxa fitxa){
        taulell.add(0, fitxa);
    }
    
    public void toStringTaulell(){
        String fitxestaulell = "";
        for(int i =0; i<taulell.size();i++){
            fitxestaulell =  fitxestaulell +  " |" + taulell.get(i).toString()+ "|";
        }
        System.out.println(fitxestaulell);
    }
    
    public void toStringMaClient(){
        for(int i =0; i<maClient.size();i++){
            System.out.println("("+ (i+1) + ")-" + maClient.get(i).toString());
        }
    }
}
