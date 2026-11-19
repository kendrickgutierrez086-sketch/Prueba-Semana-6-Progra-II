/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;
import java.io.File;
/**
 *
 * @author kendr
 */
public class Archivos {
    
    private File file=null;

    private int cantidadArchivos=0;
    private int cantidadCarpetas=0;
    public void setFile(String dir) throws RutaInvalidaException {

        if (dir==null||dir.trim().isEmpty()) {
            throw new RutaInvalidaException("La ruta es invalida");
        }

        File nuevo=new File(dir);

        if (!nuevo.exists()) {
            throw new RutaInvalidaException("La ruta no existe");
        }

        if (!nuevo.isDirectory()) {
            throw new RutaInvalidaException("La ruta no es una carpeta");
        }
                    file=nuevo;
    }
    public void viewInfo() {
        if (file == null) {
            System.out.println("Debe seleccionar un directorio");
            return;
        }

        System.out.println("DIRECTORIO SELECCIONADO");
        System.out.println("Nombre: " + file.getName());
        System.out.println("Direccion: " + file.getPath());
        System.out.println("Direccion absoluta: " + file.getAbsolutePath());
        System.out.println("Ultima modificacion: " + file.lastModified());
    }
    
//recursividad
    public void recorrer() {

        if (file==null) {
            System.out.println("Debe seleccionar un directorio");
            return;
        }
        cantidadArchivos=0;
        cantidadCarpetas=0;
        recorrerCarpetas(file, 0);
        System.out.println("-------------------------");
        System.out.println("RESULTADO DEL RECORRIDO");
        System.out.println("Archivos: " + cantidadArchivos);
        System.out.println("Carpetas: " + cantidadCarpetas);
    }
    private void recorrerCarpetas(File carpeta, int nivel) {

        File[]contenido=carpeta.listFiles();

        if (contenido==null) {
            System.out.println("No se puede acceder: "+carpeta.getPath());
            return;
        }
        for (File elemento:contenido) {
            for (int i=0;i<nivel;i++) {
                System.out.print("    ");
            }
            if (elemento.isDirectory()) {
                System.out.println("[CARPETA] "+elemento.getName());
                cantidadCarpetas++;
                recorrerCarpetas(elemento,nivel+1);
            } else if (elemento.isFile()) {
                System.out.println("[ARCHIVO] "+elemento.getName());
                cantidadArchivos++;
            }
        }
    }
    public File getFile() {
        return file;
    }

    public int getCantidadArchivos() {
        return cantidadArchivos;
    }

    public int getCantidadCarpetas() {
        return cantidadCarpetas;
    }
    
}
