package ServidorP1;

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
 * @author joangasullroyes
 */
public class Partida {
    private ArrayList<Fitxa> maClient;//Ma del client
    private ArrayList<Fitxa> maServidor;//Ma del servidor
    private ArrayList<Fitxa> taulell ;//Taulell del domino
    private ArrayList<Fitxa> reserva ;//Fitxes de la reserva

    public Partida() {
        this.maClient = new ArrayList();//Ma del client
        this.maServidor = new ArrayList();//Ma del servidor
        this.taulell = new ArrayList();//Taulell del domino
        this.reserva = new ArrayList();//Fitxes de la reserva
        
    }
    
    public ArrayList<Fitxa> getMaClient() {
        return maClient;
    }
    
    public int eliminarFitxaMa(Fitxa fitxa){
        for(int i =0; i<getMaClient().size();i++){
            if(fitxa.getNum1()==getMaClient().get(i).getNum1()&&fitxa.getNum2()==getMaClient().get(i).getNum2()){
                return i;
            }
            if(fitxa.getNum1()==getMaClient().get(i).getNum2()&&fitxa.getNum2()==getMaClient().get(i).getNum1()){
                return i;
            }
        }
        return 1000;
    }
    
    public int eliminarFitxaReserva(Fitxa fitxa){
        for(int i =0; i<getReserva().size();i++){
            if(fitxa.getNum1()==getReserva().get(i).getNum1()&&fitxa.getNum2()==getReserva().get(i).getNum2()){
                return i;
            }
            if(fitxa.getNum1()==getReserva().get(i).getNum2()&&fitxa.getNum2()==getReserva().get(i).getNum1()){
                return i;
            }
        }
        return 1000;
    }

    public int eliminarFitxaServidor(Fitxa fitxa){
        for(int i =0; i<getMaServidor().size();i++){
            if(fitxa.getNum1()==getMaServidor().get(i).getNum1()&&fitxa.getNum2()==getMaServidor().get(i).getNum2()){
                return i;
            }
            if(fitxa.getNum1()==getMaServidor().get(i).getNum2()&&fitxa.getNum2()==getMaServidor().get(i).getNum1()){
                return i;
            }
        }
        return 1000;
    }
    
    public void setMaClient(ArrayList<Fitxa> maClient) {
        this.maClient = maClient;
    }

    public ArrayList<Fitxa> getMaServidor() {
        return maServidor;
    }

    public void setMaServidor(ArrayList<Fitxa> maServidor) {
        this.maServidor = maServidor;
    }

    public ArrayList<Fitxa> getTaulell() {
        return taulell;
    }

    public void setTaulell(ArrayList<Fitxa> taulell) {
        this.taulell = taulell;
    }

    public ArrayList<Fitxa> getReserva() {
        return reserva;
    }

    public void setReserva(ArrayList<Fitxa> reserva) {
        this.reserva = reserva;
    }
    
    public void afegirFitxaTaulellDreta(Fitxa fitxa){
        taulell.add(fitxa);
    }
    
    public void afegirFitxaTaulellEsquerra(Fitxa fitxa){
        taulell.add(0, fitxa);
    }
    
    public void toStringTaulell(){
        for(int i =0; i<taulell.size();i++){
            System.out.println(taulell.get(i).toString());
        }
    }
    
    public void Inicialitzar(){
        for(int i =0; i<7; i++){
            for (int j=0; j<=i; j++){
                //System.out.println("I"+i+" J"+j);//Comprovacio que es creen les fitxes
                Fitxa fitxa = new Fitxa(i, j,'N', 'N');
                reserva.add(fitxa);
            }
        }
        Collections.shuffle(reserva);
        Collections.shuffle(reserva);//Es barregen un segon cop per dona mes random a les fitxes.
    }
    
    
    public Fitxa escollirmillorFitxa(ArrayList<Fitxa> ma){
        
        Iterator itrFitxa = ma.iterator();
        Fitxa fitxa = (Fitxa) itrFitxa.next();
        Fitxa millor_fitxa = fitxa;
        
        while(itrFitxa.hasNext()){
            
            if( millor_fitxa.esDoble() && fitxa.esDoble() ){
                if(fitxa.getValor()>millor_fitxa.getValor()){
                    millor_fitxa = fitxa;
                }
            }
            else if(!millor_fitxa.esDoble() && fitxa.esDoble()){
                millor_fitxa = fitxa;
            }
            else{
                 if(fitxa.getValor()>millor_fitxa.getValor()){
                    millor_fitxa = fitxa;
                }
            }
            fitxa = (Fitxa) itrFitxa.next();
        }
        return millor_fitxa;
    }
 
    public void toStringMaServidor(){
        for(int i =0; i<maServidor.size();i++){
            System.out.println(maServidor.get(i).toString());
        }
    }
    
    public void imprimirArray( ArrayList<Fitxa> fitxes){
        Iterator itrFitxa = fitxes.iterator();
        Fitxa fitxa = (Fitxa) itrFitxa.next();
        
        while(itrFitxa.hasNext()){
            fitxa.toString();
            fitxa = (Fitxa) itrFitxa.next();
        }

    }
    
    public Boolean RepartirFitxes(){
        /**
         * Repartim les fitxes al client
         */
        int valorFitxesClient=0;
        boolean teUnaDobleClient=false;
        int valorFitxesServidor=0;
        boolean teUnaDobleServidor=false;
        
        //Repartim les fitxes al client
        for(int i=0; i<7; i++){
            System.out.println(reserva.get(i).toString());
            //System.out.println("Es doble? "+reserva.get(i).esDoble());
            maClient.add(reserva.get(i));//Posem la fitxa al client
            if(teUnaDobleClient==true){
                if(maClient.get(i).esDoble()==true){
                    if(valorFitxesClient<maClient.get(i).valor()){
                            valorFitxesClient=maClient.get(i).valor();
                            //System.out.println("Ja tenia una doble "+valorFitxesClient);
                        }
                }
            }else{
                if(maClient.get(i).esDoble()==true){
                    valorFitxesClient=maClient.get(i).valor();
                    //System.out.println("Primera doble "+valorFitxesClient);
                    teUnaDobleClient = true;
                }else{
                    if(valorFitxesClient<maClient.get(i).valor()){
                        valorFitxesClient=maClient.get(i).valor();
                        //System.out.println("No te cap doble "+valorFitxesClient);
                    }
                }
            }
            reserva.remove(i);
        }
        System.out.println("El valor de la fitxa del client es "+valorFitxesClient);
        
        
        /**
         * Repartim les fitxes al servidor
         */
        for(int i=0; i<7; i++){
            System.out.println(reserva.get(i).toString());
            //System.out.println("Es doble? "+reserva.get(i).esDoble());
            maServidor.add(reserva.get(i));//Posem la fitxa al client
            if(teUnaDobleServidor==true){
                if(maServidor.get(i).esDoble()==true){
                    if(valorFitxesServidor<maServidor.get(i).valor()){
                            valorFitxesServidor=maServidor.get(i).valor();
                            //System.out.println("Ja tenia una doble "+valorFitxesServidor);
                        }
                }
            }else{
                if(maServidor.get(i).esDoble()==true){
                    valorFitxesServidor=maServidor.get(i).valor();
                    //System.out.println("Primera doble "+valorFitxesServidor);
                    teUnaDobleServidor = true;
                }else{
                    if(valorFitxesServidor<maServidor.get(i).valor()){
                        valorFitxesServidor=maServidor.get(i).valor();
                        //System.out.println("No te cap doble "+valorFitxesServidor);
                    }
                }
            }
            reserva.remove(i);
        }
        //System.out.println("El valor de la fitxa del servidor es "+valorFitxesServidor);
        
        if(teUnaDobleClient||teUnaDobleServidor){//Si un dels dos te una doble
            if(teUnaDobleClient && teUnaDobleServidor){//Si la tenen els dos mirem qui te el valor mes alt
                if(valorFitxesClient>valorFitxesServidor){
                    return true;
                }else{
                    return false;
                }
            }else{//Si un dels dos te una doble
                if(teUnaDobleClient==true){//Si la te el client return true
                    return true;
                }else{//Si la te el servidor retornem false
                    return false;
                }
            }
         }else{//NO hi ha cap doble
            if(valorFitxesClient>valorFitxesServidor){
                return true;
            }else{
                return false;
            }
        }
    }

    

}
