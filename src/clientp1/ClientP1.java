/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clientp1;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 *
 * @author joangasullroyes
 */
public class ClientP1 {
    //Communicació
    private static Comunicacio comunicacio = new Comunicacio();
    private static Scanner sc= new Scanner(System.in);//Scanner
    //Conte totes les fitxes distribuides entre client, servidor i reserva.
    private static Partida partida = new Partida();
    
    private static int port;
    private static String ip;
    private static int identificador;
    private static int ma;
    private static int robar;
    private static int opcio;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        port=0;
        ip="";
        demanarIPsConect();//Realitza la connexio
        comunicacio.iniciPartida(ip);
        run();
    }

    private static void demanarIPsConect() {
        System.out.println("Indica la IP: ");
        ip = sc.next();
        System.out.println("Indica el port del server: ");
        port = sc.nextInt();
        comunicacio.connectar(ip, port);
    }
    
    public static void run(){
        while(true){
            identificador = comunicacio.llegirIdentificador();
            //System.out.println("Hem rebut l'identificador "+identificador);
            switch(identificador){
                case 20:
                    tractar20();
                    break;
                case 23://7xFitxa
                    //System.out.println("Client: Entro a llegir les 7 fitxes");
                    llegir7xFitxa();
                    break;
                    
                case 24://Ha tirat una fitxa
                    tractar24();
                    break;
                    
                case 25://No tirada
                    tractar25();
                    break;
                    
                case 26://Tracta les fitxes robades quan no ha pogut tirar
                    tractar26();
                    break;
                case 27://Finalitzar partida
                    System.out.println("S acaba la partida");
                    tractar27();

                    break;
            }
            
        }
    }
    
    /*-------------------AFEGIR FITXES AL TAULELL-----------------------*/
    
    private static boolean afegirFitxaTaulellClient(Fitxa fitxa) {
        if(partida.getTaulell().isEmpty()){
            partida.afegirFitxaTaulellDreta(fitxa);
            partida.getMaClient().remove(fitxa);
            return true;
        }
        //Mirem si la vol girar
        /*if(fitxa.getOrientacio()== 'G'){
            int num1 = fitxa.getNum1();
            int num2 = fitxa.getNum2();
            fitxa.setNum1(num2);
            fitxa.setNum2(num1);
        }*/
        //Mirem a quin canto la vol posar
        //Si la vol posar al dret
        if(fitxa.getDireccio()=='E'){
           if(partida.getTaulell().get(0).getNum1() == fitxa.getNum2()){//Si coincideix el numero
               partida.afegirFitxaTaulellEsquerra(fitxa);
               partida.getMaClient().remove(fitxa);
               return true;
           }
           if(partida.getTaulell().get(0).getNum1() == fitxa.getNum1()){//Si coincideix el numero
               fitxa.girar();
               partida.afegirFitxaTaulellEsquerra(fitxa);
               partida.getMaClient().remove(fitxa);
               return true;
           }
        }
        //Si la vol posar al esquerre
        if(fitxa.getDireccio()=='D'){

            if(partida.getTaulell().get(partida.getTaulell().size()-1).getNum2() == fitxa.getNum1()){//Si coincideix el numero
                partida.afegirFitxaTaulellDreta(fitxa);
                partida.getMaClient().remove(fitxa);
                return true;
            }
            if(partida.getTaulell().get(partida.getTaulell().size()-1).getNum2() == fitxa.getNum1()){//Si coincideix el numero
                fitxa.girar();
                partida.afegirFitxaTaulellDreta(fitxa);
                partida.getMaClient().remove(fitxa);
                return true;
            }
        }
        
        // Sino mirem de posar-la nosaltres
        if(partida.getTaulell().get(0).getNum1() == fitxa.getNum2()){//Si coincideix el numero
            partida.afegirFitxaTaulellEsquerra(fitxa);
            fitxa.setDireccio('E');
            partida.getMaClient().remove(fitxa);
            return true;
        }
        else if(partida.getTaulell().get(0).getNum1() == fitxa.getNum1()){//Si coincideix el numero
            fitxa.girar();
            fitxa.setDireccio('E');
            fitxa.setOrientacio('G');
            partida.afegirFitxaTaulellEsquerra(fitxa);
            partida.getMaClient().remove(fitxa);
            return true;
        }
        
        else if(partida.getTaulell().get(partida.getTaulell().size()-1).getNum2() == fitxa.getNum1()){//Si coincideix el numero
            partida.afegirFitxaTaulellDreta(fitxa);
            fitxa.setDireccio('D');
            partida.getMaClient().remove(fitxa);
            return true;
        }
        else if(partida.getTaulell().get(partida.getTaulell().size()-1).getNum2() == fitxa.getNum2()){//Si coincideix el numero
            fitxa.girar();
            fitxa.setDireccio('D');
            fitxa.setOrientacio('G');
            partida.afegirFitxaTaulellDreta(fitxa);
            partida.getMaClient().remove(fitxa);
            return true;
        }
        /*----------------------------------------*/
        return false;}

    private static boolean afegirFitxaTaulellServer(Fitxa fitxa){
        //Mirem si la vol girar
        if(fitxa.getOrientacio() == 'G'){
            int num1 = fitxa.getNum1();
            int num2 = fitxa.getNum2();
            fitxa.setNum1(num2);
            fitxa.setNum2(num1);
        }
        
        if(partida.getTaulell().isEmpty()){
            partida.afegirFitxaTaulellDreta(fitxa);
            return true;
        }
        
        //Mirem a quin canto la vol posar
        //Si la vol posar al dret
        if(fitxa.getDireccio()=='E'){
           System.out.println("Poso la fitxa a la esquerra del taulell");
           if(partida.getTaulell().get(0).getNum1() == fitxa.getNum2()){//Si coincideix el numero
               partida.afegirFitxaTaulellEsquerra(fitxa);
               return true;
           }
           if(partida.getTaulell().get(0).getNum1() == fitxa.getNum1()){//Si coincideix el numero
               System.out.println("Hem girat la fitxa");
               fitxa.girar();
               partida.afegirFitxaTaulellEsquerra(fitxa);
               return true;
           }
        }
        //Si la vol posar al esquerre
        if(fitxa.getDireccio()=='D'){
            System.out.println("Poso la fitxa a la dreta del taulell");
            if(partida.getTaulell().get(partida.getTaulell().size()-1).getNum2() == fitxa.getNum1()){//Si coincideix el numero
                partida.afegirFitxaTaulellDreta(fitxa);
                return true;
            }
            if(partida.getTaulell().get(partida.getTaulell().size()-1).getNum2() == fitxa.getNum2()){//Si coincideix el numero
                fitxa.girar();
                
                partida.afegirFitxaTaulellDreta(fitxa);
                return true;
            }
        }
        
        // Sino mirem de posar-la nosaltres
        /*----------Afegit per treure--------------*/
        if(partida.getTaulell().get(0).getNum1() == fitxa.getNum2()){//Si coincideix el numero
            partida.afegirFitxaTaulellEsquerra(fitxa);
            return true;
        }
        if(partida.getTaulell().get(0).getNum1() == fitxa.getNum1()){//Si coincideix el numero
            fitxa.girar();
            partida.afegirFitxaTaulellEsquerra(fitxa);
            return true;
        }
        if(partida.getTaulell().get(partida.getTaulell().size()-1).getNum2() == fitxa.getNum1()){//Si coincideix el numero
            partida.afegirFitxaTaulellDreta(fitxa);
            return true;
        }
        if(partida.getTaulell().get(partida.getTaulell().size()-1).getNum2() == fitxa.getNum2()){//Si coincideix el numero
            fitxa.girar();
            partida.afegirFitxaTaulellDreta(fitxa);
            return true;
        }
        /*----------------------------------------*/
        return false;
    }
    
    /*-------------------MENUS-----------------------*/

    private static int queVolfer(int ma,int robar) {
        System.out.println("Al server li queden " + ma + " fitxes\n");
        System.out.println("A la reserva hi ha "+ robar + " fitxes\n");
        System.out.println("------Taulell----------\n");
        partida.toStringTaulell();//Pintem el taulell
        System.out.println("-----------------------\n");
        partida.toStringMaClient();//Ensenyem les fitxes
        System.out.println("Que vols fer?\n");
        System.out.println("( 1 ) -> Tirar\n");
        System.out.println("( 2 ) -> Robar fitxar/No tirar\n");
        System.out.println("( 3 ) -> Abandonar\n");
        return sc.nextInt();
    }

    private static void ferAccio(int opcio) {
        System.out.println("Entra a ferAccio amb opcio " + opcio);
        if(opcio == 1){//Vol fer una tirada
               int pos = 99;
               if(partida.getMaClient().isEmpty()){
                   System.out.println("La mà del client és buida");
               }
               else{
                while(pos> partida.getMaClient().size() ){
                     System.out.println("Quina fitxa vols tirar");
                     System.out.println("Numero de la ma del client "+partida.getMaClient().size());
                     partida.toStringMaClient();//Ensenyem les fitxes
                     pos = sc.nextInt();
                 }
                pos=pos-1;

                Fitxa fitxa = partida.getMaClient().get(pos);

                boolean esApta = comprovarFitxarTaulell(fitxa);//Boolea per saber si la fitxa cuadra amb el cartell
                //System.out.println("Rebo el bolea "+esApta);
                if(esApta == true){
                    fitxa.setDireccio('N');
                    fitxa.setOrientacio('N');
                    afegirFitxaTaulellClient(fitxa);
                    comunicacio.tirarFitxa(fitxa,partida.getMaClient().size());
                    

                }else{
                    System.out.println("La fitxa triada no es pot tirar");
                    if(comprovarMaTaulell(partida.getMaClient())){
                         ferAccio(1);
                    }
                    else{
                        opcio = queVolfer(ma,robar);
                        //System.out.println("aquesta es la opcio escollida "+opcio);
                        if(opcio>0&&opcio<4){
                            //System.out.println("Entra a ferAccio");
                            ferAccio(opcio);
                        }else{
                            while((opcio>0&&opcio<4)){
                                System.out.println("A introduit un numero erroni");
                                opcio = queVolfer(ma, robar);
                            }
                            ferAccio(opcio);
                        }
                    }

                }
            }

           }/*Aquí entra dos cops sense passar per ferAccio i encara m'estic preguntant com es possible */
        else if(opcio == 2){//Vol fer una notirada
            System.out.println("Entra a no tirada");
            if(!comprovarMaTaulell(partida.getMaClient())){
                System.out.println("Envia noTirada");
                comunicacio.enviarNoTirada(partida.getMaClient().size());
                //opcio = 0;
            }
            else{ 
                System.out.println("Pots tirar, no cal robar");  
                opcio = queVolfer(ma,robar);
                //System.out.println("aquesta es la opcio escollida "+opcio);
                if(opcio>0&&opcio<4){
                    //System.out.println("Entra a ferAccio");
                    ferAccio(opcio);
                }else{
                    while((opcio>0&&opcio<4)){
                        System.out.println("A introduit un numero erroni");
                        opcio = queVolfer(ma, robar);
                    }
                    ferAccio(opcio);
                }
            }
        }
        else if(opcio == 3){//vol abandonar
            comunicacio.enviarAbandonarPartida();//
        }
        else{
            while((opcio>0&&opcio<4)){
                System.out.println("A introduit un numero erroni");
                opcio = queVolfer(ma, robar);
            }
            ferAccio(opcio);
        }
        System.out.println("Sacaba fer Accio");
    }
    
    /*-------------------ERROR----------------------*/
    
    private static void enviarError(int IDTipus, String textLength, String message) {
        comunicacio.enviarError(IDTipus, textLength, message);
        
    }
    
    /*-------------------COMPROVAR FITXA TAULELL----------------------*/
    private static boolean comprovarMaTaulell(ArrayList<Fitxa> ma){
        Fitxa fitxa = null;
        Iterator itrFitxa = ma.iterator();
        while(itrFitxa.hasNext()){
            fitxa = (Fitxa) itrFitxa.next();
            if(comprovarFitxarTaulell(fitxa)){
                return true;
            }
        }
        return false;
        
    }
    
    
    private static boolean comprovarFitxarTaulell(Fitxa fitxa) {
        if(partida.getTaulell().isEmpty()){
            return true;
        }
        else if(partida.getTaulell().get(0).getNum1() == fitxa.getNum2()){//Si coincideix el numero
            return true;
        }
        else if(partida.getTaulell().get(0).getNum1() == fitxa.getNum1()){//Si coincideix el numero
            return true;
        }
        else if(partida.getTaulell().get(partida.getTaulell().size()-1).getNum2() == fitxa.getNum1()){//Si coincideix el numero
            return true;
        }
        else if(partida.getTaulell().get(partida.getTaulell().size()-1).getNum2() == fitxa.getNum2()){//Si coincideix el numero
            return true;
        }
        else{
            return false;
        }
    }
    
    /*-------------------AGAFAR LES FITXES-----------------------*/

    private static void llegir7xFitxa() {
        for(int i = 0; i<7; i++){
            Fitxa fitxa = comunicacio.llegirValorFitxa();
            partida.getMaClient().add(fitxa);//Afegim les 7 fitxes
        }
        
    }
    
    
    /*-------------------SWITCH-----------------------*/

    private static void tractar24() {
        Fitxa fitxa = comunicacio.llegirFitxa();
        String frase;
        System.out.println( "El client ha rebut: " + fitxa);
        ma = comunicacio.llegirMa();//Numero de fitxes que li queden al Server
        robar = comunicacio.llegirRobar();//Numero ed fitxes que queden per robar
        boolean afegida = afegirFitxaTaulellServer(fitxa);//Afegim la fitxa al taulell
        if(afegida==false){
            frase = "No hem pogut afegir la fitxa del server al taulell";
            System.out.println(frase);
            enviarError( 10, Integer.toString(frase.length()), frase);
        }else{
            opcio = queVolfer(ma,robar);
            //System.out.println("aquesta es la opcio escollida "+opcio);
            if(opcio>0&&opcio<4){
                //System.out.println("Entra a ferAccio");
                ferAccio(opcio);
            }else{
                while((opcio>0&&opcio<4)){
                    System.out.println("A introduit un numero erroni");
                    opcio = queVolfer(ma, robar);
                }
                ferAccio(opcio);
            }

        }
    }

    private static void tractar25() {
        ma = comunicacio.llegirMa();
        robar = comunicacio.llegirRobar();
        opcio = queVolfer(ma, robar);
        //System.out.println("aquesta es la opcio escollida "+opcio);
        if(opcio>0&&opcio<4){
            //System.out.println("Entra a ferAccio");
            ferAccio(opcio);
        }else{
            System.out.println("A introduit un numero erroni");
            queVolfer(ma, robar);
        }
    }

    private static void tractar26() {
        String paraula = comunicacio.llegirLesFitxesRobar();
        System.out.println("La paraula de fitxes rebudes es"+ paraula);
        //Si hi ha alguna fitxa a afegir a la mà
        Fitxa fitxa;
        if(paraula != null){
            //System.out.println("Entro a mirar les fitxes que tinc que robar");
            int i = 0;
            int num1;
            int num2;
            char caracter; 
            //System.out.println("Tira de numeros" + paraula);
            while (i<paraula.length()){
                //System.out.println("La paraula es"  + paraula.charAt(i));
                caracter = paraula.charAt(i);
                num1 = Character.getNumericValue(caracter);
                //System.out.println("El primer numero es"+num1);
                i = i + 1;
                caracter = paraula.charAt(i);
                num2 = Character.getNumericValue(caracter);
                //System.out.println("El segundo numero es"+num2);
                //Fitxa fitxa = comunicacio.llegirValorFitxa();
                fitxa = new Fitxa(num1, num2, 'N','N');
                //System.out.println("Afegeixo la fitxa "+ fitxa.toString());
                partida.getMaClient().add(fitxa);//L'afegim a la mà
                i = i + 1;
            }
        
        //Llegim la ultima fitxa i la tirem
        //Fitxa fitxa = comunicacio.llegirValorFitxa();
        
        ma = comunicacio.llegirMa();
        robar = comunicacio.llegirRobar();
        
        //fitxa = partida.getMaClient().get(partida.getMaClient().size()-1);//Agafo la ultima fitxa
        //Joan sisplau modifica aixo si ho toques abans que jo
        //afegirFitxaTaulellClient(fitxa);
        
        //comunicacio.tirarFitxa(fitxa, partida.getMaClient().size()); 
        opcio = queVolfer(ma,robar);
        //System.out.println("aquesta es la opcio escollida "+opcio);
        if(opcio>0&&opcio<4){
            //System.out.println("Entra a ferAccio");
            ferAccio(opcio);
        }else{
            while((opcio>0&&opcio<4)){
                System.out.println("A introduit un numero erroni");
                opcio = queVolfer(ma, robar);
            }
            ferAccio(opcio);
        }
        }
        else{
            String frase = "Error al tractar el 26: numero de fitxes incorrecte";
            enviarError( 10, Integer.toString(frase.length()), frase);
        }
        
    }

    private static void tractar27() {
        System.out.println("S'ha acabat la partida!!!");
        char winner =comunicacio.llegirWinner();
        int puntsClient = comunicacio.llegirPunts();
        int puntsServer = comunicacio.llegirPunts();
        String frase;
        if(winner== 'N'){
            frase = "Winner = N";
            enviarError( 10, Integer.toString(frase.length()), frase);
        }else if(winner== 'E' | winner== 'e'){
            System.out.println("Empat" + puntsClient + " - " + puntsServer);
        }else if(winner== 'S' | winner== 's'){
            System.out.println("Guanyar el servidor!!! S:" + puntsServer + "  C:" + puntsClient );
        }else{
            if(winner== 'C' | winner== 'C'){
                System.out.println("Guanyar el client!! S:" + puntsServer + " - C:" + puntsClient);
            }else{
                frase = "Error";
                System.out.println(frase);
                enviarError( 10, Integer.toString(frase.length()), frase);
            }
        }
        comunicacio.finalitzarPartida();

    }

    private static void tractar20() {
        comunicacio.llegirError();
    }

    
    
}
