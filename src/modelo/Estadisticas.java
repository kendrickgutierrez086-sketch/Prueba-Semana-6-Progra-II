
package modelo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Calendar;

/**
 * Realiza el analisis recursivo de archivos y carpetas.
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

    private int carpetas=0;
    private int carpetasVacias=0;
    private int profundidadMaxima=0;

    private File mayor=null;
    private File reciente=null;

    private ArrayList<File> archivos=new ArrayList<>();
    private Set<String> rutasVisitadas=new HashSet<>();

    public void analizar(File carpeta){
        analizar(carpeta,0);
    }

    public void analizar(File carpeta,int profundidad){

        txt=0;
        java=0;
        pdf=0;
        otros=0;

        tamTxt=0;
        tamJava=0;
        tamPdf=0;
        tamOtros=0;

        carpetas=0;
        carpetasVacias=0;
        profundidadMaxima=0;

        mayor=null;
        reciente=null;

        archivos.clear();
        rutasVisitadas.clear();

        if(carpeta==null || !carpeta.exists() || !carpeta.isDirectory()){
            return;
        }

        recorrer(carpeta,0,profundidad);

        mostrarResultados();
    }

    private void recorrer(File carpeta,int nivel,int limite){

        try{

            String ruta=carpeta.getCanonicalPath();

            if(rutasVisitadas.contains(ruta)){
                return;
            }

            rutasVisitadas.add(ruta);

        }catch(Exception e){
            return;
        }

        if(nivel>profundidadMaxima){
            profundidadMaxima=nivel;
        }

        File[] contenido=carpeta.listFiles();

        if(contenido==null){
            return;
        }

        boolean vacia=contenido.length==0;

        carpetas++;

        if(vacia){
            carpetasVacias++;
        }

        for(File elemento:contenido){

            if(elemento.isDirectory()){

                if(limite==0 || nivel<limite){
                    recorrer(elemento,nivel+1,limite);
                }

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
        long tamaño=archivo.length();

        if(nombre.endsWith(".txt")){
            txt++;
            tamTxt+=tamaño;

        }else if(nombre.endsWith(".java")){
            java++;
            tamJava+=tamaño;

        }else if(nombre.endsWith(".pdf")){
            pdf++;
            tamPdf+=tamaño;

        }else{
            otros++;
            tamOtros+=tamaño;
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

        if(archivos.isEmpty()){
            return 0;
        }

        return cantidad*100.0/archivos.size();
    }

    public String generarReporte(){

        StringBuilder texto=new StringBuilder();

        texto.append("REPORTE DE ESTADISTICAS\n");
        texto.append("========================\n\n");

        texto.append("Total de archivos: ").append(archivos.size()).append("\n");
        texto.append("Total de carpetas: ").append(carpetas).append("\n");
        texto.append("Carpetas vacias: ").append(carpetasVacias).append("\n");
        texto.append("Profundidad maxima: ").append(profundidadMaxima).append("\n\n");

        texto.append("ARCHIVOS TXT\n");
        texto.append("Cantidad: ").append(txt).append("\n");
        texto.append("Tamaño: ").append(convertirTamano(tamTxt)).append("\n");
        texto.append("Porcentaje: ").append(String.format("%.2f",porcentaje(txt))).append("%\n\n");

        texto.append("ARCHIVOS JAVA\n");
        texto.append("Cantidad: ").append(java).append("\n");
        texto.append("Tamaño: ").append(convertirTamano(tamJava)).append("\n");
        texto.append("Porcentaje: ").append(String.format("%.2f",porcentaje(java))).append("%\n\n");

        texto.append("ARCHIVOS PDF\n");
        texto.append("Cantidad: ").append(pdf).append("\n");
        texto.append("Tamaño: ").append(convertirTamano(tamPdf)).append("\n");
        texto.append("Porcentaje: ").append(String.format("%.2f",porcentaje(pdf))).append("%\n\n");

        texto.append("OTROS ARCHIVOS\n");
        texto.append("Cantidad: ").append(otros).append("\n");
        texto.append("Tamaño: ").append(convertirTamano(tamOtros)).append("\n");
        texto.append("Porcentaje: ").append(String.format("%.2f",porcentaje(otros))).append("%\n\n");

        texto.append("Tamaño total: ").append(convertirTamano(tamTxt+tamJava+tamPdf+tamOtros)).append("\n");

        texto.append("Archivo mas grande: ");
        texto.append(mayor==null?"Ninguno":mayor.getAbsolutePath()).append("\n");

        texto.append("Archivo mas reciente: ");
        texto.append(reciente==null?"Ninguno":reciente.getAbsolutePath()).append("\n");

        return texto.toString();
    }

    public void mostrarResultados(){
        System.out.println(generarReporte());
    }

    public int getTotalArchivos(){
        return archivos.size();
    }

    public int getTxt(){
        return txt;
    }

    public int getJava(){
        return java;
    }

    public int getPdf(){
        return pdf;
    }

    public int getOtros(){
        return otros;
    }

    public String getTamañoTotal(){
        return convertirTamano(tamTxt+tamJava+tamPdf+tamOtros);
    }

    public String getTamañoTxt(){
        return convertirTamano(tamTxt);
    }

    public String getTamañoJava(){
        return convertirTamano(tamJava);
    }

    public String getTamañoPdf(){
        return convertirTamano(tamPdf);
    }

    public String getTamañoOtros(){
        return convertirTamano(tamOtros);
    }

    public String getPorcentajeTxt(){
        return String.format("%.2f",porcentaje(txt))+"%";
    }

    public String getPorcentajeJava(){
        return String.format("%.2f",porcentaje(java))+"%";
    }

    public String getPorcentajePdf(){
        return String.format("%.2f",porcentaje(pdf))+"%";
    }

    public String getPorcentajeOtros(){
        return String.format("%.2f",porcentaje(otros))+"%";
    }

    public int getCarpetas(){
        return carpetas;
    }

    public int getCarpetasVacias(){
        return carpetasVacias;
    }

    public int getProfundidadMaxima(){
        return profundidadMaxima;
    }

    public String getArchivoReciente(){
        if(reciente==null){
            return "Ninguno";
        }
        return reciente.getAbsolutePath();
    }

    public String getArchivoMayor(){
        if(mayor==null){
            return "Ninguno";
        }
        return mayor.getAbsolutePath()+" ("+convertirTamano(mayor.length())+")";
    }
}
