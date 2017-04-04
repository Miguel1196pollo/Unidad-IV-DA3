package mx.edu.utng.wscalificacionceneval;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by Toshiba on 04/04/2017.
 */
public class CalificacionCeneval implements KvmSerializable {

    private int id;
    private String nombreAlumno;
    private String fechaEdicion;
    private float calificacion;
    private String activo;

    public CalificacionCeneval(int id, String nombreAlumno, String fechaEdicion, float calificacion, String activo) {
        this.id = id;
        this.nombreAlumno = nombreAlumno;
        this.fechaEdicion = fechaEdicion;
        this.calificacion = calificacion;
        this.activo = activo;
    }

    public CalificacionCeneval() {
        this(0,"","",0.0f,"");
    }

    @Override
    public Object getProperty(int i) {
        switch (i) {
            case 0:
                return id;
            case 1:
                return nombreAlumno;
            case 2:
                return fechaEdicion;
            case 3:
                return calificacion;
            case 4:
                return activo;
        }

        return  null;
    }

    @Override
    public int getPropertyCount() {
        return 5;
    }

    @Override
    public void setProperty(int i, Object o) {
        switch (i){
            case 0:
                id =Integer.parseInt(o.toString());
                break;
            case 1:
                nombreAlumno = o.toString();
                break;
            case 2:
                fechaEdicion = o.toString();
                break;
            case 3:
                calificacion = Float.parseFloat(o.toString());
                break;
            case 4:
                activo = o.toString();
                break;
            default:
                break;
        }
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
        switch (i) {
            case 0:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "id";
                break;
            case 1:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "nombreAlumno";
                break;
            case 2:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "fechaEdicion";
                break;

            case 3:
                propertyInfo.type = Float.class;
                propertyInfo.name = "calificacion";
                break;

            case 4:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "activo";
                break;
            default:
                break;
        }


    }
}
