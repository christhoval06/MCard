package felipillocrew.app.micard.utils;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class TarjetasSQLiteHelper extends SQLiteOpenHelper {

	String sqlCreate = "CREATE TABLE IF NOT EXISTS tarjetas ( id INTEGER primary key autoincrement, nombre TEXT, imagen TEXT, tarjeta TEXT not null, fecha TEXT not null, balance TEXT not null )";
	public final String TABLE = "tarjetas"; 

	public TarjetasSQLiteHelper(Context contexto, String db,
			CursorFactory factory, int version) {
		super(contexto, db, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(sqlCreate);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int versionAnterior,
			int versionNueva) {
		db.execSQL("DROP TABLE IF EXISTS tarjetas");
		db.execSQL(sqlCreate);
	}

	public void create(SQLiteDatabase db) {
		db.execSQL(sqlCreate);
	}

	public boolean save(SQLiteDatabase db, JSONObject json) {
		long id = 0;
		ContentValues cv = new ContentValues();
		try {
			@SuppressWarnings("unchecked")
			Iterator<Object> keys = json.keys();
			while (keys.hasNext()) {
				String key = (String) keys.next();

				cv.put(key, json.getString(key));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		id = db.insert(TABLE, null, cv);
		if (id != -1 && id > 0) {
			return true;
		}
		return false;
	}

	public boolean save(SQLiteDatabase db, String nombre, String tarjeta,
			String fecha, String balance) {
		long id = 0;
		ContentValues cv = new ContentValues();
		cv.put("nombre", nombre);
		cv.put("tarjeta", tarjeta);
		cv.put("fecha", fecha);
		cv.put("balance", balance);
		id = db.insert(TABLE, null, cv);
		if (id != -1 && id > 0) {
			return true;
		}
		return false;
	}

	public boolean haveTarjetas(SQLiteDatabase db) {
		Cursor c = db.rawQuery("SELECT * FROM tarjetas LIMIT 1;", null);
		if (c != null) {
			if (c.moveToFirst()) {
				return true;
			}
		}
		return false;
	}

	public boolean existeTarjeta(SQLiteDatabase db, String codigo) {
		Cursor c = db.rawQuery(
				"SELECT * FROM tarjetas Where tarjeta=? LIMIT 1;",
				new String[] { codigo });
		if (c != null) {
			if (c.moveToFirst()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean update(SQLiteDatabase db, String saldo, String fecha, String tarjeta) {
		ContentValues cv = new ContentValues();
		cv.put("fecha", fecha);
		cv.put("balance", saldo);
		return db.update(TABLE, cv, "tarjeta=?", new String[] { tarjeta }) > 0;
	}

	public boolean deleteTarjeta(SQLiteDatabase db) {
		return db.delete(TABLE, null, null) > 0;
	}
	
	public boolean deleteTarjeta(SQLiteDatabase db, String tarjeta) {
		return db.delete(TABLE, "tarjeta=?", new String[] { tarjeta }) > 0;
	}

	public JSONObject getTarjetas(SQLiteDatabase db) {
		Cursor c = db.rawQuery("SELECT * FROM tarjetas", null);
		if (c != null) {
			if (c.moveToFirst()) {
				JSONObject json = new JSONObject();
				JSONArray data = new JSONArray();
				try {
					json.put(TABLE, data);
					do {
						JSONObject j = new JSONObject();
						for (String item : c.getColumnNames()) {
							j.put(c.getColumnName(c.getColumnIndex(item)),
									c.getString(c.getColumnIndex(item)));
						}
						data.put(j);
					} while (c.moveToNext());
				} catch (JSONException e) {
					e.printStackTrace();
					return null;
				}
				return json;
			}
		}
		return null;
	}
	
	
	public boolean saveImagen(SQLiteDatabase db, String tarjeta, String imagen) {
		ContentValues cv = new ContentValues();
		cv.put("imagen", imagen);
		return db.update(TABLE, cv, "tarjeta=?", new String[] { tarjeta }) > 0;
	}

	public boolean save(SQLiteDatabase db, String nombre, String tarjeta, String fecha, String balance, String image_ruta) {
		long id = 0;
		ContentValues cv = new ContentValues();
		cv.put("nombre", nombre);
		cv.put("tarjeta", tarjeta);
		cv.put("fecha", fecha);
		cv.put("balance", balance);
		cv.put("imagen", image_ruta);
		id = db.insert(TABLE, null, cv);
		if (id != -1 && id > 0) {
			return true;
		}
		return false;
	}

	public boolean update(SQLiteDatabase db, String nombre, String saldo, String fecha, String tarjeta, String image_ruta) {
		ContentValues cv = new ContentValues();
		cv.put("nombre", nombre);
		cv.put("tarjeta", tarjeta);
		cv.put("fecha", fecha);
		cv.put("balance", saldo);
		cv.put("imagen", image_ruta);
		return db.update(TABLE, cv, "tarjeta=?", new String[] { tarjeta }) > 0;
	}
}
