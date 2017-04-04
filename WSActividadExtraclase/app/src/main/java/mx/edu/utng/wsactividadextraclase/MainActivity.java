package mx.edu.utng.wsactividadextraclase;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etCveActividadExtraclase;
    private EditText etDescription;
    private EditText etLugar;
    private EditText etTotalHoras;
    private ToggleButton tbTipoActividad;

    private Button btGuardar;
    private Button btListar;

    private ActividadExtraclase actividad = null;

    final String NAMESPACE = "http://ws.utng.edu.mx";
    final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    static String URL = "http://192.168.24.81:8080/WSActividadExtraClase/services/ActividadExtraclaseWS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startComponentes();
    }

    private void startComponentes() {
        etCveActividadExtraclase = (EditText) findViewById(R.id.txt_cve_extraclase);
        etDescription = (EditText) findViewById(R.id.txt_descripction);
        etLugar = (EditText) findViewById(R.id.txt_lugar);
        etTotalHoras = (EditText)findViewById(R.id.txt_total_horas);
        tbTipoActividad = (ToggleButton) findViewById(R.id.toogle_tipo_actividad);
        btGuardar = (Button) findViewById(R.id.btn_guardar);
        btListar = (Button) findViewById(R.id.btn_listar);
        btGuardar.setOnClickListener(this);
        btListar.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actividad_extraclase_w, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        String cveActividadExtraclase = etCveActividadExtraclase.getText().toString();
        String description = etDescription.getText().toString();
        String lugar = etLugar.getText().toString();
        String totalHoras = etTotalHoras.getText().toString();
        String tipoActividad = tbTipoActividad.getText().toString();


        if (v.getId() == btGuardar.getId()) {
            if (cveActividadExtraclase != null && !cveActividadExtraclase.isEmpty() &&
                    cveActividadExtraclase != null && !description.isEmpty() &&
                    description != null && !description.isEmpty() &&
                    lugar != null && !lugar.isEmpty() &&
                    totalHoras != null && !totalHoras.isEmpty() &&
                    tipoActividad != null && !tipoActividad.isEmpty()) {
                try {
                    if (getIntent().getExtras().getString("accion")
                            .equals("modificar")) {
                        TaskWSUpdate tarea = new TaskWSUpdate();
                        tarea.execute();
                        cleanEditTex();
                    }

                } catch (Exception e) {
                    //Cuando no se haya mandado una accion por defecto es insertar.
                    TaskWSInsert movie = new TaskWSInsert();
                    movie.execute();
                }
            } else {
                Toast.makeText(this, "llenar todos los campos", Toast.LENGTH_LONG).show();
            }

        }
        if (btListar.getId() == v.getId()) {
            startActivity(new Intent(MainActivity.this, ListaActivdadesExtraclase.class));
        }
    }//fin conClick

    private void cleanEditTex() {
        etCveActividadExtraclase.setText("");
        etDescription.setText("");
        etLugar.setText("");
        etTotalHoras.setText("");
        tbTipoActividad.setText("");
    }


    private class TaskWSInsert extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            boolean result = true;
            final String METHOD_NAME = "addActividadExtraclase";
            final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;

            SoapObject request =
                    new SoapObject(NAMESPACE, METHOD_NAME);

            actividad = new ActividadExtraclase();
            actividad.setProperty(0, 0);
            obtenerDatos();

            PropertyInfo info = new PropertyInfo();
            info.setName("actividad");
            info.setValue(actividad);
            info.setType(actividad.getClass());
            request.addProperty(info);
            envelope.setOutputSoapObject(request);
            envelope.addMapping(NAMESPACE, "Actividad", ActividadExtraclase.class);

            /* Para serializar flotantes y otros tipos no cadenas o enteros*/
            MarshalFloat mf = new MarshalFloat();
            mf.register(envelope);

            HttpTransportSE transporte = new HttpTransportSE(URL);
            try {
                transporte.call(SOAP_ACTION, envelope);
                SoapPrimitive response =
                        (SoapPrimitive) envelope.getResponse();
                String res = response.toString();
                if (!res.equals("1")) {
                    result = false;
                }

            } catch (Exception e) {
                Log.e("Error ", e.getMessage());
                result = false;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                cleanEditTex();
                Toast.makeText(getApplicationContext(),
                        "Registro exitoso.",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Error al insertar.",
                        Toast.LENGTH_SHORT).show();

            }
        }
    }//fin tarea insertar

    private class TaskWSUpdate extends
            AsyncTask<String, Integer, Boolean> {

        protected Boolean doInBackground(String... params) {

            boolean result = true;

            final String METHOD_NAME = "editActividadExtraclase";
            final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            actividad = new ActividadExtraclase();
            actividad.setProperty(0, getIntent().getExtras().getString("valor0"));
            obtenerDatos();

            PropertyInfo info = new PropertyInfo();
            info.setName("actividad");
            info.setValue(actividad);
            info.setType(actividad.getClass());

            request.addProperty(info);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);

            envelope.setOutputSoapObject(request);

            envelope.addMapping(NAMESPACE, "Actividad", actividad.getClass());

            MarshalFloat mf = new MarshalFloat();
            mf.register(envelope);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try {
                transporte.call(SOAP_ACTION, envelope);

                SoapPrimitive resultado_xml = (SoapPrimitive) envelope
                        .getResponse();
                String res = resultado_xml.toString();

                if (!res.equals("1")) {
                    result = false;
                }

            } catch (HttpResponseException e) {
                Log.e("Error HTTP", e.toString());
            } catch (IOException e) {
                Log.e("Error IO", e.toString());
            } catch (XmlPullParserException e) {
                Log.e("Error XmlPullParser", e.toString());
            }

            return result;

        }

        protected void onPostExecute(Boolean result) {

            if (result) {
                Toast.makeText(getApplicationContext(), "Actualizado OK",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Error al actualizar",
                        Toast.LENGTH_SHORT).show();

            }
        }
    }

    public void obtenerDatos(){
        actividad.setProperty(1, etCveActividadExtraclase.getText().toString());
        actividad.setProperty(2, etDescription.getText().toString());
        actividad.setProperty(3, etLugar.getText().toString());
        actividad.setProperty(4, etTotalHoras.getText().toString());


        if(tbTipoActividad.isChecked()){
            actividad.setProperty(4,2);
        }else{
            actividad.setProperty(4,1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle datosRegreso = this.getIntent().getExtras();
        try {

            etCveActividadExtraclase.setText(datosRegreso.getString("valor1"));
            etDescription.setText(datosRegreso.getString("valor2"));
            etLugar.setText(datosRegreso.getString("valor3"));
            etTotalHoras.setText(datosRegreso.getString("valor4"));


            if (datosRegreso.getString("valor5").equals("1")) {
                tbTipoActividad.setChecked(false);
            } else {
                tbTipoActividad.setChecked(true);
            }
        } catch (Exception e) {
            Log.e("Error al Regargar", e.toString());
        }

    }

}