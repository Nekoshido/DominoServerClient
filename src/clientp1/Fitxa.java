/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clientp1;

/**
 *
 * @author hector
 */
public class Fitxa {
    private int num1;
    private int num2;
    private char orientacio;
    private char direccio;
    
    public Fitxa(int num1, int num2, char orientacio, char direccio) {
        this.num1 = num1;
        this.num2 = num2;
        this.orientacio = orientacio;
        this.direccio = direccio;
    }

    @Override
    public String toString(){ 
        String paraula;
        if(this.orientacio == 'N'){
            paraula = num1 + "|"+num2;
        }
        else{
             paraula = num2 + "|"+num1;
        }
        return paraula;
    }
    public void girar(){
        int num = num2;
        num2 = num1;
        num1 = num;
        this.orientacio = 'G';
        System.out.println("La fitxa gira");
    }
    public Boolean esDoble(){
        if (this.num1 == this.num2){
            return true;
        }
        else{
            return false;
        }
    }
    
    public int getValor(){
        return this.num1+this.num2;
    }
    
    public int getNum1() {
        return num1;
    }

    public void setNum1(int num1) {
        this.num1 = num1;
    }

    public int getNum2() {
        return num2;
    }

    public void setNum2(int num2) {
        this.num2 = num2;
    }

    public char getOrientacio() {
        return orientacio;
    }

    public void setOrientacio(char orientacio) {
        this.orientacio = orientacio;
    }

    public char getDireccio() {
        return direccio;
    }

    public void setDireccio(char direccio) {
        this.direccio = direccio;
    }
}
