/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author kendr
 */
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Calendar;

public class Bitacora {
    private File archivo=new File("bitacora.log");
    public void registrar(String accion,String ruta)throws IOException{

        Calendar fecha=Calendar.getInstance();

        String registro=fecha.get(Calendar.DAY_OF_MONTH)+"/"+(fecha.get(Calendar.MONTH)+1)+"/"+fecha.get(Calendar.YEAR)+" "+fecha.get(Calendar.HOUR_OF_DAY)+":"+fecha.get(Calendar.MINUTE)+":"+fecha.get(Calendar.SECOND)+" | "+accion+" | "+ruta;
        
        FileWriter escribir=new FileWriter(archivo,true);
        escribir.write(registro+"\n");
        escribir.close();
    }

    public String leerBitacora()throws IOException{
        String contenido="";
        if(!archivo.exists()){
            return "No hay bitacoras";
        }
        FileReader entrada=new FileReader(archivo);
        BufferedReader leer=new BufferedReader(entrada);
        String linea;
        while((linea=leer.readLine())!=null){
            contenido+=linea+"\n";
        }
        leer.close();
        return contenido;
    }
    public File getArchivo(){
        return archivo;
    }
    public void limpiarBitacora()throws IOException{

    FileWriter escribir=new FileWriter(archivo,false);
    escribir.close();
}
    
}
