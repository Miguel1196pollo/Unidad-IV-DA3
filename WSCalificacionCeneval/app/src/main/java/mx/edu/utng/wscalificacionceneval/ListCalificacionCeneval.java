package mx.edu.utng.wscalificacionceneval;

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
 * Created by Toshiba on 04/04/2017.
 */
public class ListCalificacionCeneval extends ListActivity {
    final String NAMESPACE = "http://ws.utng.edu.mx";

    final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
            SoapEnvelope.VER11);

    private ArrayList<CalificacionCeneval> calificaciones = new ArrayList<CalificacionCeneval>();
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
                CalificacionCeneval calificacion = calificaciones.get(posicionSeleccionado);
                Bundle bundleCalificacion = new Bundle();
                for (int i = 0; i < calificacion.getPropertyCount(); i++) {
                    bundleCalificacion.putString("valor" + i, calificacion.getProperty(i)
                            .toString());
                }
                bundleCalificacion.putString("accion", "modificar");
                Intent intent = new Intent(ListCalificacionCeneval.this, MainActivity.class);
                intent.putExtras(bundleCalificacion);
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
                startActivity(new Intent(ListCalificacionCeneval.this, MainActivity.class));
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
        idSeleccionado = (Integer) calificaciones.get(info.position).getProperty(0);
        posicionSeleccionado = info.position;
        inflater.inflate(R.menu.menu_contextual, menu);
    }

    private class TaskWSConsulted extends AsyncTask<String, Integer, Boolean> {

        protected Boolean doInBackground(String... params) {

            boolean result = true;
            final String METHOD_NAME = "getCalificaciones";
            final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;
            calificaciones.clear();
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            envelope.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(MainActivity.URL);
            try {
                transporte.call(SOAP_ACTION, envelope);
                Vector<SoapObject> response = (Vector<SoapObject>) envelope.getResponse();
                if (response != null) {
                    for (SoapObject objSoap : response) {
                        CalificacionCeneval calificacion = new CalificacionCeneval();
                        calificacion.setProperty(0, Integer.parseInt(objSoap.getProperty("id").toString()));
                        calificacion.setProperty(1, objSoap.getProperty("nombreAlumno").toString());
                        calificacion.setProperty(2, objSoap.getProperty("fechaEdicion").toString());
                        calificacion.setProperty(3, Float.parseFloat(objSoap.getProperty("calificacion").toString()));
                        if(objSoap.getProperty("activo").toString().trim().equals("1")){
                            calificacion.setProperty(4, "SI");
                        }else{
                            calificacion.setProperty(4, "NO");
                        }
                        Log.i("Hola", "ddd"+objSoap.getProperty("activo"));
                        calificaciones.add(calificacion);
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
                    CalificacionCeneval calificacion = new CalificacionCeneval();
                    calificacion.setProperty(0, Integer.parseInt(objSoap.getProperty("id").toString()));
                    calificacion.setProperty(1, objSoap.getProperty("nombreAlumno").toString());
                    calificacion.setProperty(2, objSoap.getProperty("fechaEdicion").toString());
                    calificacion.setProperty(3, Float.parseFloat(objSoap.getProperty("calificacion").toString()));
                    calificacion.setProperty(4, objSoap.getProperty("activo").toString());
                    calificaciones.add(calificacion);
                } catch (SoapFault e1) {
                    Log.e("Error SoapFault", e.toString());
                    result = false;
                }
            }
            return result;
        }

        protected void onPostExecute(Boolean result) {

            if (result) {
                final String[] datos = new String[calificaciones.size()];
                for (int i = 0; i < calificaciones.size(); i++) {
                    datos[i] = calificaciones.get(i).getProperty(0) + " - "
                            + calificaciones.get(i).getProperty(1)+ " - "
                            + calificaciones.get(i).getProperty(2)+ " - "
                            + calificaciones.get(i).getProperty(3)+ " - "
                            + calificaciones.get(i).getProperty(4);
                }

                ArrayAdapter<String> adaptador = new ArrayAdapter<String>(
                        ListCalificacionCeneval.this,
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

            final String METHOD_NAME = "removeCalificacionCeneval";
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






