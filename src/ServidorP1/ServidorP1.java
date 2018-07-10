/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ServidorP1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hector
 */
public class ServidorP1 {
    private static Scanner sc = new Scanner(System.in);
    
    public static void main(String[] args) {
        int numPartida = 0;
        
        if (args.length > 1){
            System.out.println("Us: java Servidor [<numPort>]");
            System.exit(1);
        }

        try {
            System.out.print("Introdueix el port: ");
            int portServidor = sc.nextInt();
            ServerSocket serverSocket = new ServerSocket(portServidor);
            System.out.println("Servidor socket preparat en el portServidor " + portServidor);

            while(true){

                Socket socket = new Socket();
                System.out.println("Esperant una connexió...");
                socket = serverSocket.accept();
                numPartida++;
                System.out.println("Un cliente s'ha conectat. ID:"+numPartida);

                Fil fil = new Fil(socket, numPartida);
                fil.start();

            }
		
	} catch (IOException ex) {
            System.out.println("IOException on socket listen: " + ex);
            ex.printStackTrace();
        }
    }
    

    
}
class Fil extends Thread{
	private Socket socket;
	private int numPartida;
	private static Partida partida ;
        private boolean noTirada;
        private boolean fiPartida = false;
        private Comunicacio comunicacio;
        
	public Fil(Socket sckt, int num){
		this.socket = sckt;
		this.numPartida = num;
	}
	
	public void run(){
            partida = new Partida();
            noTirada = false;
	    partida.Inicialitzar();
        try{
            comunicacio = new Comunicacio(socket, numPartida);
        }
        catch (Exception ex){
            System.out.println("Missatge d'Excepcio:"+ex.getMessage()+" - Excepcio:"+ex+" - Client ("+numPartida+")");
	}
        
        System.out.println("Partida NUM: " + numPartida);

        while(!fiPartida){
            try {
                
                int identificador = comunicacio.llegirIdentificador();
                System.out.println("------Llegim l'identificador--------- "+identificador);
                switch(identificador){
                    case 10: // ERROR
                        int idTipus = comunicacio.llegirError();
                        try {
                            corregirError(idTipus);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;

                    case 11: // "HELO"
                        tractar11();
                        break;

                    case 14: // TIRADA
                        tractar14();
                        break;

                    case 15:  //NO TIRADA
                        //LLEGIR NO TIRADA
                        tractar15();
                        break;
                    case 18: // ABANDONAR
                        tractar18();
                        break;
                    default:
                        break;

                }   
            } catch (IOException ex) {
                Logger.getLogger(Fil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Thread.currentThread().interrupt();
    }
        
/*-----------------------------ERROR-------------------------------------------*/

    public static void corregirError(int error) throws IOException{
        
    }
        
/*-----------------------------ROBAR CLIENT I SERVIDOR-------------------------------------------*/
         
             
    public void robarClient() throws IOException{
        comunicacio.enviarIdentificador(26);
        Fitxa fitxa = partida.getReserva().get(0);
        String fitxes = "";
        boolean podemEnviar=false;
        while(!podemEnviar){
            if(!comprovarFitxarTaulell(fitxa)){
                fitxa.setOrientacio('N');
                fitxa.setDireccio('N');
                partida.getMaClient().add(fitxa);
                //System.out.println("Afegim al string fitxes la fitxa "+fitxa.toString());
                fitxes = fitxes+ Integer.toString(fitxa.getNum1()) + Integer.toString(fitxa.getNum2());
                //System.out.println("Tamany reserva abans de borrar" +partida.getReserva().size());
                System.out.println(partida.getReserva().toString());
                //int posicioEliminar=partida.eliminarFitxaReserva(fitxa);
                partida.getReserva().remove(fitxa);
                //System.out.println("Tamany reserva despres de borrar" +partida.getReserva().size());
                System.out.println("Robar client - Reserva: " +partida.getReserva().toString());
                
            }else{
                fitxa.setOrientacio('N');
                fitxa.setDireccio('N');
                partida.getMaClient().add(fitxa);
                //System.out.println("Afegim al string fitxes la fitxa "+fitxa.toString());
                fitxes = fitxes+ Integer.toString(fitxa.getNum1()) + Integer.toString(fitxa.getNum2());
                //System.out.println("Tamany reserva abans de borrar" +partida.getReserva().size());
                //System.out.println("Reserva: " + partida.getReserva().toString());
                partida.getReserva().remove(fitxa);
                System.out.println("Reserva: " +partida.getReserva().toString());
                //System.out.println("Tamany reserva despres de borrar" +partida.getReserva().size());
                podemEnviar=true;
            }
            if(partida.getReserva().size()<=0){
                podemEnviar=true;
            }else{
                fitxa = partida.getReserva().get(0);
            }
        }
        //comunicacio.enviarNumFitxes(fitxes.size());

        comunicacio.enviarFitxes(fitxes);
        System.out.println("Enviem la paraula fitxes amb: "+fitxes);
        System.out.println("S'envia la ma " + partida.getMaServidor().size()+ " i la reserva " + partida.getReserva().size());
        comunicacio.enviarIdentificador(partida.getMaServidor().size());
        comunicacio.enviarIdentificador(partida.getReserva().size());
        
    }
    
    public Fitxa robarServidor(){
        if(partida.getReserva().size()>0){
            Fitxa fitxa = partida.getReserva().get(0);
            boolean podemEnviar=false;
            while(!podemEnviar){
                if(!comprovarFitxarTaulell(fitxa)){
                    System.out.println("No es pot afegir la fitxa");
                    fitxa.setOrientacio('N');
                    fitxa.setDireccio('N');
                    partida.getMaServidor().add(fitxa);
                    //System.out.println("Tamany reserva abans de borrar" +partida.getReserva().size());
                    //System.out.println(partida.getReserva().toString());
                    //int posicioEliminar=partida.eliminarFitxaReserva(fitxa);
                    partida.getReserva().remove(fitxa);
                    //System.out.println("Tamany reserva despres de borrar" +partida.getReserva().size());
                    System.out.println("Robar Server - Reserva: " +partida.getReserva().toString());   
                    System.out.println("Server despres de robar: ");
                    partida.toStringMaServidor();
                    System.out.println("Taulell: ");partida.toStringTaulell();
                }else{
                    fitxa.setOrientacio('N');
                    fitxa.setDireccio('N');
                    //partida.getMaServidor().add(fitxa);No l'afegim perque la tirarem
                    //System.out.println("Tamany reserva abans de borrar" +partida.getReserva().size());
                    System.out.println(partida.getReserva().toString());
                    //int posicioEliminar=partida.eliminarFitxaReserva(fitxa);
                    partida.getReserva().remove(fitxa);
                    //System.out.println("Tamany reserva despres de borrar" +partida.getReserva().size());
                    System.out.println("Robar Server - Reserva: " +partida.getReserva().toString());
                    System.out.println("Server despres de robar: ");
                    partida.toStringMaServidor();
                    System.out.println("Taulell: ");
                    partida.toStringTaulell();
                    podemEnviar=true;
                    return fitxa;
                }
                if(partida.getReserva().size()<=0){
                    podemEnviar=true;
                }else{
                    fitxa = partida.getReserva().get(0);
                }
            }
            return fitxa;
        }
        else{
            return null;
        }
    }
    

    /*-----------------------------COMPROVAR FITXA AL TAULELL-------------------------------------------*/
    
        
    private boolean comprovarFitxarTaulell(Fitxa fitxa) { 
        if(partida.getTaulell().isEmpty()){
            return true;
        }
        else if(partida.getTaulell().get(0).getNum1() == fitxa.getNum2()){//Si coincideix el numero
            //System.out.println(partida.getTaulell().get(0).getNum1() + "Coincideix amb "+ fitxa.getNum2() );
            return true;
        }
        else if(partida.getTaulell().get(0).getNum1() == fitxa.getNum1()){//Si coincideix el numero
            //System.out.println(partida.getTaulell().get(0).getNum1() + "Coincideix amb "+ fitxa.getNum1() );
            return true;
        }
        else if(partida.getTaulell().get(partida.getTaulell().size()-1).getNum2() == fitxa.getNum1()){//Si coincideix el numero
           // System.out.println(partida.getTaulell().get(partida.getTaulell().size()-1).getNum2() + "Coincideix amb "+ fitxa.getNum1() );
            return true;
        }
        else if(partida.getTaulell().get(partida.getTaulell().size()-1).getNum2() == fitxa.getNum2()){//Si coincideix el numero
            //System.out.println(partida.getTaulell().get(partida.getTaulell().size()-1).getNum2() + "Coincideix amb "+ fitxa.getNum1() );
            return true;
        }
        else{
           //System.out.println("No coincideix amb cap");
            return false;
        }
    }
    
    /*----------------------------TIRADES--------------------------------*/
    private void tiradaServer() {
        System.out.println("Reserva en tirada del Server: " + partida.getReserva().toString());
        System.out.println("La Ma del server es: ");
        partida.toStringMaServidor();
        System.out.println("Taulell: ");
        partida.toStringTaulell();
        Fitxa fitxa = tiradaServidor();
        //System.out.println("A tirada server la fitxa es:"+ fitxa);
        if (fitxa != null){
            System.out.println( "El Servidor tira: " + fitxa);
            if((partida.getMaServidor().size()>0) && (partida.getMaClient().isEmpty() )){
                finalitzarPartida();
            }
            else{
                comunicacio.enviarTirada(fitxa, partida.getMaServidor().size(), partida.getReserva().size());
                if( partida.getMaServidor().isEmpty()){ // si era la ultima fitxa
                    noTirada=true;
                }
                else{
                    noTirada=false;
                }
            }
        }
        else{
            fitxa = robarServidor();
            System.out.println( "El Servidor ha robat.");
            if(fitxa != null){
                if(partida.getMaClient().isEmpty() && partida.getMaServidor().isEmpty()){
                    System.out.println("S acaba partida");
                    finalitzarPartida();
                }
                else if(partida.getMaClient().isEmpty() && partida.getMaServidor().size()>1){
                    
                }
                else{
                    if(partida.getReserva().size()>0){
                        tirarFitxa(fitxa);
                        comunicacio.enviarTirada(fitxa, partida.getMaServidor().size(), partida.getReserva().size());
                        System.out.println( "El Servidor tira: " + fitxa);
                        
                        if(partida.getMaServidor().isEmpty()){
                            noTirada = true;
                        }else{
                            noTirada=false; 
                        }
                    }else{
                        comunicacio.enviarNoTirada(partida.getMaServidor().size(),partida.getReserva().size());
                    }
                }
            }
            else{
                if(noTirada == true){
                    finalitzarPartida();
                }
                else if(partida.getMaClient().isEmpty() && (partida.getMaServidor().size()>0)){
                    finalitzarPartida();
                }
                else{
                    comunicacio.enviarNoTirada(partida.getMaServidor().size(), partida.getReserva().size());
                    System.out.println( "El Servidor passa.");
                    noTirada = true;
                }
            }
        }
    }
    
    /*
    *
    *Triar quina es la fitxa que tirara el server
    */
    private Fitxa tiradaServidor(){
        Fitxa fitxa = null;
        Iterator itrFitxa = partida.getMaServidor().iterator();
        while(itrFitxa.hasNext()){
            fitxa = (Fitxa) itrFitxa.next();
            if(partida.getTaulell().isEmpty()){//Si esta buit el taulell tria la millor
                fitxa = partida.escollirmillorFitxa(partida.getMaServidor());
                partida.getTaulell().add(fitxa);
                partida.getMaServidor().remove(fitxa);
                
                return fitxa;
            }//Sino mira al taulell quina tirara
            else if(comprovarFitxarTaulell(fitxa)){
                System.out.println("tiradaServidor comprovar taulell");
                fitxa = tirarFitxa(fitxa);
                
                return fitxa;
            }
        }
        return null;
    }

    private Fitxa tirarFitxa(Fitxa fitxa) {
        if((partida.getTaulell().get(0).getNum1() == fitxa.getNum2())){
            System.out.println("Opcio 1" + fitxa);
            fitxa.setDireccio('E');
            fitxa.setOrientacio('N');
            partida.getMaServidor().remove(fitxa);
            partida.getTaulell().add(0, fitxa);
            return fitxa;
        }
        else if((partida.getTaulell().get(partida.getTaulell().size()-1).getNum2() == fitxa.getNum2())){
            System.out.println("Opcio 2" + fitxa);
            fitxa.setDireccio('D');
            fitxa.girar();
            partida.getMaServidor().remove(fitxa);
            partida.getTaulell().add(fitxa);
            return fitxa;
        }
        else if((partida.getTaulell().get(partida.getTaulell().size()-1).getNum2() == fitxa.getNum1())){
            System.out.println("Opcio 3" + fitxa);
            fitxa.setDireccio('D');
            fitxa.setOrientacio('N');
            partida.getMaServidor().remove(fitxa);
            partida.getTaulell().add(fitxa);
            return fitxa;
        }
        else if(partida.getTaulell().get(0).getNum1() == fitxa.getNum1()){
            System.out.println("Opcio 4" + fitxa);
            fitxa.setDireccio('E');
            fitxa.girar();
            partida.getMaServidor().remove(fitxa);
            partida.getTaulell().add(0, fitxa);
            return fitxa; 
        }
        else{
            return null;
        }
    }
    
    /*----------------------------SWITCH----------------------------------------*/

    private void tractar11() {
        comunicacio.aceptarPartida();
        boolean comensa = partida.RepartirFitxes();
        
        comunicacio.enviar7xFitxa(partida.getMaClient());
        System.out.println("Ma del servidor:");
        partida.toStringMaServidor();
        if(comensa){
            int ma = partida.getMaServidor().size();
            int robar = partida.getReserva().size();
            comunicacio.enviarNoTirada(ma,robar);
        }
        else{
            tiradaServer();
            noTirada=false;
        }
    }

    private void tractar14() {
        Fitxa fitxa = comunicacio.llegirFitxa();
        int ma = comunicacio.llegirMa();
        boolean perAfegir = comprovarFitxarTaulell(fitxa);
        if(perAfegir==false){
            if((noTirada == true) && (partida.getMaClient().size()>1)){
                finalitzarPartida();
            }
            else{
                System.out.println("Servidor:");
                partida.toStringMaServidor();
                System.out.println("Taulell: " + partida.getTaulell().toString());
                System.out.println("Ma client: " + partida.getMaClient().toString());
                System.out.println("Reserva: " +partida.getReserva().toString());
                System.out.println("Corregim l'error");
                try {
                    corregirError(0);
                } catch (IOException ex) {
                    Logger.getLogger(Fil.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
        }else{
            afegirFitxaTaulellClient(fitxa);
            if(/*partida.getMaClient().isEmpty() &&*/ (noTirada==true)){
                finalitzarPartida();
                comunicacio.finalitzarPartida();
            }
            else{
                tiradaServer();
                noTirada=false;
            }
        }
    }

    
    /*-------------------AFEGIR FITXES AL TAULELL-----------------------*/
    
    private static boolean afegirFitxaTaulellClient(Fitxa fitxa) {
        //Mirem si la vol girar
        //System.out.println("Aqui peta en afegir fitxa "+ fitxa.getOrientacio());
        /*if(fitxa.getOrientacio() == 'G'){
            int num1 = fitxa.getNum1();
            int num2 = fitxa.getNum2();
            fitxa.setNum1(num2);
            fitxa.setNum2(num1);
        }*/
        
        if(partida.getTaulell().isEmpty()){
            partida.afegirFitxaTaulellDreta(fitxa);
            int posicioEliminar=partida.eliminarFitxaMa(fitxa);
            partida.getMaClient().remove(posicioEliminar);
            //boolean comp = partida.getMaClient().remove(0);
            //System.out.println("El resultat de eliminar la fitxa es"+ comp);
            return true;
        }
        //Mirem a quin canto la vol posar
        //Si la vol posar al dret
        else if(fitxa.getDireccio()=='E'){
           System.out.println("Poso la fitxa a la esquerra del taulell");
           if(partida.getTaulell().get(0).getNum1() == fitxa.getNum2()){//Si coincideix el numero
               partida.afegirFitxaTaulellEsquerra(fitxa);
               
               int posicioEliminar=partida.eliminarFitxaMa(fitxa);
               partida.getMaClient().remove(posicioEliminar);
            
               //boolean comp = partida.getMaClient().remove(fitxa);
               //System.out.println("El resultat de eliminar la fitxa es"+ comp);
               return true;
           }
        }
        //Si la vol posar al esquerre
        else if(fitxa.getDireccio()=='D'){
            System.out.println("Poso la fitxa a la dreta del taulell");
            if(partida.getTaulell().get(partida.getTaulell().size()-1).getNum2() == fitxa.getNum1()){//Si coincideix el numero
                partida.afegirFitxaTaulellDreta(fitxa);
                
                int posicioEliminar=partida.eliminarFitxaMa(fitxa);
                partida.getMaClient().remove(posicioEliminar);
                //boolean comp = partida.getMaClient().remove(fitxa);
                //System.out.println("El resultat de eliminar la fitxa es"+ comp);
                return true;
            }
        }
        
        // Sino mirem de posar-la nosaltres
        if(partida.getTaulell().get(0).getNum1() == fitxa.getNum2()){//Si coincideix el numero
            partida.afegirFitxaTaulellEsquerra(fitxa);
            
            int posicioEliminar=partida.eliminarFitxaMa(fitxa);
            partida.getMaClient().remove(posicioEliminar);
            //boolean comp = partida.getMaClient().remove(fitxa);
            //System.out.println("El resultat de eliminar la fitxa es"+ comp);
            return true;
        }
        if(partida.getTaulell().get(0).getNum1() == fitxa.getNum1()){//Si coincideix el numero
            fitxa.girar();
            partida.afegirFitxaTaulellEsquerra(fitxa);
            
            int posicioEliminar=partida.eliminarFitxaMa(fitxa);
            partida.getMaClient().remove(posicioEliminar);
            //boolean comp = partida.getMaClient().remove(fitxa);
            //System.out.println("El resultat de eliminar la fitxa es"+ comp);
            return true;
        }
        if(partida.getTaulell().get(partida.getTaulell().size()-1).getNum2() == fitxa.getNum1()){//Si coincideix el numero
            partida.afegirFitxaTaulellDreta(fitxa);
            
            int posicioEliminar=partida.eliminarFitxaMa(fitxa);
            partida.getMaClient().remove(posicioEliminar);
            //boolean comp = partida.getMaClient().remove(fitxa);
            //System.out.println("El resultat de eliminar la fitxa es"+ comp);
            return true;
        }
        if(partida.getTaulell().get(partida.getTaulell().size()-1).getNum2() == fitxa.getNum2()){//Si coincideix el numero
            fitxa.girar();
            partida.afegirFitxaTaulellDreta(fitxa);
            
            int posicioEliminar=partida.eliminarFitxaMa(fitxa);
            partida.getMaClient().remove(posicioEliminar);
            //boolean comp = partida.getMaClient().remove(fitxa);
            //System.out.println("El resultat de eliminar la fitxa es"+ comp);
            return true;
        }
        /*----------------------------------------*/
        return false;
    }

    private void tractar15() throws IOException {
    //EXECUTAR ROBAR DEL CLIENT
    int ma = comunicacio.llegirMa();
    if (partida.getMaClient().size() != ma){
        System.out.println("Ma rebuda: "+ma+" ma que teniem: "+partida.getMaClient().size());
        System.out.println("Ma client: " + partida.getMaClient().toString());
        System.out.println("Reserva: " +partida.getReserva().toString());
        String frase = "El tamany de la ma no és el mateix que al client";
        //System.out.println("Peta aqui");
        comunicacio.enviarError( 10, Integer.toString(frase.length()), frase);
                System.out.println("Peta aqui2");
    }else{
        if(noTirada==true){
            System.out.println("Entro a tractar 15 per finalitzar partida");
            finalitzarPartida();
            comunicacio.finalitzarPartida();
        }else{
            System.out.println("Entro per mira si li dono fitxes o tiro");
            if(partida.getReserva().size()>0){//Si encara queden fitxes a la reserva el fem robar
                System.out.println("Estem a tracta 15 i farem robar el client");
                robarClient();
            }else{
                System.out.println("Estem a tractar 15 i farem una tirada(no el farem robar)");
                tiradaServer();
            }
        }
    }

    // Tirar nosaltres}
    }
    
    private void finalitzarPartida() {
        System.out.println("S'acaba la partida!!!!");
        String win;
        int puntsClient =0;
        for (int i = 0; i < partida.getMaClient().size(); i++) {
            puntsClient += partida.getMaClient().get(i).getValor();
        }

        int puntsServer =0;
        for (int i = 0; i < partida.getMaServidor().size(); i++) {
            puntsServer += partida.getMaServidor().get(i).getValor();
        }
        if(puntsClient<puntsServer){
            win = "C";
        }else if(puntsClient>puntsServer){
            win = "S";
        }else{
            win = "E";
        }
        System.out.println("Punts client: " + puntsClient);
        System.out.println("Punts Server: " + puntsServer);
        comunicacio.enviarFinalJoc(puntsClient,puntsServer, win);
        fiPartida=true;
    }

    private void tractar18() {
        System.out.println("El client vol abandonar");
        comunicacio.llegirFinal();
        fiPartida= true;
    }
    
}

