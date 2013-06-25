package felipillocrew.app.micard;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import felipillocrew.app.micard.utils.DB;
import felipillocrew.app.micard.utils.Dialogs;
import felipillocrew.app.micard.utils.JSONListener;
import felipillocrew.app.micard.utils.JSONParams;
import felipillocrew.app.micard.utils.POSTclient;

public class ConsultarSaldo {
	
	public static final int NUEVA_TARJETA = 12;

	private String codigo,saldo, fecha;
	private Activity act;
	private String url = "http://200.46.245.230:8080/PortalCAE-WAR-MODULE/SesionPortalServlet";
	private POSTclient sendCodigo;
	private JSONParams params;
	
	private Dialogs dialogos;
	private DB db;
	SharedPreferences configs = null;

	public ConsultarSaldo(String codigo, Activity act) {
		this.codigo = codigo;
		this.act = act;
		this.dialogos = new Dialogs(this.act);
		db = new DB(this.act);
		configs = PreferenceManager.getDefaultSharedPreferences(act);
	}
	
	public ConsultarSaldo(Activity act) {
		this.codigo = null;
		this.act = act;
		this.dialogos = new Dialogs(this.act);
		db = new DB(this.act);
		configs = PreferenceManager.getDefaultSharedPreferences(act);
	}

	public void enviarCodigo(String codigo) {
		this.codigo = codigo;
		enviarCodigo();
	}
	
	public void  enviarCodigo() {
		sendCodigo = new POSTclient(act, listener);
		ArrayList<NameValuePair> val = new ArrayList<NameValuePair>();
		val.add(new BasicNameValuePair("accion", "6"));
		val.add(new BasicNameValuePair("NumDistribuidor", "99"));
		val.add(new BasicNameValuePair("NomUsuario", "usuInternet"));
		val.add(new BasicNameValuePair("NomHost", "AFT"));
		val.add(new BasicNameValuePair("NomDominio", "aft.cl"));
		val.add(new BasicNameValuePair("Trx", ""));
		val.add(new BasicNameValuePair("RutUsuario", "0"));
		val.add(new BasicNameValuePair("NumTarjeta", codigo));
		val.add(new BasicNameValuePair("bloqueable", "0"));
		params = new JSONParams(url, val, "html");
		sendCodigo.execute(params);
	}

	private JSONListener listener = new JSONListener() {

		@Override
		public void onRemoteCallComplete(JSONObject json) {
			try {
				if(json.getBoolean("success")){
					String html = json.getString("html");
					String[] html_ = html.split("<td bgcolor=\"#F15B22\" class=\"verdanabold-ckc\">");
					if( html_.length >2){
						saldo = html_[1].replace("</td>", "").replace("<td bgcolor=\"#FFFFFF\" class=\"verdanabold-ckc\">", "").replace("Fecha saldo:", "").trim();
						fecha = html_[2].split("</td>")[0];
						if(configs.getBoolean("saldo_vista", true)) dialogos.alert("SALDO", "su balance es de: " + saldo, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
								guardarTarjeta(codigo, fecha, saldo);
								
							}
						});
						else showSaldo(codigo, fecha, saldo);
					}else{
						dialogos.alert("Error", "Tarjeta Nº " + codigo + " no es valida", null);
					}
				}
			} catch(JSONException e){
				e.printStackTrace();
			}
		}
	};

	protected void guardarTarjeta(String codigo, String fecha, String saldo) {
		if(!db.existeTarjeta(codigo)){
			if(configs.getBoolean("tarjetas_add", false)) nuevaTarjeta(codigo, fecha, saldo);
			if(configs.getBoolean("tarjeta_autosave", false)) db.saveTarjeta(configs.getString("tarjeta_default", "Nueva Tarjeta"), codigo, fecha, saldo);
		}else{
			db.updateTarjeta(saldo, fecha, codigo);
		}
	}
	
	protected void showSaldo(String codigo, String fecha, String saldo) {
		Intent intent = new Intent(act, Saldo.class);
		Bundle b = new Bundle();
		b.putString(NuevaTarjeta.CODIGO, codigo);
		b.putString(NuevaTarjeta.FECHA, fecha);
		b.putString(NuevaTarjeta.SALDO, saldo);
		b.putString(NuevaTarjeta.ACCION, "add");
		intent.putExtras(b);
		this.act.startActivity(intent);
		
	}

	public JSONObject tarjetas() {
		if(db.haveTarjetas()){
			return db.getTarjetas();
		}
		else return null;
	}
	
	private void nuevaTarjeta(String codigo, String fecha, String saldo){
		Intent intent = new Intent("felipillocrew.app.micard.NUEVA");
		Bundle b = new Bundle();
		b.putString(NuevaTarjeta.CODIGO, codigo);
		b.putString(NuevaTarjeta.FECHA, fecha);
		b.putString(NuevaTarjeta.SALDO, saldo);
		b.putString(NuevaTarjeta.ACCION, "add");
		intent.putExtras(b);
		this.act.startActivityForResult(intent, NUEVA_TARJETA);
	}
	
	public boolean saveImage( String path, String tarjeta){
		return db.saveImage( tarjeta, path);
	}
	
	public boolean borrar(String tarjeta) {
		return db.deleteTarjeta(tarjeta);
		
	}

}
