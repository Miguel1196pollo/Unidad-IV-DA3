package mx.edu.utng.wsmovie;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by Toshiba on 29/03/2017.
 */
public class Movie implements KvmSerializable {

    private int id;
    private String name;
    private String sinopsis;
    private float price;
    private int type;

    public Movie(int id, String name, String sinopsis, float price, int type) {
        this.id = id;
        this.name = name;
        this.sinopsis = sinopsis;
        this.price = price;
        this.type = type;
    }

    public Movie() {
        this(0,"","",0.0f,0);
    }

    @Override
    public Object getProperty(int i) {
        switch (i) {
            case 0:
                return id;
            case 1:
                return name;
            case 2:
                return sinopsis;
            case 3:
                return price;
            case 4:
                return type;
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
                name = o.toString();
                break;
            case 2:
                sinopsis = o.toString();
                break;
            case 3:
                price = Float.parseFloat(o.toString());
                break;
            case 4:
                type = Integer.parseInt(o.toString());
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
                propertyInfo.name = "name";
                break;
            case 2:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "sinopsis";
                break;
            case 3:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "price";
                break;
            case 4:
                propertyInfo.type = Float.class;
                propertyInfo.name = "type";
                break;
            default:
                break;
        }


    }
}