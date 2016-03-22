package info.androidhive.radioucab.Logica;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import info.androidhive.radioucab.Conexiones.conexionPOSTAPIString;
import info.androidhive.radioucab.Conexiones.conexionPUTAPI;
import info.androidhive.radioucab.Model.Usuario;
import info.androidhive.radioucab.R;

public class UsuarioLogica implements RespuestaAsyncTask, RespuestaArchivoAsyncTask, RespuestaStringAsyncTask {

    private File rutaRadioUCAB;
    private final ManejoSesionTwitter sesionTwitter = new ManejoSesionTwitter();
    public Context contexto;
    private Toast toast;
    public Usuario usuario;
    private ManejoString cambioUrl = new ManejoString();
    private String url;
    private boolean actualizar, usuarioNuevo;
    private final ManejoActivity manejoActivity = ManejoActivity.getInstancia();

    public UsuarioLogica() {
    }

    public void crearUsuarioAPI() {
        conexionPOSTAPIString conexion = new conexionPOSTAPIString();
        conexion.contexto = contexto;
        //conexion.mensaje = "Enviando los datos...";
        conexion.delegate = (RespuestaStringAsyncTask) this;
        JSONObject objeto = new JSONObject();
        try {
            objeto.put("nombre", usuario.getNombre());
            objeto.put("apellido", usuario.getApellido());
            objeto.put("usuario_twitter", usuario.getUsuario_twitter());
            objeto.put("token_twitter", usuario.getToken_twitter());
            objeto.put("token_secret_twitter", usuario.getToken_secret_twitter());
            objeto.put("correo", usuario.getCorreo());
            objeto.put("imagen_grande", usuario.getImagen_grande());
            objeto.put("imagen_normal", usuario.getImagen_normal());
            objeto.put("tipo", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        conexion.objeto = objeto;
        conexion.execute("Api/Usuario/Postusuario");
    }

    public void actualizarUsuarioAPI() {
        conexionPUTAPI conexion = new conexionPUTAPI();
        conexion.contexto = contexto;
        //conexion.mensaje = "Enviando los datos...";
        conexion.delegate = this;
        JSONObject objeto = new JSONObject();
        try {
            objeto.put("guid", usuario.getGuid());
            objeto.put("nombre", usuario.getNombre());
            objeto.put("apellido", usuario.getApellido());
            objeto.put("usuario_twitter", usuario.getUsuario_twitter());
            objeto.put("token_twitter", usuario.getToken_twitter());
            objeto.put("token_secret_twitter", usuario.getToken_secret_twitter());
            objeto.put("correo", usuario.getCorreo());
            objeto.put("imagen_grande", usuario.getImagen_grande());
            objeto.put("imagen_normal", usuario.getImagen_normal());
            objeto.put("tipo", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        conexion.objeto = objeto;
        conexion.execute("Api/Usuario/Putusuario");
    }

    public void resetearUsuarioTelefono() {
        rutaRadioUCAB = new File(contexto.getString(R.string.ruta_archivos_radio_ucab));
        borrarCarpetaRecursivo(rutaRadioUCAB);
    }

    private void borrarCarpetaRecursivo(File fileOrDirectory) {
        try {
            if (fileOrDirectory.isDirectory())
                for (File child : fileOrDirectory.listFiles())
                    borrarCarpetaRecursivo(child);
            fileOrDirectory.delete();
        } catch (Exception ex) {
            Log.e("Carpeta", ex.getMessage());
        }
    }

    public void manejoImagenes() {
        resetearUsuarioTelefono();
        cambioUrl = new ManejoString();
        url = cambioUrl.getURLImagen(usuario.getImagen_normal());
        ManejoArchivos almacenarFotoGrande = new ManejoArchivos();
        almacenarFotoGrande.delegate = (RespuestaArchivoAsyncTask) this;
        almacenarFotoGrande.tipo_foto = 0;
        almacenarFotoGrande.execute(url, "picBig", cambioUrl.getFormatoImagen());
    }

    public void almacenarUsuarioContinuacion() {
        if (actualizar && usuarioNuevo) {
            crearUsuarioAPI();
        } else if (actualizar && !usuarioNuevo) {
            actualizarUsuarioAPI();
        } else {
            usuario.save();
            manejoActivity.cambiarFragment("Perfil",false);
        }
    }

    public void almacenarUsuario(boolean actualizar, boolean usuarioNuevo) {
        this.actualizar = actualizar;
        this.usuarioNuevo = usuarioNuevo;
        manejoImagenes();
    }

    public void comprobarUsuario(Usuario usuarioApp, Usuario usuarioBD) {
        //debo actualizar los token de session en la base de datos, aunque creo que esto no es valido, siempre mantiene los mismos
        //tokens
        actualizar = false;
        if (!usuarioApp.getToken_twitter().equals(usuarioBD.getToken_twitter())) {
            usuarioBD.setToken_twitter(usuarioApp.getToken_twitter());
            actualizar = true;
        }
        if (!usuarioApp.getToken_secret_twitter().equals(usuarioBD.getToken_secret_twitter())) {
            usuarioBD.setToken_secret_twitter(usuarioApp.getToken_secret_twitter());
            actualizar = true;
        }
        if (!usuarioApp.getImagen_normal().equals(usuarioBD.getImagen_normal())) {
            usuarioBD.setImagen_normal(usuarioApp.getImagen_normal());
            actualizar = true;
        }
        usuario = usuarioBD;
        almacenarUsuario(actualizar, false);
    }

    @Override
    public void procesoExitoso(JSONArray resultados) {
        //me regresa el usuario almacenado
        JSONObject resultado = null;
        try {
            resultado = resultados.getJSONObject(0);
            usuario.setGuid(resultado.getString("guid").replace("\"", ""));
            usuario.save();
            manejoActivity.cambiarFragment("Perfil",false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void procesoExitoso(JSONObject resultado) {

    }

    @Override
    public void procesoExitoso(int codigo, int tipo) {
        if (codigo == 204) {
            usuario.save();
            manejoActivity.cambiarFragment("Perfil",false);
        }
    }

    @Override
    public void resultadoProceso(int respuesta, int tipo) {
        if (tipo == 0) {
            ManejoArchivos almacenarFotoNormal = new ManejoArchivos();
            almacenarFotoNormal.delegate = this;
            almacenarFotoNormal.tipo_foto = 1;
            almacenarFotoNormal.execute(usuario.getImagen_normal(), "picNormal", cambioUrl.getFormatoImagen());
        } else {
            usuario.setImagen_grande(url);
            usuario.setFormato_imagen(cambioUrl.getFormatoImagen());
            almacenarUsuarioContinuacion();
        }
    }

    @Override
    public void procesoNoExitoso() {
        toast = Toast.makeText(contexto, "No es posible procesar su solicitud, intente más tarde", Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void procesoExitoso(String resultado) {
        //me regresa el guid del usuario almacenado
        usuario.setGuid(resultado.replace("\"", ""));
        usuario.save();
        manejoActivity.cambiarFragment("Perfil",false);
    }

    @Override
    public void procesoNoExitosoString() {
        toast = Toast.makeText(contexto, "No es posible procesar su solicitud, intente más tarde", Toast.LENGTH_LONG);
        toast.show();
    }
}
