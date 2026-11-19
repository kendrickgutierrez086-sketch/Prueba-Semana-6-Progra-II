/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

/**
 *
 * @author kendr
 */
public class Configuracion {
    
    private File archivo=new File("config.txt");

    private String ruta="";
    private String nombre="";
    private String extension="";
    private String tamañoMinimo="0";
    private String tamañoMaximo="0";
    private String profundidad="0";

    public void guardar()throws IOException{
        FileWriter escribir=new FileWriter(archivo);
        escribir.write(ruta+"\n");
        escribir.write(nombre+"\n");
        escribir.write(extension+"\n");
        escribir.write(tamañoMinimo+"\n");
        escribir.write(tamañoMaximo+"\n");
        escribir.write(profundidad+"\n");
        escribir.close();
    }

    public void cargar()throws IOException{

        if(!archivo.exists()){
            return;
        }
        FileReader entrada=new FileReader(archivo);
        BufferedReader leer=new BufferedReader(entrada);
        String dato;

        dato=leer.readLine();
        if(dato!=null) ruta=dato;

        dato=leer.readLine();
        if(dato!=null) nombre=dato;

        dato=leer.readLine();
        if(dato!=null) extension=dato;

        dato=leer.readLine();
        if(dato!=null) tamañoMinimo=dato;
        dato=leer.readLine();
        if(dato!=null) tamañoMaximo=dato;
        dato=leer.readLine();
        if(dato!=null) profundidad=dato;

        leer.close();
    }

    public void setRuta(String ruta){
        this.ruta=ruta;
    }
    public void setNombre(String nombre){
        this.nombre=nombre;
    }
    public void setExtension(String extension){
        this.extension=extension;
    }
    public void setTamañoMinimo(String tamañoMinimo){
        this.tamañoMinimo=tamañoMinimo;
    }
    public void setTamañoMaximo(String tamañoMaximo){
        this.tamañoMaximo=tamañoMaximo;
    }
    public void setProfundidad(String profundidad){
        this.profundidad=profundidad;
    }
    public String getRuta(){
        return ruta;
    }
    public String getNombre(){
        return nombre;
    }
    public String getExtension(){
        return extension;
    }
    public String getTamañoMinimo(){
        return tamañoMinimo;
    }
    public String getTamañoMaximo(){
        return tamañoMaximo;
    }
    public String getProfundidad(){
        return profundidad;
    }
}
