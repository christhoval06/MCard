package felipillocrew.app.micard.utils;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DB {
	
	public static final int  VERSION_DB = 1;
	private Context	nContext;
	private String dbname = "metroDB";
	SQLiteDatabase db = null;
	
	private TarjetasSQLiteHelper tarjeta;
	
	public DB(Context context) {
		this.nContext = context;
		this.tarjeta = new TarjetasSQLiteHelper(nContext, dbname, null, VERSION_DB);
		this.db = this.tarjeta.getWritableDatabase();
		if(this.db != null){}
	}
	
	public SQLiteDatabase db() {
		return this.db;		
	}
	
	public String name() {
		return this.dbname;
	}
	
	public int version() {
		return DB.VERSION_DB;
	}
	
	public JSONObject sql2JSON(String sql, String items) {
		Cursor c = this.db.rawQuery(sql, null);
    	if(c != null){
    		 if  (c.moveToFirst()) {
    			 JSONObject json = new JSONObject();
    			 JSONArray data = new JSONArray();
    			 try {
					json.put(items, data);
	                  do {
	                	  JSONObject j = new JSONObject();
	                	  for (String item : c.getColumnNames()){
	                		  j.put(c.getColumnName(c.getColumnIndex(item)),c.getString(c.getColumnIndex(item)));
	                	  }
	                	  data.put(j);
	                  }while (c.moveToNext());
    			 } catch (JSONException e) {
						e.printStackTrace();
						return null;
						}
                  return json;
            } 
    	}
    	return null;
		
	}
	
	public boolean save(String tabla, JSONObject json){
    	long id=0;
    	ContentValues cv = new ContentValues();
    	try {
	    	@SuppressWarnings("unchecked")
			Iterator<Object> keys = json.keys();
	    	while(keys.hasNext()) {
	    		String key = (String) keys.next();
	    		cv.put(key, json.getString(key));
	    	}
    	} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		id = db.insert(tabla, null, cv);
		if(id != -1 && id>0){
			return true;
		}
		return false;
    }
	
	
	/****************TARJETAS*************************/
	
	 public boolean haveTarjetas(){
		 return tarjeta.haveTarjetas(db);
	 }
	 
	 public boolean existeTarjeta(String codigo){
		 return tarjeta.existeTarjeta(db, codigo);
	 }
	 
	 public boolean saveTarjeta(JSONObject json){
		 return tarjeta.save(db, json);
	 }
	 
	 public boolean saveTarjeta(String nombre, String tarjeta, String fecha, String balance){
		 return this.tarjeta.save(db, nombre, tarjeta, fecha, balance);
	 }
	 
	 public boolean deleteTarjetas() {
		 return tarjeta.deleteTarjeta(db);
	 }
	 
	 public boolean deleteTarjeta(String tarjeta) {
		 return this.tarjeta.deleteTarjeta(db, tarjeta);
	 }
	 
	 public boolean updateTarjeta(String saldo, String fecha, String tarjeta) {
		 return this.tarjeta.update(db, saldo, fecha, tarjeta);
	 }
	 
	 public JSONObject getTarjetas(){
		 return tarjeta.getTarjetas(db);
	 }

	public boolean saveImage(String tarjeta, String path) {
		return this.tarjeta.saveImagen(db, tarjeta, path);
	}

	public boolean saveTarjeta(String nombre, String codigo, String fecha, String balance, String image_ruta) {
		Log.v("DB", "save");
		 return this.tarjeta.save(db, nombre, codigo, fecha, balance, image_ruta);
		
	}
	
	public boolean updateTarjeta(String nombre, String saldo, String fecha, String tarjeta, String image_ruta) {
		 return this.tarjeta.update(db, nombre, saldo, fecha, tarjeta, image_ruta);
	 }
	 
	
}
