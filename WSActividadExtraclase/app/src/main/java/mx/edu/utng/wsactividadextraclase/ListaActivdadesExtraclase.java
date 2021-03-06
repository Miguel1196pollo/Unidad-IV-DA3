package mx.edu.utng.wsactividadextraclase;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Toshiba on 30/03/2017.
 */
public class ListaActivdadesExtraclase extends ListActivity {

    final String NAMESPACE = "http://ws.utng.edu.mx";

    final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
            SoapEnvelope.VER11);

    private ArrayList<ActividadExtraclase> actividades = new ArrayList<ActividadExtraclase>();
    private int idSeleccionado;
    private int posicionSeleccionado;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TaskWSConsulted consulted = new TaskWSConsulted();
        consulted.execute();
        registerForContextMenu(getListView());
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_modificar:
                ActividadExtraclase actividadExtraclase = actividades.get(posicionSeleccionado);
                Bundle bundleActividad = new Bundle();
                for (int i = 0; i < actividadExtraclase.getPropertyCount(); i++) {
                    bundleActividad.putString("valor" + i, actividadExtraclase.getProperty(i)
                            .toString());
                }
                bundleActividad.putString("accion", "modificar");
                Intent intent = new Intent(ListaActivdadesExtraclase.this, MainActivity.class);
                intent.putExtras(bundleActividad);
                startActivity(intent);

                return true;
            case R.id.item_eliminar:
                TaskWSDelete eliminar = new TaskWSDelete();
                eliminar.execute();

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(getApplicationContext());
        menuInflater.inflate(R.menu.menu_regresar, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_regresar:
                startActivity(new Intent(ListaActivdadesExtraclase.this, MainActivity.class));
                break;
            default:
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(getListView().getAdapter().getItem(info.position).toString());
        idSeleccionado = (Integer) actividades.get(info.position).getProperty(0);
        posicionSeleccionado = info.position;
        inflater.inflate(R.menu.menu_contextual, menu);
    }




    private class TaskWSConsulted extends AsyncTask<String, Integer, Boolean> {

        protected Boolean doInBackground(String... params) {

            boolean result = true;
            final String METHOD_NAME = "getActividades";
            final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;
            actividades.clear();
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            envelope.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(MainActivity.URL);
            try {
                transporte.call(SOAP_ACTION, envelope);
                Vector<SoapObject> response = (Vector<SoapObject>) envelope.getResponse();
                if (response != null) {
                    for (SoapObject objSoap : response) {
                        ActividadExtraclase actividad = new ActividadExtraclase();
                        actividad.setProperty(0, Integer.parseInt(objSoap.getProperty("id").toString()));
                        actividad.setProperty(1, objSoap.getProperty("cve_actividad_extraclase").toString());
                        actividad.setProperty(2, objSoap.getProperty("description").toString());
                        actividad.setProperty(3, objSoap.getProperty("lugar"));
                        actividad.setProperty(4, objSoap.getProperty("total_horas"));
                        actividad.setProperty(5, objSoap.getProperty("tipo_actividad").toString());
                        actividades.add(actividad);
                    }
                }

            } catch (XmlPullParserException e) {
                Log.e("Error XMLPullParser", e.toString());
                result = false;
            } catch (HttpResponseException e) {
                Log.e("Error HTTP", e.toString());

                result = false;
            } catch (IOException e) {
                Log.e("Error IO", e.toString());
                result = false;
            } catch (ClassCastException e) {
                try {
                    SoapObject objSoap = (SoapObject) envelope.getResponse();
                    ActividadExtraclase actividad = new ActividadExtraclase();
                    actividad.setProperty(0, Integer.parseInt(objSoap.getProperty("id").toString()));
                    actividad.setProperty(1, objSoap.getProperty("cve_actividad_extraclase").toString());
                    actividad.setProperty(2, objSoap.getProperty("description").toString());
                    actividad.setProperty(3, objSoap.getProperty("lugar").toString());
                    actividad.setProperty(4, objSoap.getProperty("total_horas").toString());
                    actividad.setProperty(5, objSoap.getProperty("tipo_actividad").toString());

                    actividades.add(actividad);
                } catch (SoapFault e1) {
                    Log.e("Error SoapFault", e.toString());
                    result = false;
                }
            }
            return result;
        }

        protected void onPostExecute(Boolean result) {

            if (result) {
                final String[] datos = new String[actividades.size()];
                for (int i = 0; i < actividades.size(); i++) {
                    datos[i] = actividades.get(i).getProperty(0) + " - "
                            + actividades.get(i).getProperty(1)+ " - "
                            + actividades.get(i).getProperty(2)+ " - "
                            + actividades.get(i).getProperty(3)+ " - "
                            + actividades.get(i).getProperty(4)+ " - "
                            + actividades.get(i).getProperty(5);
                }

                ArrayAdapter<String> adaptador = new ArrayAdapter<String>(
                        ListaActivdadesExtraclase.this,
                        android.R.layout.simple_list_item_1, datos);
                setListAdapter(adaptador);
            } else {
                Toast.makeText(getApplicationContext(), "No se encontraron datos.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class TaskWSDelete extends AsyncTask<String, Integer, Boolean> {

        protected Boolean doInBackground(String... params) {

            boolean result = true;

            final String METHOD_NAME = "removeActividadExtraclase";
            final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;


            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("id", idSeleccionado);

            envelope.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(MainActivity.URL);
            try {
                transporte.call(SOAP_ACTION, envelope);
                SoapPrimitive resultado_xml = (SoapPrimitive) envelope.getResponse();
                String res = resultado_xml.toString();

                if (!res.equals("0")) {
                    result = true;
                }

            } catch (Exception e) {
                Log.e("Error", e.toString());
                result = false;
            }
            return result;
        }

        protected void onPostExecute(Boolean result) {

            if (result) {
                Toast.makeText(getApplicationContext(),
                        "Eliminado", Toast.LENGTH_SHORT).show();
                TaskWSConsulted consulta = new TaskWSConsulted();
                consulta.execute();
            } else {
                Toast.makeText(getApplicationContext(), "Error al eliminar",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}
