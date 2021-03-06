package info.androidhive.radioucab.Logica;

import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import info.androidhive.radioucab.Model.Actualizacion;

public class ActualizacionLogica {

    public Date ultimaActWS;
    private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    private final ManejoFecha manejoFecha = new ManejoFecha();

    public String getActualizacionParrilla(){
        final List<Actualizacion> actualizacionesAlmacenadas = Actualizacion.listAll(Actualizacion.class);
        if (actualizacionesAlmacenadas != null && actualizacionesAlmacenadas.size() > 0) {
            final String fecha = manejoFecha.getDiaSemana(actualizacionesAlmacenadas.get(0).getActParrilla()) + ". "
                    + format.format(actualizacionesAlmacenadas.get(0).getActParrilla());
            return fecha;
        }
        return "";
    }

    public boolean comprobarActualizacionParrilla(){
        final List<Actualizacion> actualizacionesAlmacenadas = Actualizacion.listAll(Actualizacion.class);
        if (actualizacionesAlmacenadas != null && actualizacionesAlmacenadas.size() > 0) {
            if (actualizacionesAlmacenadas.get(0).getActParrilla() != null) {
                if (manejoFecha.getDiaSemana(actualizacionesAlmacenadas.get(0).getActParrilla()).equals(manejoFecha.getDiaSemana(new Date()))) {
                    return true;
                }
            }
        }
        return false;
    }

    //1 evento, 2 noticia, 3 programa, 4 parrilla
    public Actualizacion crearActualizacion(int tipo) {
        Actualizacion nuevaActualizacion = new Actualizacion();
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            Date fecha_falsa = format.parse("01/01/1976");
            switch (tipo) {
                case 1:
                    nuevaActualizacion = new Actualizacion(ultimaActWS, fecha_falsa, fecha_falsa, fecha_falsa, fecha_falsa);
                    break;
                case 2:
                    nuevaActualizacion = new Actualizacion(fecha_falsa, ultimaActWS, fecha_falsa, fecha_falsa, fecha_falsa);
                    break;
                case 3:
                    nuevaActualizacion = new Actualizacion(fecha_falsa, fecha_falsa, ultimaActWS, fecha_falsa, fecha_falsa);
                    break;
                case 4:
                    nuevaActualizacion = new Actualizacion(fecha_falsa, fecha_falsa, fecha_falsa, ultimaActWS, fecha_falsa);
                    break;
                case 5:
                    nuevaActualizacion = new Actualizacion(fecha_falsa, fecha_falsa, fecha_falsa, fecha_falsa, ultimaActWS);
                    break;
            }
        } catch (Exception e) {
            Log.i("Actualizacion", e.getMessage());
        }
        return nuevaActualizacion;
    }

    //1 evento, 2 noticia, 3 programa, 4 parrilla, 5 tag
    public Actualizacion modificarActualizacion(int tipo, Date evento, Date noticia, Date programa, Date parrilla, Date tag) {
        Actualizacion nuevaActualizacion = new Actualizacion();
        switch (tipo) {
            case 1:
                nuevaActualizacion.setActEvento(ultimaActWS);
                nuevaActualizacion.setActNoticia(noticia);
                nuevaActualizacion.setActPrograma(programa);
                nuevaActualizacion.setActParrilla(parrilla);
                nuevaActualizacion.setActTag(tag);
                break;
            case 2:
                nuevaActualizacion.setActEvento(evento);
                nuevaActualizacion.setActNoticia(ultimaActWS);
                nuevaActualizacion.setActPrograma(programa);
                nuevaActualizacion.setActParrilla(parrilla);
                nuevaActualizacion.setActTag(tag);
                break;
            case 3:
                nuevaActualizacion.setActEvento(evento);
                nuevaActualizacion.setActNoticia(noticia);
                nuevaActualizacion.setActPrograma(ultimaActWS);
                nuevaActualizacion.setActParrilla(parrilla);
                nuevaActualizacion.setActTag(tag);
                break;
            case 4:
                nuevaActualizacion.setActEvento(evento);
                nuevaActualizacion.setActNoticia(noticia);
                nuevaActualizacion.setActPrograma(programa);
                nuevaActualizacion.setActParrilla(ultimaActWS);
                nuevaActualizacion.setActTag(tag);
                break;
            case 5:
                nuevaActualizacion.setActEvento(evento);
                nuevaActualizacion.setActNoticia(noticia);
                nuevaActualizacion.setActPrograma(programa);
                nuevaActualizacion.setActParrilla(parrilla);
                nuevaActualizacion.setActTag(ultimaActWS);
                break;
        }
        return nuevaActualizacion;
    }

    public void almacenarUltimaActualizacion(int tipo, Date ultimaActWS) {
        this.ultimaActWS = ultimaActWS;
        try {
            Actualizacion ultimaActualizacion = new Actualizacion();
            Actualizacion nuevaActualizacion = new Actualizacion();
            List<Actualizacion> listaActualizaciones = Actualizacion.listAll(Actualizacion.class);
            if (listaActualizaciones != null && listaActualizaciones.size() > 0) {
                ultimaActualizacion = listaActualizaciones.get(0);
                Date noticia = ultimaActualizacion.getActNoticia();
                Date evento = ultimaActualizacion.getActEvento();
                Date programa = ultimaActualizacion.getActPrograma();
                Date parrilla = ultimaActualizacion.getActParrilla();
                Date tag = ultimaActualizacion.getActTag();
                nuevaActualizacion = modificarActualizacion(tipo, evento, noticia, programa, parrilla, tag);
                Actualizacion.deleteAll(Actualizacion.class);
            } else {
                nuevaActualizacion = crearActualizacion(tipo);
            }
            nuevaActualizacion.save();
        } catch (Exception e) {
            Log.e("Actualizacion: ult.act", e.getMessage());
        }
    }

}
