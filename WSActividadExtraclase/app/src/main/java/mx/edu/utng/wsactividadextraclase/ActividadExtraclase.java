package mx.edu.utng.wsactividadextraclase;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by Toshiba on 30/03/2017.
 */
public class ActividadExtraclase implements KvmSerializable{

    private int id;
    private String cveActividadExtraclase;
    private String description;
    private String lugar;
    private String totalHoras;
    private String tipoActividad;

    public ActividadExtraclase(int id, String cveActividadExtraclase, String description, String lugar, String totalHoras, String tipoActividad) {
        this.id = id;
        this.cveActividadExtraclase = cveActividadExtraclase;
        this.description = description;
        this.lugar = lugar;
        this.totalHoras = totalHoras;
        this.tipoActividad = tipoActividad;
    }

    public ActividadExtraclase(){
        this(0,"","","","","");
    }


    @Override
    public Object getProperty(int i) {
        switch (i){
            case 0:
                return id;
            case 1:
                return cveActividadExtraclase;
            case 2:
                return description;
            case 3:
                return lugar;
            case 4:
                return totalHoras;
            case 5:
                return tipoActividad;
        }
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 6;
    }

    @Override
    public void setProperty(int i, Object o) {
        switch (i){
            case 0:
                id = Integer.parseInt(o.toString());
                break;
            case 1:
                cveActividadExtraclase = o.toString();
                break;
            case 2:
                description = o.toString();
                break;
            case 3:
                lugar = o.toString();
                break;
            case 4:
                totalHoras = o.toString();
                break;
            case 5:
                tipoActividad = o.toString();
        }

    }


    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
        switch (i){
            case 0:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "cve_actividad_extraclase";
                break;
            case 1:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "description";
                break;
            case 2:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "lugar";
                break;
            case 3:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "total_horas";
                break;
            case 4:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "tipo_actividad";
                break;
        }
    }
}



