package mx.edu.utng.wsequipo;

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

    private EditText etNombre;
    private EditText etTipoEquipo;
    private EditText etNumeroEquipo;
    private EditText etDescripcion;
    private EditText etFilial;
    private ToggleButton tbEstatus;

    private Button btGuardar;
    private Button btListar;

    private Equipo equipo = null;

    final String NAMESPACE = "http://ws.utng.edu.mx";
    final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    static String URL = "http://192.168.24.81:8080/WSEquipo/services/EquipoWS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startComponentes();
    }

    private void startComponentes(){
        etNombre = (EditText)findViewById(R.id.txt_nombre);
        etTipoEquipo = (EditText)findViewById(R.id.txt_tipo_equipo);
        etNumeroEquipo = (EditText)findViewById(R.id.txt_numero_equipo);
        etDescripcion = (EditText)findViewById(R.id.txt_descripcion);
        etFilial = (EditText)findViewById(R.id.txt_filial);
        tbEstatus = (ToggleButton) findViewById(R.id.toogle_estatus);
        btGuardar = (Button)findViewById(R.id.btn_guardar);
        btListar = (Button)findViewById(R.id.btn_listar);

        btGuardar.setOnClickListener(this);
        btListar.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_equipo_w, menu);
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
        String nombre = etNombre.getText().toString();
        String tipoEquipo = etTipoEquipo.getText().toString();
        String numeroEquipo = etNumeroEquipo.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String filial = etFilial.getText().toString();
        String estatus = tbEstatus.getText().toString();

        if (view.getId() == btGuardar.getId()) {
            if (nombre != null && !nombre.isEmpty() &&
                    nombre != null && !tipoEquipo.isEmpty() &&
                    numeroEquipo != null && !numeroEquipo.isEmpty() &&
                    descripcion != null && !descripcion.isEmpty() &&
                    filial != null && !filial.isEmpty() &&
                    estatus != null && !estatus.isEmpty()) {
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
        if (btListar.getId() == view.getId()) {
            startActivity(new Intent(MainActivity.this, ListEquipo.class));
        }
    }//fin conClick

    private void cleanEditTex() {
        etNombre.setText("");
        etTipoEquipo.setText("");
        etNumeroEquipo.setText("");
        etDescripcion.setText("");
        etFilial.setText("");
        tbEstatus.setText("");
    }

    private class TaskWSInsert extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            boolean result = true;
            final String METHOD_NAME = "addEquipo";
            final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;

            SoapObject request =
                    new SoapObject(NAMESPACE, METHOD_NAME);

            equipo = new Equipo();
            equipo.setProperty(0, 0);
            obtenerDatos();

            PropertyInfo info = new PropertyInfo();
            info.setName("equipo");
            info.setValue(equipo);
            info.setType(equipo.getClass());
            request.addProperty(info);
            envelope.setOutputSoapObject(request);
            envelope.addMapping(NAMESPACE, "Equipo", Equipo.class);

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

            final String METHOD_NAME = "editEquipo";
            final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            equipo = new Equipo();
            equipo.setProperty(0, getIntent().getExtras().getString("valor0"));
            obtenerDatos();

            PropertyInfo info = new PropertyInfo();
            info.setName("equipo");
            info.setValue(equipo);
            info.setType(equipo.getClass());

            request.addProperty(info);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);

            envelope.setOutputSoapObject(request);

            envelope.addMapping(NAMESPACE, "Equipo", equipo.getClass());

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
        equipo.setProperty(1, etNombre.getText().toString());
        equipo.setProperty(2, etTipoEquipo.getText().toString());
        equipo.setProperty(3, etNumeroEquipo.getText().toString());
        equipo.setProperty(4, etDescripcion.getText().toString());
        equipo.setProperty(5, etFilial.getText().toString());
        equipo.setProperty(6, tbEstatus.getText().toString());


        if(tbEstatus.isChecked()){
            equipo.setProperty(6,2);
        }else{
            equipo.setProperty(6,1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle datosRegreso = this.getIntent().getExtras();
        try {

            etNombre.setText(datosRegreso.getString("valor1"));
            etTipoEquipo.setText(datosRegreso.getString("valor2"));
            etNumeroEquipo.setText(datosRegreso.getString("valor3"));
            etDescripcion.setText(datosRegreso.getString("valor4"));
            etFilial.setText(datosRegreso.getString("valor5"));



            if (datosRegreso.getString("valor6").equals("1")) {
                tbEstatus.setChecked(false);
            } else {
                tbEstatus.setChecked(true);
            }
        } catch (Exception e) {
            Log.e("Error al Regargar", e.toString());
        }

    }

}