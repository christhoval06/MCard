package felipillocrew.app.micard.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class TarjetaItem {
    private String id;
    private String nombre;
    private String fecha;
    private String tarjeta;
    private String balance;
    private String imagen;
     
    public TarjetaItem(JSONObject data) {
        try {
			this.id = data.getString("id");
			this.nombre= data.getString("nombre");
			this.tarjeta= data.getString("tarjeta");
			this.fecha=data.getString("fecha");
			this.balance=data.getString("balance");
			this.imagen=data.has("imagen") ? data.getString("imagen"): null;
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }

    public String getid() {
        return id;
    }
    public String getbalance() {
        return balance;
    }
    public String getfecha() {
        return fecha;
    }
    public String gettarjeta() {
        return tarjeta;
    }
    public String getImagen() {
    	return imagen;
    }
    public String getnombre() {
        return nombre;
    }

    
    
    public void setid(String id) {
    	this.id = id;
    }
    public void setbalance( String balance) {
    	this.balance = balance;
    }
    public void setfecha( String fecha) {
    	this.fecha= fecha;
    }
    public void settarjeta( String tarjeta) {
    	this.tarjeta= tarjeta;
    }
    public void setnombre(String nombre) {
        this.nombre = nombre;
    }
    
    @Override
    public String toString() {
    	return getnombre() + " "+ gettarjeta() + " " + getfecha();
    }  
    
}
