/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
/**
 *
 * @author kendr
 */
public class Estadisticas {
    private int txt=0;
    private int java=0;
    private int pdf=0;
    private int otros=0;
    private long tamTxt=0;
    private long tamJava=0;
    private long tamPdf=0;
    private long tamOtros=0;
    private File mayor=null;
    private File reciente=null;
    private ArrayList<File> archivos=new ArrayList<>();

    public void analizar(File carpeta){
        txt=0;
        java=0;
        pdf=0;
        otros=0;
        tamTxt=0;
        tamJava=0;
        tamPdf=0;
        tamOtros=0;
        mayor=null;
        reciente=null;
        archivos.clear();
        recorrer(carpeta);
        mostrarResultados();
    }

    private void recorrer(File carpeta){

        File[] contenido=carpeta.listFiles();

        if(contenido==null){
            return;
        }

        for(File elemento:contenido){

            if(elemento.isDirectory()){

                recorrer(elemento);

            }else if(elemento.isFile()){

                archivos.add(elemento);

                clasificar(elemento);

                if(mayor==null || elemento.length()>mayor.length()){
                    mayor=elemento;
                }

                if(reciente==null || elemento.lastModified()>reciente.lastModified()){
                    reciente=elemento;
                }
            }
        }
    }

    private void clasificar(File archivo){
        String nombre=archivo.getName().toLowerCase();
        long tamano=archivo.length();
        if(nombre.endsWith(".txt")){
            txt++;
            tamTxt+=tamano;
        }else if(nombre.endsWith(".java")){
            java++;
            tamJava+=tamano;
        }else if(nombre.endsWith(".pdf")){
            pdf++;
            tamPdf+=tamano;
        }else{
            otros++;
            tamOtros+=tamano;
        }
    }
    private String convertirTamano(long bytes){
        double kb=bytes/1024.0;
        if(kb<1024){
            return String.format("%.2f KB",kb);
        }
        double mb=kb/1024;
        if(mb<1024){
            return String.format("%.2f MB",mb);
        }
        double gb=mb/1024;
        return String.format("%.2f GB",gb);
    }

    private double porcentaje(int cantidad){
        if(archivos.size()==0){
            return 0;
        }
        return(cantidad*100.0)/archivos.size();
    }

    public void mostrarResultados(){

        System.out.println("ESTADISTICAS DE ARCHIVOS");
        System.out.println("Total de archivos: "+archivos.size());
        System.out.println("\nARCHIVOS TXT");
        System.out.println("Cantidad: "+txt);
        System.out.println("Tamano: "+convertirTamano(tamTxt));
        System.out.println("Porcentaje: "+String.format("%.2f",porcentaje(txt))+"%");

        System.out.println("\nARCHIVOS JAVA");
        System.out.println("Cantidad: "+java);
        System.out.println("Tamano: "+convertirTamano(tamJava));
        System.out.println("Porcentaje: "+String.format("%.2f",porcentaje(java))+"%");

        System.out.println("\nARCHIVOS PDF");
        System.out.println("Cantidad: "+pdf);
        System.out.println("Tamano: "+convertirTamano(tamPdf));
        System.out.println("Porcentaje: "+String.format("%.2f",porcentaje(pdf))+"%");
        System.out.println("\nOTROS ARCHIVOS");
        System.out.println("Cantidad: "+otros);
        System.out.println("Tamano: "+convertirTamano(tamOtros));
        System.out.println("Porcentaje: "+String.format("%.2f",porcentaje(otros))+"%");
        if(mayor!=null){
            System.out.println("\nARCHIVO MAS GRANDE");
            System.out.println(mayor.getAbsolutePath());
            System.out.println(convertirTamano(mayor.length()));
        }
        if(reciente!=null){
            System.out.println("\nARCHIVO MAS RECIENTE");
            System.out.println(reciente.getAbsolutePath());
            System.out.println(new Date(reciente.lastModified()));
        }
    }
}
