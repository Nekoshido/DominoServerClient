/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clientp1;

import java.io.IOException;
import java.net.*;

/**
 *
 * @author hector
 */
public class Comunicacio{
    InetAddress maquinaServidora;
    Socket socket = null;
    ComUtils comUtils;
    private int numPartida;
    
    /*----------------CONECTAR--------------------------------------*/
    
    public void connectar(String ipRival, int portRival){
        try{
        /* Obtenim la IP de la maquina servidora */
        maquinaServidora = InetAddress.getByName(ipRival);
        /* Obrim una connexio amb el servidor */
        socket = new Socket(maquinaServidora, portRival);
        /* Obrim un flux d'entrada/sortida amb el servidor */
        comUtils = new ComUtils(socket);
        }catch(Exception ex){
            System.out.println("No s'ha pogut conectar"); 
        }
    }
    
    public void iniciPartida(String ipRival){
        int identificador=11;
        try{
            System.out.println("Enviem un inici de partida amb la ip "+ ipRival);
            comUtils.write_int32(identificador);
            String paraula = "HELO";
            comUtils.write_string(paraula.length(), paraula);
        }catch (Exception ex) {
            System.out.println("No s'ha pogut iniciar la partida");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
     /*----------------LLEGIR--------------------------------------*/
    
    public int llegirIdentificador(){
        try{
            int identificador = comUtils.read_int32();
            if(identificador==20||identificador==23||identificador==24||identificador==25||identificador==26||identificador==27){
                System.out.println("Hem rebut l'identificador "+identificador);
                return identificador;
            } else {
                String missatge = "S'ha rebut un identificador incorrecta!";
                String misServidor = "MISSATGE DEL SERVIDOR: "+missatge;
                System.out.println(missatge);
                enviarError(1, String.valueOf(misServidor.length()), misServidor);
                return 0010;
            }
        }catch(Exception ex){
                return 0010;
        }
    }

    public Fitxa llegirFitxa() {
        try{
            System.out.println("Llegir la fitxa");
            
            String num1 = comUtils.read_string(1);
            int num_1 = Integer.parseInt(num1);

            String num2 = comUtils.read_string(1);
            int num_2 = Integer.parseInt(num2);
            
            String orientacio = comUtils.read_string(1);
            String direccio = comUtils.read_string(1);
            
            char ori = orientacio.charAt(0);
            char dir = direccio.charAt(0);

            Fitxa fitxa = new Fitxa(num_1, num_2, ori, dir);
            System.out.println("La fitxa es: " + fitxa);
            return fitxa;
        }catch (Exception ex) {
            System.out.println("No s'ha pogut llegir la fitxa");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public int llegirMa() {
        try{
            //System.out.println("Llegir la ma"); 
            int ma = comUtils.read_int32();
            return ma;
        }catch (Exception ex) {
            System.out.println("No s'ha pogut llegir la fitxa");
            try {
                socket.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
            return 0010;
        }
    }
    
    public int llegirRobar() {
        try{
            //System.out.println("Llegir les fitxes que queden per robar");
            int robar = comUtils.read_int32();
            return robar;
        }catch (Exception ex) {
            System.out.println("No s'ha pogut llegir el robar");
            try {
                socket.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
            return 0010;
        }
    }
    
    public Fitxa llegirValorFitxa() {
        try{            
            String num1 = comUtils.read_string(1);
            int num_1 = Integer.parseInt(num1);
            String num2 = comUtils.read_string(1);
            int num_2 = Integer.parseInt(num2);
            System.out.println("Client :Llegeixo la fitxa "+num1+" | "+num2);       

            Fitxa fitxa = new Fitxa(num_1, num_2, 'N', ' ');
            return fitxa;
        }
        catch (IOException ex) {
            System.out.println("ID " + numPartida + ": No s'han pogut enviar les fitxes");
            try {
                    socket.close();
            } catch (IOException e) {
                    e.printStackTrace();
            }
        }
        return null;
    }
    

    public String llegirLesFitxesRobar() {
        try{
            String fitxes = comUtils.read_string_variable(2);
            //System.out.println("S hauran de llegir " + fitxes + " fitxes");
            return fitxes;
        }catch (Exception ex) {
            System.out.println("No s'ha pogut llegir el robar");
            try {
                socket.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public char llegirWinner() {
        try{
            String win = comUtils.read_string(1);
            char winner = win.charAt(0);
            return winner;
        } catch (Exception ex) {
            System.out.println("ID " + numPartida + ": ERROR al tancar");
            enviarAbandonarPartida();
        }
        return 'N';
    }

    int llegirPunts() {
        try{
            int punts = comUtils.read_int32();
            return punts;
        }catch(Exception ex){
            return 0010;
        }
    }
    
     public int llegirError(){
        try{
            System.out.println("ID " + numPartida + ": Llegint error");
            int idTipus = comUtils.read_int32();
            String longitud1 = comUtils.read_string();
            String longitud2 = comUtils.read_string();
            String longitud = longitud1 + longitud2;	
            int lon = Integer.parseInt(longitud);
            String missatge = comUtils.read_string_variable(lon);
            System.out.println("ID " + numPartida + ": ERROR: "+missatge);
            return idTipus;
        } catch (Exception ex) {
            System.out.println("ID " + numPartida + ": No s'ha pogut rebre la resposta");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0010;
        }
    }
    
    /*------------ENVIAR-----------------------------------------------*/
    
    public void enviarAbandonarPartida(){
        try{
            comUtils.write_int32(18);//Abandonar partida
            String paraula  = "bye";
            comUtils.write_string(paraula.length(), paraula);
            System.out.println("ID " + numPartida + ": Partida acabada");
            finalitzarPartida();
        } catch (Exception ex) {
            System.out.println("ID " + numPartida + ": ERROR al tancar");
            enviarAbandonarPartida();
        }
    }

    public void enviarError(int IDTipus, String textLength, String message){
        int identificador = 0010;
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

    void tirarFitxa(Fitxa fitxa, int ma) {
        int identifidacor = 14;
        try{
            comUtils.write_int32(identifidacor);//Tirar una fitxa
            comUtils.write_string(1, Integer.toString(fitxa.getNum1()));
            comUtils.write_string(1, Integer.toString(fitxa.getNum2()));
            comUtils.write_string(1, Character.toString(fitxa.getOrientacio()));
            comUtils.write_string(1, Character.toString(fitxa.getDireccio()));
            comUtils.write_int32(ma);
            System.out.println("S ha escrit la ma: "+ fitxa);
        } catch (Exception ex) {
            System.out.println("ID " + numPartida + " error al tirar la fitxa");
            enviarAbandonarPartida();
        }
    }

    void enviarNoTirada(int ma) {
        int identificador = 15;
        try{
            System.out.println("S envia identificador no tirada");
            comUtils.write_int32(identificador);//Tirar una fitxa
            comUtils.write_int32(ma);
            System.out.println("Sacaba denviar identificador no tirada");
        } catch (Exception ex) {
            System.out.println("ID " + numPartida + " error a l'enviar No Tirada");
            enviarAbandonarPartida();
        }
    }
    
    public void finalitzarPartida(){
        try{
            socket.close();
            System.exit(0);
        } catch (Exception ex) {
            System.out.println("No s'ha acabat la partida");
            finalitzarPartida();
        }
    }
}