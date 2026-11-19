package Adaptador;

import GUI.VentanaPrincipal;
import modelo.Buscador;
import modelo.Estadisticas;
import modelo.Duplicados;
import modelo.GestorArchivos;
import modelo.Bitacora;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import modelo.Configuracion;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AdaptadorPrincipal {

    private Buscador buscador=new Buscador();
    private File elementoSeleccionado=null;
    private Estadisticas estadisticas=new Estadisticas();
    private Duplicados duplicados=new Duplicados();
    private GestorArchivos gestor=new GestorArchivos();
    private Bitacora bitacora=new Bitacora();
    private Configuracion configuracion=new Configuracion();

    private VentanaPrincipal vista;
    private File carpetaSeleccionada=null;

    public AdaptadorPrincipal(VentanaPrincipal vista){
    this.vista=vista;
    iniciarEventos();
    cargarConfiguracion();

    vista.addWindowListener(new WindowAdapter(){
        @Override
        public void windowClosing(WindowEvent e){
            guardarConfiguracion();
        }
    });
}

    private void iniciarEventos(){

        vista.getBtnActualizarBitacora().addActionListener(e->actualizarBitacora());
        vista.getBtnLimpiarBitacora().addActionListener(e->limpiarBitacora());

        vista.getBtnCrearCarpeta().addActionListener(e->crearCarpeta());
        vista.getBtnCrearArchivo().addActionListener(e->crearArchivo());
        vista.getBtnRenombrar().addActionListener(e->renombrar());
        vista.getBtnCopiar().addActionListener(e->copiar());
        vista.getBtnMover().addActionListener(e->mover());
        vista.getBtnEliminar().addActionListener(e->eliminar());

        vista.getBtnDuplicados().addActionListener(e->mostrarDuplicados());

        vista.getBtnExaminar().addActionListener(e->seleccionarCarpeta());

        vista.getArbolCarpetas().addTreeSelectionListener(e->seleccionarElemento());

        vista.getBtnAplicarFiltros().addActionListener(e->aplicarFiltros());
        vista.getBtnLimpiarFiltros().addActionListener(e->limpiarFiltros());

        vista.getBtnActualizarEstadisticas().addActionListener(e->actualizarEstadisticas());
    }

    private void seleccionarCarpeta(){

        JFileChooser selector=new JFileChooser();

        selector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int resultado=selector.showOpenDialog(vista);

        if(resultado==JFileChooser.APPROVE_OPTION){

            carpetaSeleccionada=selector.getSelectedFile();

            vista.getTxtRuta().setText(carpetaSeleccionada.getAbsolutePath());

            cargarArbol();
        }
    }

    private void cargarArbol(){

        DefaultMutableTreeNode raiz=new DefaultMutableTreeNode(carpetaSeleccionada);

        agregarElementos(carpetaSeleccionada,raiz);

        DefaultTreeModel modelo=new DefaultTreeModel(raiz);

        vista.getArbolCarpetas().setModel(modelo);

        vista.getArbolCarpetas().setCellRenderer(new DefaultTreeCellRenderer(){

            @Override
            public java.awt.Component getTreeCellRendererComponent(javax.swing.JTree arbol,Object valor,boolean seleccionado,boolean expandido,boolean hoja,int fila,boolean enfocado){

                super.getTreeCellRendererComponent(arbol,valor,seleccionado,expandido,hoja,fila,enfocado);

                DefaultMutableTreeNode nodo=(DefaultMutableTreeNode)valor;
                File archivo=(File)nodo.getUserObject();

                setText(archivo.getName());

                return this;
            }
        });

        vista.getArbolCarpetas().setSelectionPath(new TreePath(raiz.getPath()));
    }

    private void agregarElementos(File carpeta,DefaultMutableTreeNode nodo){

        File[] archivos=carpeta.listFiles();

        if(archivos==null){
            return;
        }

        for(File archivo:archivos){

            DefaultMutableTreeNode hijo=new DefaultMutableTreeNode(archivo);

            nodo.add(hijo);

            if(archivo.isDirectory()){
                agregarElementos(archivo,hijo);
            }
        }
    }

    private void seleccionarElemento(){

        DefaultMutableTreeNode nodo=(DefaultMutableTreeNode)vista.getArbolCarpetas().getLastSelectedPathComponent();

        if(nodo==null){
            return;
        }

        File archivo=(File)nodo.getUserObject();

        elementoSeleccionado=archivo;

        mostrarTabla(archivo);
        mostrarInformacion(archivo);
    }

    private void mostrarTabla(File archivo){
        DefaultTableModel modelo=new DefaultTableModel();
        modelo.addColumn("Nombre");
        modelo.addColumn("Tipo");
        modelo.addColumn("Tamaño (bytes)");
        modelo.addColumn("Ultima modificacion");
        File[] elementos;

        if(archivo.isDirectory()){
            elementos=archivo.listFiles();
        }else{
            elementos=new File[]{archivo};
        }

        if(elementos!=null){

            for(File elemento:elementos){

                String tipo=elemento.isDirectory()?"Carpeta":"Archivo";

                Calendar fecha=Calendar.getInstance();
                fecha.setTimeInMillis(elemento.lastModified());

                String modificacion=String.format("%02d/%02d/%04d",
                        fecha.get(Calendar.DAY_OF_MONTH),
                        fecha.get(Calendar.MONTH)+1,
                        fecha.get(Calendar.YEAR));

                modelo.addRow(new Object[]{
                    elemento.getName(),
                    tipo,
                    elemento.length(),
                    modificacion
                });
            }
        }

        vista.getTablaArchivos().setModel(modelo);
    }

    private void mostrarInformacion(File archivo){

        vista.getLblInfoNombre().setText("Nombre: "+archivo.getName());

        vista.getLblInfoTipo().setText("Tipo: "+(archivo.isDirectory()?"Carpeta":"Archivo"));

        vista.getLblInfoTamaño().setText("Tamaño: "+archivo.length()+" bytes");

        vista.getLblInfoUbicacion().setText("Ubicacion: "+archivo.getAbsolutePath());
    }

    private void aplicarFiltros(){

        try{

            buscador.limpiarFiltros();

            buscador.setNombre(vista.getTxtNombre().getText().trim());

            buscador.setExtension(vista.getTxtExtension().getText().trim());

            String minimo=vista.getTxtTamañoMinimo().getText().trim();
            String maximo=vista.getTxtTamañoMaximo().getText().trim();

            if(!minimo.isEmpty()){
                buscador.setTamanoMinimo(Double.parseDouble(minimo));
            }

            if(!maximo.isEmpty()){
                buscador.setTamanoMaximo(Double.parseDouble(maximo));
            }

            SimpleDateFormat formato=new SimpleDateFormat("dd/MM/yyyy");
            formato.setLenient(false);

            String fechaMinima=vista.getTxtFechaMinima().getText().trim();
            String fechaMaxima=vista.getTxtFechaMaxima().getText().trim();

            if(!fechaMinima.isEmpty()){
                Calendar fecha=Calendar.getInstance();
                fecha.setTime(formato.parse(fechaMinima));
                buscador.setFechaMinima(fecha);
            }

            if(!fechaMaxima.isEmpty()){
                Calendar fecha=Calendar.getInstance();
                fecha.setTime(formato.parse(fechaMaxima));
                fecha.set(Calendar.HOUR_OF_DAY,23);
                fecha.set(Calendar.MINUTE,59);
                fecha.set(Calendar.SECOND,59);
                fecha.set(Calendar.MILLISECOND,999);
                buscador.setFechaMaxima(fecha);
            }

            if(carpetaSeleccionada==null){
                JOptionPane.showMessageDialog(vista,"Primero selecciona una carpeta");
                return;
            }

            ArrayList<File> resultados=buscador.buscar(carpetaSeleccionada);

            DefaultTableModel modelo=new DefaultTableModel();

            modelo.addColumn("Nombre");
            modelo.addColumn("Tipo");
            modelo.addColumn("Tamaño (bytes)");
            modelo.addColumn("Ultima modificacion");

            for(File archivo:resultados){

                Calendar fecha=Calendar.getInstance();
                fecha.setTimeInMillis(archivo.lastModified());

                String modificacion=String.format("%02d/%02d/%04d",
                        fecha.get(Calendar.DAY_OF_MONTH),
                        fecha.get(Calendar.MONTH)+1,
                        fecha.get(Calendar.YEAR));

                modelo.addRow(new Object[]{
                    archivo.getName(),
                    "Archivo",
                    archivo.length(),
                    modificacion
                });
            }

            vista.getTablaArchivos().setModel(modelo);

            JOptionPane.showMessageDialog(vista,"Archivos encontrados: "+resultados.size());

        }catch(Exception e){

            JOptionPane.showMessageDialog(vista,"Revisa los valores ingresados. Las fechas deben usar dd/MM/yyyy y los tamaños deben ser numeros.");
        }
    }

    private void limpiarFiltros(){

        buscador.limpiarFiltros();

        vista.getTxtNombre().setText("");
        vista.getTxtExtension().setText("");
        vista.getTxtTamañoMinimo().setText("");
        vista.getTxtTamañoMaximo().setText("");
        vista.getTxtFechaMinima().setText("");
        vista.getTxtFechaMaxima().setText("");

        if(elementoSeleccionado!=null){
            mostrarTabla(elementoSeleccionado);
        }else if(carpetaSeleccionada!=null){
            mostrarTabla(carpetaSeleccionada);
        }
    }

    private void actualizarEstadisticas(){

        if(carpetaSeleccionada==null){
            JOptionPane.showMessageDialog(vista,"Primero selecciona una carpeta");
            return;
        }

        estadisticas.analizar(carpetaSeleccionada);

        vista.getLblTotalArchivos().setText("Total de archivos: "+estadisticas.getTotalArchivos());

        vista.getLblTamañoTotal().setText("Tamaño total: "+estadisticas.getTamañoTotal());

        vista.getLblArchivosTxt().setText("Archivos TXT: "+estadisticas.getTxt());

        vista.getLblArchivosJava().setText("Archivos JAVA: "+estadisticas.getJava());

        vista.getLblArchivosPdf().setText("Archivos PDF: "+estadisticas.getPdf());

        vista.getLblOtrosArchivos().setText("Otros archivos: "+estadisticas.getOtros());

        vista.getLblArchivoReciente().setText("Archivo reciente: "+estadisticas.getArchivoReciente());

        vista.getLblArchivoMayor().setText("Archivo mayor: "+estadisticas.getArchivoMayor());
    }

    private void mostrarDuplicados(){

        if(carpetaSeleccionada==null){
            JOptionPane.showMessageDialog(vista,"Primero selecciona una carpeta");
            return;
        }

        duplicados.buscarDuplicados(carpetaSeleccionada);

        HashMap<String,ArrayList<File>> grupos=duplicados.getGrupos();

        if(grupos.isEmpty()){
            JOptionPane.showMessageDialog(vista,"No se encontraron archivos duplicados");
            return;
        }

        StringBuilder texto=new StringBuilder();

        for(Map.Entry<String,ArrayList<File>> grupo:grupos.entrySet()){

            texto.append("ARCHIVOS DUPLICADOS\n");
            texto.append("-------------------------\n");

            for(File archivo:grupo.getValue()){

                texto.append("Nombre: ").append(archivo.getName()).append("\n");
                texto.append("Tamaño: ").append(archivo.length()).append(" bytes\n");
                texto.append("Ubicacion: ").append(archivo.getAbsolutePath()).append("\n\n");
            }
        }

        texto.append("Total de grupos duplicados: ").append(grupos.size());

        JTextArea area=new JTextArea(texto.toString(),20,50);
        area.setEditable(false);
        area.setCaretPosition(0);

        JScrollPane scroll=new JScrollPane(area);

        JOptionPane.showMessageDialog(vista,scroll,"Archivos duplicados",JOptionPane.INFORMATION_MESSAGE);
    }

    private File obtenerCarpetaActual(){

        if(elementoSeleccionado!=null){

            if(elementoSeleccionado.isDirectory()){
                return elementoSeleccionado;
            }

            return elementoSeleccionado.getParentFile();
        }

        return carpetaSeleccionada;
    }

    private void actualizarArbol(){

        if(carpetaSeleccionada!=null && carpetaSeleccionada.exists()){
            cargarArbol();
        }
    }

    private void crearCarpeta(){

        File carpeta=obtenerCarpetaActual();

        if(carpeta==null){
            JOptionPane.showMessageDialog(vista,"Selecciona una carpeta");
            return;
        }

        String nombre=JOptionPane.showInputDialog(vista,"Nombre de la carpeta:");

        if(nombre==null || nombre.trim().isEmpty()){
            return;
        }

        File nueva=new File(carpeta,nombre);

        if(gestor.crearCarpeta(nueva.getAbsolutePath())){

            registrarAccion("Crear carpeta",nueva);

            JOptionPane.showMessageDialog(vista,"Carpeta creada correctamente");

            actualizarArbol();

        }else{

            JOptionPane.showMessageDialog(vista,"No se pudo crear la carpeta");
        }
    }

    private void crearArchivo(){

        File carpeta=obtenerCarpetaActual();

        if(carpeta==null){
            JOptionPane.showMessageDialog(vista,"Selecciona una carpeta");
            return;
        }

        String nombre=JOptionPane.showInputDialog(vista,"Nombre del archivo:");

        if(nombre==null || nombre.trim().isEmpty()){
            return;
        }

        try{

            File nuevo=new File(carpeta,nombre);

            if(gestor.crearArchivo(nuevo.getAbsolutePath(),"")){

                registrarAccion("Crear archivo",nuevo);

                JOptionPane.showMessageDialog(vista,"Archivo creado correctamente");

                actualizarArbol();

            }else{

                JOptionPane.showMessageDialog(vista,"No se pudo crear el archivo");
            }

        }catch(Exception e){

            JOptionPane.showMessageDialog(vista,"Error al crear el archivo");
        }
    }

    private void renombrar(){

        if(elementoSeleccionado==null || elementoSeleccionado.equals(carpetaSeleccionada)){
            JOptionPane.showMessageDialog(vista,"Selecciona un archivo o carpeta");
            return;
        }

        String nombre=JOptionPane.showInputDialog(vista,"Nuevo nombre:",elementoSeleccionado.getName());

        if(nombre==null || nombre.trim().isEmpty()){
            return;
        }

        if(gestor.renombrar(elementoSeleccionado,nombre,false)){

            File renombrado=new File(elementoSeleccionado.getParentFile(),nombre);

            registrarAccion("Renombrar",renombrado);

            elementoSeleccionado=renombrado;

            JOptionPane.showMessageDialog(vista,"Elemento renombrado correctamente");

            actualizarArbol();

        }else{

            JOptionPane.showMessageDialog(vista,"No se pudo renombrar el elemento. Comprueba que el nombre no exista");
        }
    }

    private void copiar(){

        if(elementoSeleccionado==null || !elementoSeleccionado.isFile()){
            JOptionPane.showMessageDialog(vista,"Selecciona un archivo para copiar");
            return;
        }

        JFileChooser selector=new JFileChooser();
        selector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if(selector.showOpenDialog(vista)==JFileChooser.APPROVE_OPTION){

            try{

                File carpeta=selector.getSelectedFile();
                File destino=new File(carpeta,elementoSeleccionado.getName());

                boolean confirmar=false;

                if(destino.exists()){

                    int respuesta=JOptionPane.showConfirmDialog(vista,
                            "El archivo ya existe. Deseas reemplazarlo?",
                            "Confirmar copia",
                            JOptionPane.YES_NO_OPTION);

                    if(respuesta!=JOptionPane.YES_OPTION){
                        return;
                    }

                    confirmar=true;
                }

                if(gestor.copiar(elementoSeleccionado,destino,confirmar)){

                    registrarAccion("Copiar archivo",destino);

                    JOptionPane.showMessageDialog(vista,"Archivo copiado correctamente");

                    actualizarArbol();

                }else{

                    JOptionPane.showMessageDialog(vista,"No se pudo copiar el archivo");
                }

            }catch(Exception e){

                JOptionPane.showMessageDialog(vista,"Error al copiar el archivo");
            }
        }
    }

    private void mover(){

        if(elementoSeleccionado==null || !elementoSeleccionado.isFile()){
            JOptionPane.showMessageDialog(vista,"Selecciona un archivo para mover");
            return;
        }

        JFileChooser selector=new JFileChooser();
        selector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if(selector.showOpenDialog(vista)==JFileChooser.APPROVE_OPTION){

            try{

                File carpeta=selector.getSelectedFile();
                File destino=new File(carpeta,elementoSeleccionado.getName());

                boolean confirmar=false;

                if(destino.exists()){

                    int respuesta=JOptionPane.showConfirmDialog(vista,
                            "El archivo ya existe. Deseas reemplazarlo?",
                            "Confirmar movimiento",
                            JOptionPane.YES_NO_OPTION);

                    if(respuesta!=JOptionPane.YES_OPTION){
                        return;
                    }

                    confirmar=true;
                }

                if(gestor.mover(elementoSeleccionado,destino,confirmar)){

                    registrarAccion("Mover archivo",destino);

                    elementoSeleccionado=null;

                    JOptionPane.showMessageDialog(vista,"Archivo movido correctamente");

                    actualizarArbol();

                }else{

                    JOptionPane.showMessageDialog(vista,"No se pudo mover el archivo");
                }

            }catch(Exception e){

                JOptionPane.showMessageDialog(vista,"Error al mover el archivo");
            }
        }
    }

    private void eliminar(){

        if(elementoSeleccionado==null || elementoSeleccionado.equals(carpetaSeleccionada)){
            JOptionPane.showMessageDialog(vista,"Selecciona un archivo o carpeta");
            return;
        }

        int respuesta=JOptionPane.showConfirmDialog(vista,
                "Estas seguro de eliminar este elemento?\n"+elementoSeleccionado.getAbsolutePath(),
                "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION);

        if(respuesta==JOptionPane.YES_OPTION){

            if(gestor.eliminar(elementoSeleccionado,true)){

                registrarAccion("Eliminar",elementoSeleccionado);

                JOptionPane.showMessageDialog(vista,"Elemento eliminado correctamente");

                elementoSeleccionado=null;

                actualizarArbol();

            }else{

                JOptionPane.showMessageDialog(vista,"No se pudo eliminar el elemento");
            }
        }
    }

    private void actualizarBitacora(){

        try{

            vista.getAreaBitacora().setText(bitacora.leerBitacora());

            vista.getAreaBitacora().setCaretPosition(0);

        }catch(Exception e){

            JOptionPane.showMessageDialog(vista,"Error al leer la bitacora");
        }
    }

    private void limpiarBitacora(){

        int respuesta=JOptionPane.showConfirmDialog(vista,
                "Deseas eliminar todos los registros de la bitacora?",
                "Confirmar limpieza",
                JOptionPane.YES_NO_OPTION);

        if(respuesta==JOptionPane.YES_OPTION){

            try{

                bitacora.limpiarBitacora();

                vista.getAreaBitacora().setText("");

                JOptionPane.showMessageDialog(vista,"Bitacora limpiada correctamente");

            }catch(Exception e){

                JOptionPane.showMessageDialog(vista,"Error al limpiar la bitacora");
            }
        }
    }

    private void registrarAccion(String accion,File archivo){
        try{
            bitacora.registrar(accion,archivo.getAbsolutePath());
        }catch(Exception e){
            JOptionPane.showMessageDialog(vista,"No se pudo guardar el registro en la bitacora");
        }
    }
    private void cargarConfiguracion(){

    try{

        configuracion.cargar();

        vista.getTxtNombre().setText(configuracion.getNombre());
        vista.getTxtExtension().setText(configuracion.getExtension());

        vista.getTxtTamañoMinimo().setText(
                configuracion.getTamañoMinimo().equals("0")?"":configuracion.getTamañoMinimo());

        vista.getTxtTamañoMaximo().setText(
                configuracion.getTamañoMaximo().equals("0")?"":configuracion.getTamañoMaximo());

        String ruta=configuracion.getRuta();

        if(!ruta.isEmpty()){

            File carpeta=new File(ruta);

            if(carpeta.exists() && carpeta.isDirectory()){

                carpetaSeleccionada=carpeta;

                vista.getTxtRuta().setText(ruta);

                cargarArbol();
            }
        }

    }catch(Exception e){

        JOptionPane.showMessageDialog(vista,"No se pudo cargar la configuracion");
    }
}
    private void guardarConfiguracion(){

    try{

        if(carpetaSeleccionada!=null){
            configuracion.setRuta(carpetaSeleccionada.getAbsolutePath());
        }

        configuracion.setNombre(vista.getTxtNombre().getText());
        configuracion.setExtension(vista.getTxtExtension().getText());

        String minimo=vista.getTxtTamañoMinimo().getText().trim();
        String maximo=vista.getTxtTamañoMaximo().getText().trim();

        configuracion.setTamañoMinimo(minimo.isEmpty()?"0":minimo);
        configuracion.setTamañoMaximo(maximo.isEmpty()?"0":maximo);

        configuracion.guardar();

    }catch(Exception e){

        JOptionPane.showMessageDialog(vista,"No se pudo guardar la configuracion");
    }
}
    
    
}
