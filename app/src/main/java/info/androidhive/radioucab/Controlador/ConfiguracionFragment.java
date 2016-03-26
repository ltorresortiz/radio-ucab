package info.androidhive.radioucab.Controlador;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import java.util.List;

import info.androidhive.radioucab.Logica.ConfiguracionLogica;
import info.androidhive.radioucab.Logica.ManejoActivity;
import info.androidhive.radioucab.Model.Concurso;
import info.androidhive.radioucab.Model.Configuracion;
import info.androidhive.radioucab.R;

public class ConfiguracionFragment extends Fragment {

    final private ManejoActivity manejoActivity = ManejoActivity.getInstancia();
    private Switch configuracionUno;
    private Switch configuracionDos;
    private Button botonGuardar;
    final private ConfiguracionLogica configuracionLogica = new ConfiguracionLogica();

	public ConfiguracionFragment(){}
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_configuracion, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manejoActivity.editarActivity(7, false, "Configuracion", "Configuración");
        configuracionUno = (Switch) getActivity().findViewById(R.id.campo_switch_configuracion_uno);
        configuracionDos = (Switch) getActivity().findViewById(R.id.campo_switch_configuracion_dos);
        botonGuardar = (Button) getActivity().findViewById(R.id.boton_actualizar_configuracion);
        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                almacenarConfiguraciones();
            }
        });
        cargarConfiguraciones();
    }

    public void cargarConfiguraciones(){
        List<Configuracion> configuracionList = Configuracion.listAll(Configuracion.class);
        if (configuracionList != null && configuracionList.size() > 0){
            if (configuracionList.get(0).getRecibirNotificaciones() == 1)
                configuracionUno.setChecked(true);
            else
                configuracionUno.setChecked(false);
            if (configuracionList.get(0).getUsoHighStreaming() == 1)
                configuracionDos.setChecked(true);
            else
                configuracionDos.setChecked(false);
        }
    }

    public void almacenarConfiguraciones(){
        int valorNotificacion = 0;
        int valorStreaming = 0;
        if (configuracionUno.isChecked()) {
            valorNotificacion = 1;
        }
        if (configuracionDos.isChecked()){
            valorStreaming = 1;
        }
        configuracionLogica.almacenarConfiguracion(valorNotificacion, valorStreaming);
    }

}
