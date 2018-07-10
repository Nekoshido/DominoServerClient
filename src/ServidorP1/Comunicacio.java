/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ServidorP1;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

/**
 *
 * @author hector
 */
public class Comunicacio{
    
    private final int numPartida;

    Socket socket;
    private ComUtils comUtils;
    
    /*----------------CONECTAR--------------------------------------*/
    
    public Comunicacio(Socket s, int i) throws IOException {
        socket = s;
        numPartida = i;
        try {
            comUtils = new ComUtils(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /*----------------LLEGIR--------------------------------------*/
    
    public int llegirIdentificador(){
        try{
            int identificador = comUtils.read_int32();
            System.out.println("Primer identificador "+identificador);
            if(identificador==10||identificador==11||identificador==14||identificador==15||identificador==18){
                System.out.println("Hem rebut l'identificador "+identificador);
                return identificador;
            } else {
                    String missatge = "S'ha rebut un identificador incorrecte!";
                    String misServidor = "MISSATGE DEL SERVIDOR: "+missatge;
                    System.out.println(missatge);
                    enviarError(1, String.valueOf(misServidor.length()), misServidor);
                    return 0020;
            }
        }catch(Exception ex){
                return 0020;
        }
    }
    
    
    
    public Fitxa llegirFitxa() {
        try{
            System.out.println("Llegir la fitxa");
            
            String num1 = comUtils.read_string(1);
            //System.out.println("---------Aqui es on peta "+ num1);
            int num_1 = Integer.parseInt(num1);

            String num2 = comUtils.read_string(1);
            //System.out.println("---------Aqui es on peta "+ num2);
            int num_2 = Integer.parseInt(num2);
            
            
            String orientacio = comUtils.read_string(1);
            String direccio = comUtils.read_string(1);
            //System.out.println("---------Aqui es on peta "+ orientacio+ " aixo " + direccio);
            
            char ori = orientacio.charAt(0);
            char dir = direccio.charAt(0);
            
            Fitxa fitxa = new Fitxa(num_1, num_2, ori, dir);
            System.out.println("S'ha rebut la fitxa" + fitxa);
            return fitxa;
        }catch (Exception ex) {
            System.out.println("No s'ha pogut llegir la fitxa");
            finalitzarPartida();
            return null;
        }
    }

    public void aceptarPartida(){
        System.out.println("ID " + numPartida + ": Partida acceptada");
        try {
            String missatge = comUtils.read_string(4);
            System.out.println("ID " + numPartida + ": Missatge: "+missatge);
        } catch (IOException ex) {
            System.out.println("ID " + numPartida + ": No s'ha pogut rebre el missatgea");
        }
    }
    
    public void finalitzarPartida(){
        try{
            System.out.println("ID " + numPartida + ": Partida acabada");
            socket.close();
            Thread.currentThread().interrupt();
        } catch (Exception ex) {
            System.out.println("ID " + numPartida + ": ERROR al tancar");
            finalitzarPartida();
        }
    }
        
    public int llegirError(){
        try{
            System.out.println("ID " + numPartida + ": Llegint error");
            int idTipus = comUtils.read_int32();
            String longitud1 = comUtils.read_string_variable(1);
            String longitud2 = comUtils.read_string_variable(1);
            String longitud = longitud1 + longitud2;	
            int lon = Integer.parseInt(longitud);
            String missatge = comUtils.read_string_variable(lon);
            System.out.println("ID " + numPartida + ": ERROR: "+missatge);
            return idTipus;
        } catch (Exception ex) {
            System.out.println("ID " + numPartida + ": No s'ha pogut rebre la resposta");
            finalitzarPartida();
            return 0020;
        }
    }

    public int llegirMa() {
        try{
            //System.out.println("Llegir la ma"); 
            int ma = comUtils.read_int32();
            return ma;
        }catch (Exception ex) {
            System.out.println("No s'ha pogut llegir la ma");
            finalitzarPartida();
            return 0020;
        }
    }
    public void llegirFinal() {
        try{
            //System.out.println("Llegir la ma"); 
            //String ma = comUtils.read_string(3);
            //System.out.println(ma);
            String adeu = comUtils.read_string_variable(3);
            System.out.println("El client ens ha dit"+adeu);
            finalitzarPartida();
        }catch (Exception ex) {
            System.out.println("No s'ha pogut llegir la ma");
        }
    
    }
    /*------------ENVIAR-----------------------------------------------*/
    
    public void enviarIdentificador(int id){
        try{
            comUtils.write_int32(id);
        }
        catch(IOException ex) {
            System.out.println("ID " + numPartida + ": ERROR a l'enviar identificador");
            ex.printStackTrace();
        }
    }


    public void enviarError(int IDTipus, String textLength, String message){
        int identificador = 0020;
        try{
                comUtils.write_int32(identificador);
                comUtils.write_int32(IDTipus);
                comUtils.write_string(textLength.substring(0, 1));
                comUtils.write_string(textLength.substring(1, 2));
                int longitudInt = Integer.parseInt(textLength);
                comUtils.write_string_variable(longitudInt, message);

        } catch (Exception ex) {
                System.out.println("ID " + numPartida + ": ERROR a l'enviar");
                enviarError(IDTipus, textLength, message);
        }
    }
    
    public void enviar7xFitxa(ArrayList<Fitxa> ma){
        int identificador = 23;
        try{
            System.out.println("envio el identificador"+identificador);
            comUtils.write_int32(identificador);
            for(int i = 0; i< ma.size(); i++){
                String num1 = Integer.toString(ma.get(i).getNum1());
                String num2 = Integer.toString(ma.get(i).getNum2());

                comUtils.write_string(1, num1);//Enviem el primer numero
                comUtils.write_string(1, num2);//Enviem el segon numero

                System.out.println("Server: Dono la fitxa "+ num1+" | "+num2);
            }
        }
        catch (IOException ex) {
            System.out.println("ID " + numPartida + ": No s'han pogut enviar les fitxes");
            finalitzarPartida();
        }

    }

    public void enviarFitxes(String paraula) throws IOException{
        try{
            
            System.out.println("La fitxes robades son "+ paraula);
            
            comUtils.write_string_variable(2, paraula);//Enviem el primer numero
        }
        catch (IOException ex) {
            System.out.println("ID " + numPartida + ": No s'ha pogut enviar la fitxa");
        }
    }
    

    void enviarNoTirada(int ma, int robar) {
        int identificador = 25;
        try{
            comUtils.write_int32(identificador);//Tirar una fitxa
            comUtils.write_int32(ma);
            comUtils.write_int32(robar);
        } catch (Exception ex) {
            System.out.println("ID " + numPartida + " error");
            finalitzarPartida();
        }
    }

    void enviarTirada(Fitxa fitxa, int ma, int robar) {
        int identificador = 24;
        try{
            comUtils.write_int32(identificador);//Tirar una fitxa
            //System.out.println("->Enviem "+Integer.toString(fitxa.getNum1()));
            comUtils.write_string(1, Integer.toString(fitxa.getNum1()));
            
            //System.out.println("->Enviem "+Integer.toString(fitxa.getNum2()));
            comUtils.write_string(1, Integer.toString(fitxa.getNum2()));
            
            //System.out.println("->Enviem "+Character.toString(fitxa.getOrientacio()));
            comUtils.write_string(1, Character.toString(fitxa.getOrientacio()));
            
            //System.out.println("-> Enviem "+Character.toString(fitxa.getDireccio()));
            comUtils.write_string(1, Character.toString(fitxa.getDireccio()));
            comUtils.write_int32(ma);
            comUtils.write_int32(robar);
            System.out.println("S'ha acabat d'enviar la tirada del Servidor");
        }
        catch (Exception ex) {
            System.out.println("ID " + numPartida + " error");
            ex.printStackTrace();
        }
    }

    
    void enviarFinalJoc(int puntsClient, int puntsServer, String win) {
        int identificador = 27;
        try{
            comUtils.write_int32(identificador);//Tirar una fitxa
            comUtils.write_string(1, win);
            comUtils.write_int32(puntsClient);
            comUtils.write_int32(puntsServer);
            finalitzarPartida();
        }
        catch (Exception ex) {
            System.out.println("ID " + numPartida + " error");
            ex.printStackTrace();
        }
    }


}
