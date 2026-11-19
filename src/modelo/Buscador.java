/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * @author kendr
 */
public class Buscador {
    
    private ArrayList<File> resultados=new ArrayList<>();

    private String nombre="";
    private String extension="";

    private long tamanoMinimo=0;
    private long tamanoMaximo=Long.MAX_VALUE;

    private Calendar fechaMinima=null;
    private Calendar fechaMaxima=null;

    public void setNombre(String nombre){
        this.nombre=nombre.toLowerCase();
    }

    public void setExtension(String extension){
        this.extension=extension.toLowerCase();
    }

    public void setTamanoMinimo(double kb){
        tamanoMinimo=(long)(kb*1024);
    }
    public void setTamanoMaximo(double kb){
        tamanoMaximo=(long)(kb*1024);
    }
    public void setFechaMinima(Calendar fecha){
        fechaMinima=fecha;
    }
    public void setFechaMaxima(Calendar fecha){
        fechaMaxima=fecha;
    }
    public ArrayList<File> buscar(File carpeta){
        resultados.clear();
        if(carpeta==null || !carpeta.exists() || !carpeta.isDirectory()){
            System.out.println("Directorio invalido");
            return resultados;
        }
        recorrer(carpeta);
        return resultados;
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
                if(cumpleFiltros(elemento)){
                    resultados.add(elemento);
                }
            }
        }
    }

    private boolean cumpleFiltros(File archivo){
        String archivoNombre=archivo.getName().toLowerCase();
        if(!archivoNombre.contains(nombre)){
            return false;
        }
        if(!extension.isEmpty()){
            String ext=obtenerExtension(archivo);
            if(!ext.equals(extension)){
                return false;
            }
        }
        long tamano=archivo.length();
        if(tamano<tamanoMinimo || tamano>tamanoMaximo){
            return false;
        }
        Calendar modificacion=Calendar.getInstance();
        modificacion.setTimeInMillis(archivo.lastModified());
        if(fechaMinima!=null){
            if(modificacion.before(fechaMinima)){
                return false;
            }
        }
        if(fechaMaxima!=null){
            if(modificacion.after(fechaMaxima)){
                return false;
            }
        }
        return true;
    }

    private String obtenerExtension(File archivo){

        String nombre=archivo.getName();
        int punto=nombre.lastIndexOf(".");
        if(punto==-1 || punto==nombre.length()-1){
            return "";
        }
        return nombre.substring(punto+1).toLowerCase();
    }

    public void mostrarResultados(){

        if(resultados.isEmpty()){
            System.out.println("No se encontraron archivos");
            return;
        }

        System.out.println("RESULTADOS DE BUSQUEDA");
        for(File archivo:resultados){

            Calendar fecha=Calendar.getInstance();
            fecha.setTimeInMillis(archivo.lastModified());

            System.out.println("-------------------------");
            System.out.println("Nombre: "+archivo.getName());
            System.out.println("Ruta: "+archivo.getAbsolutePath());
            System.out.println("Tamano: "+archivo.length()+" bytes");
            System.out.println("Fecha: "+fecha.get(Calendar.DAY_OF_MONTH)+"/"+(fecha.get(Calendar.MONTH)+1)+"/"+fecha.get(Calendar.YEAR));
        }
        System.out.println("-------------------------");
        System.out.println("Total encontrados: "+resultados.size());
    }

    public ArrayList<File> getResultados(){
        return resultados;
    }

    public void limpiarFiltros(){
        nombre="";
        extension="";
        tamanoMinimo=0;
        tamanoMaximo=Long.MAX_VALUE;
        fechaMinima=null;
        fechaMaxima=null;
    }
}
