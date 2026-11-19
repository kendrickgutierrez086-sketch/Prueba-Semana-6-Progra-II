/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author kendr
 */
public class Duplicados {
    
    
    
    private HashMap<String,ArrayList<File>> grupos=new HashMap<>();
    public void buscarDuplicados(File carpeta){
        grupos.clear();
        if(carpeta==null || !carpeta.exists() || !carpeta.isDirectory()){
            System.out.println("Directorio invalido");
            return;
        }
        recorrer(carpeta);
        eliminarNoDuplicados();
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
                String clave=elemento.getName()+"_"+elemento.length();
                if(!grupos.containsKey(clave)){
                    grupos.put(clave,new ArrayList<File>());
                }
                grupos.get(clave).add(elemento);
            }
        }
    }

    private void eliminarNoDuplicados(){
        ArrayList<String> eliminar=new ArrayList<>();
        for(Map.Entry<String,ArrayList<File>> grupo:grupos.entrySet()){
            ArrayList<File> archivos=grupo.getValue();
            if(archivos.size()<2){
                eliminar.add(grupo.getKey());
            }
        }

        for(String clave:eliminar){
            grupos.remove(clave);
        }
    }

    public void mostrarDuplicados(){

        if(grupos.isEmpty()){
            System.out.println("No se encontraron archivos duplicados");
            return;
        }
        System.out.println("ARCHIVOS DUPLICADoS");
        for(Map.Entry<String,ArrayList<File>> grupo:grupos.entrySet()){
            System.out.println("-------------------------");
            for(File archivo:grupo.getValue()){
                System.out.println("Nombre: "+archivo.getName());
                System.out.println("Ruta:"+archivo.getAbsolutePath());
                System.out.println("Tamano: "+archivo.length()+" bytes");
                System.out.println();
            }
        }

        System.out.println("Total duplicados: "+grupos.size());
    }
    public HashMap<String,ArrayList<File>> getGrupos(){
        return grupos;
    }
}
