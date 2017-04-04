package mx.edu.utng.wsequipo;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by Toshiba on 31/03/2017.
 */
public class Equipo  implements KvmSerializable{

    private int id;
    private String nombre;
    private String tipoEquipo;
    private String numeroEquipo;
    private String descripcion;
    private String filial;
    private String estatus;


    public Equipo(int id, String nombre, String tipoEquipo, String numeroEquipo, String descripcion, String filial, String estatus) {
        this.id = id;
        this.nombre = nombre;
        this.tipoEquipo = tipoEquipo;
        this.numeroEquipo = numeroEquipo;
        this.descripcion = descripcion;
        this.filial = filial;
        this.estatus = estatus;
    }

    public Equipo(){
        this(0,"","","","","","");
    }

    @Override
    public Object getProperty(int i) {
        switch (1){
            case 0:
                return id;
            case 2:
                return nombre;
            case 3:
                return tipoEquipo;
            case 4:
                return numeroEquipo;
            case 5:
                return descripcion;
            case 6:
                return filial;
            case 7:
                return estatus;
        }
        return null;
    }
    @Override
    public int getPropertyCount() {
        return 7;
    }

    @Override
    public void setProperty(int i, Object o) {
        switch (i){
            case 0:
                id =Integer.parseInt(o.toString());
                break;
            case 1:
                nombre= o.toString();
                break;
            case 2:
                tipoEquipo = o.toString();
                break;
            case 3:
                numeroEquipo = o.toString();
                break;
            case 4:
                descripcion = o.toString();
                break;
            case 5:
                filial = o.toString();
            case 6:
                estatus = o.toString();
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
                propertyInfo.name = "nombre";
                break;
            case 2:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "tipo_equipo";
                break;
            case 3:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "numero_equipo";
                break;
            case 4:
                propertyInfo.type = propertyInfo.STRING_CLASS;
                propertyInfo.name = "descripcion";
            case 5:
                propertyInfo.type = propertyInfo.STRING_CLASS;
                propertyInfo.name = "filial";
            case 6:
                propertyInfo.type = propertyInfo.STRING_CLASS;
                propertyInfo.name = "estatus";
                break;
            default:
                break;
        }


    }
}
