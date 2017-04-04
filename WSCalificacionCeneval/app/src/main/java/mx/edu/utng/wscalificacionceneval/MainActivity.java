package mx.edu.utng.wscalificacionceneval;

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

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etNombreAlumno;
    private EditText etFechaEdicion;
    private EditText etCalificacion;
    private ToggleButton tbActivo;

    private Button btGuardar;
    private Button btListar;

    private CalificacionCeneval ceneval = null;

    final String NAMESPACE = "http://ws.utng.edu.mx";
    final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    static String URL = "http://192.168.24.81:8080/WSCalificacionCeneval/services/CalificacionCenevalWS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startComponentes();
    }

    private void startComponentes() {
        etNombreAlumno = (EditText) findViewById(R.id.txt_nombre_alumno);
        etFechaEdicion = (EditText) findViewById(R.id.txt_fecha_edicion);
        etCalificacion = (EditText) findViewById(R.id.txt_calificacion);
        tbActivo = (ToggleButton) findViewById(R.id.toogle_activo);
        btGuardar = (Button) findViewById(R.id.btn_guardar);
        btListar = (Button) findViewById(R.id.btn_listar);
        btGuardar.setOnClickListener(this);
        btListar.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_calificacion_w, menu);
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
    public void onClick(View view) {
        String nombreAlumno = etNombreAlumno.getText().toString();
        String fechEdicion = etFechaEdicion.getText().toString();
        String calificacion = etCalificacion.getText().toString();
        String activo = tbActivo.getTextOff().toString();

        if (view.getId() == btGuardar.getId()) {
            if (nombreAlumno != null && !nombreAlumno.isEmpty() &&
                    nombreAlumno != null && !fechEdicion.isEmpty() &&
                    fechEdicion != null && !fechEdicion.isEmpty() &&
                    calificacion != null && !calificacion.isEmpty() &&
                    activo != null && !activo.isEmpty()) {
                try {
                    if (getIntent().getExtras().getString("accion")
                            .equals("modificar")) {
                        TaskWSUpdate tarea = new TaskWSUpdate();
                        tarea.execute();

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
        if (btListar.getId() == view.getId()) {
            startActivity(new Intent(MainActivity.this, ListCalificacionCeneval.class));
        }
    }//fin conClick

    private void cleanEditTex() {
        etNombreAlumno.setText("");
        etFechaEdicion.setText("");
        etCalificacion.setText("");

        tbActivo.setChecked(false);
    }


    private class TaskWSInsert extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            boolean result = true;
            final String METHOD_NAME = "addCalificacionCeneval";
            final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;

            SoapObject request =
                    new SoapObject(NAMESPACE, METHOD_NAME);

            ceneval = new CalificacionCeneval();
            ceneval.setProperty(0, 0);
            obtenerDatos();

            PropertyInfo info = new PropertyInfo();
            info.setName("calificacion");
            info.setValue(ceneval);
            info.setType(ceneval.getClass());
            request.addProperty(info);
            envelope.setOutputSoapObject(request);
            envelope.addMapping(NAMESPACE, "Calificacion", CalificacionCeneval.class);

            /* Para serializar flotantes y otros tipos no cadenas o enteros*/
            MarshalFloat mf = new MarshalFloat();
            mf.register(envelope);

            HttpTransportSE transporte = new HttpTransportSE(URL);
            transporte.debug = true;
            try {
                transporte.call(SOAP_ACTION, envelope);

                SoapPrimitive response =
                        (SoapPrimitive) envelope.getResponse();
                String res = response.toString();
                if (!res.equals("1")) {
                    result = true;
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

            final String METHOD_NAME = "editCalificacionCeneval";
            final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            ceneval = new CalificacionCeneval();
            ceneval.setProperty(0, getIntent().getExtras().getString("valor0"));
            obtenerDatos();

            PropertyInfo info = new PropertyInfo();
            info.setName("calificacion");
            info.setValue(ceneval);
            info.setType(ceneval.getClass());

            request.addProperty(info);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);

            envelope.setOutputSoapObject(request);

            envelope.addMapping(NAMESPACE, "calificacion", ceneval.getClass());

            MarshalFloat mf = new MarshalFloat();
            mf.register(envelope);

            HttpTransportSE transporte = new HttpTransportSE(URL);
            transporte.debug = true;
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
        ceneval.setProperty(1, etNombreAlumno.getText().toString());
        ceneval.setProperty(2, etFechaEdicion.getText().toString());
        ceneval.setProperty(3, Float.parseFloat(etCalificacion.getText().toString()));

        if(tbActivo.isChecked()){
            ceneval.setProperty(4,1);
        }else{
            ceneval.setProperty(4,0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle datosRegreso = this.getIntent().getExtras();
        try {

            etNombreAlumno.setText(datosRegreso.getString("valor1"));
            etFechaEdicion.setText(datosRegreso.getString("valor2"));
            etCalificacion.setText(datosRegreso.getString("valor3"));

            if (datosRegreso.getString("valor4").equals("1")) {
                tbActivo.setChecked(false);
            } else {
                tbActivo.setChecked(true);
            }
        } catch (Exception e) {
            Log.e("Error al Regargar", e.toString());
        }

    }

}