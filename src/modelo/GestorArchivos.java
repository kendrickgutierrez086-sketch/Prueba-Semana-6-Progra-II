/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;
import java.io.File;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author kendr
 */
public class GestorArchivos {
    
    public boolean crearCarpeta(String ruta){

        File carpeta=new File(ruta);

        if(carpeta.exists()){
            return false;
        }

        return carpeta.mkdirs();
    }

    public boolean crearArchivo(String ruta,String contenido)throws IOException{

        File archivo=new File(ruta);

        if(archivo.exists()){
            return false;
        }

        File padre=archivo.getParentFile();

        if(padre!=null &&!padre.exists()){
            return false;
        }
        FileWriter escribir=new FileWriter(archivo);
        escribir.write(contenido);
        escribir.close();

        return true;
    }
    public boolean renombrar(File origen,String nuevoNombre,boolean confirmar){
        if(origen==null||!origen.exists()){
            return false;
        }

        if(nuevoNombre==null||nuevoNombre.trim().isEmpty()){
            return false;
        }
        File destino=new File(origen.getParentFile(),nuevoNombre);
        if(destino.exists()&&!confirmar){
            return false;
        }

        if(destino.exists()){
            return false;
        }

        return origen.renameTo(destino);
    }

    public boolean copiar(File origen,File destino,boolean confirmar)throws IOException{
        if(origen==null||!origen.exists()||!origen.isFile()){
            return false;
        }

        if(destino==null||destino.isDirectory()){
            return false;
        }

        if(origen.getCanonicalPath().equals(destino.getCanonicalPath())){
            return false;
        }

        File padre=destino.getParentFile();
        if(padre!=null && !padre.exists()){
            return false;
        }
        if(destino.exists() && !confirmar){
            return false;
        }
        FileInputStream entrada=new FileInputStream(origen);
        FileOutputStream salida=new FileOutputStream(destino);
        try{
            byte[] datos=new byte[1024];
            int cantidad;
            while((cantidad=entrada.read(datos))!=-1){
                salida.write(datos,0,cantidad);
            }
        }catch(IOException e){
            salida.close();
            entrada.close();
            destino.delete();
            throw e;
        }
        entrada.close();
        salida.close();
        return true;
    }

    public boolean mover(File origen,File destino,boolean confirmar)throws IOException{
        if(origen==null||!origen.exists()||!origen.isFile()){
            return false;
        }

        if(destino==null||destino.isDirectory()){
            return false;
        }

        if(origen.getCanonicalPath().equals(destino.getCanonicalPath())){
            return false;
        }

        if(copiar(origen,destino,confirmar)){
            if(origen.delete()){
                return true;
            }
            destino.delete();
        }
        return false;
    }

    public boolean eliminar(File archivo,boolean confirmar){
        if(archivo==null || !archivo.exists()){
            return false;
        }
        if(!confirmar){
            return false;
        }
        return borrarTodo(archivo);
    }

    private boolean borrarTodo(File archivo){
        if(archivo.isDirectory()){
            File[] contenido=archivo.listFiles();
            if(contenido==null){
                return false;
            }
            for(File elemento:contenido){
                if(!borrarTodo(elemento)){
                    return false;
                }
            }
        }
        return archivo.delete();
    }
    
}
