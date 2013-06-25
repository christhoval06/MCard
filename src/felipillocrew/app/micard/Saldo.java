package felipillocrew.app.micard;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

import felipillocrew.app.micard.utils.DB;

public class Saldo extends SherlockActivity {
	String codigo, saldo, nombre, fecha, image_ruta, accion;
	DB db;

	TextView tarjeta_tv, saldo_tv, fecha_tv;

	SharedPreferences configs = null;
	Button guardar, cancelar;
	public static final int guardar_id = R.id.guardar,
			cancelar_id = R.id.cancelar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.saldo);

		Bundle b = getIntent().getExtras();
		if (b != null) {
			this.codigo = b.containsKey(NuevaTarjeta.CODIGO) ? b.getString(NuevaTarjeta.CODIGO) : null;
			this.fecha = b.containsKey(NuevaTarjeta.FECHA) ? b.getString(NuevaTarjeta.FECHA) : null;
			this.saldo = b.containsKey(NuevaTarjeta.SALDO) ? b.getString(NuevaTarjeta.SALDO) : null;
			this.accion = b.containsKey(NuevaTarjeta.ACCION) ? b.getString(NuevaTarjeta.ACCION) : null;
		}
		db = new DB(this);

		super.setTitle("Consulta de Saldo");
		configs = PreferenceManager.getDefaultSharedPreferences(this);

		tarjeta_tv = (TextView) findViewById(R.id.tarjeta);
		saldo_tv = (TextView) findViewById(R.id.saldo);
		fecha_tv = (TextView) findViewById(R.id.fecha);
		
		tarjeta_tv.setText(codigo);
		saldo_tv.setText("$" + saldo);
		fecha_tv.setText(fecha);

		guardar = (Button) findViewById(guardar_id);
		cancelar = (Button) findViewById(cancelar_id);
		
		
		guardar.setOnClickListener(acciones_botones);
		cancelar.setOnClickListener(acciones_botones);
		
		guardarTarjeta();

	}
	

	View.OnClickListener acciones_botones = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case guardar_id:
				nuevaTarjeta();
				break;
			case cancelar_id:
				finish();
				break;
			default:
				break;
			}
		}
	};
	
	
	
	protected void guardarTarjeta() {
		if(!db.existeTarjeta(codigo)){
			guardar.setEnabled(configs.getBoolean("tarjetas_add", false));
			if(configs.getBoolean("tarjeta_autosave", false)) db.saveTarjeta(configs.getString("tarjeta_default", "Nueva Tarjeta"), codigo, fecha, saldo);
		}else{
			db.updateTarjeta(saldo, fecha, codigo);
		}
	}
	
	private void nuevaTarjeta(){
		Intent intent = new Intent(this, NuevaTarjeta.class);
		Bundle b = new Bundle();
		b.putString(NuevaTarjeta.CODIGO, codigo);
		b.putString(NuevaTarjeta.FECHA, fecha);
		b.putString(NuevaTarjeta.SALDO, saldo);
		b.putString(NuevaTarjeta.ACCION, "add");
		intent.putExtras(b);
		startActivityForResult(intent, ConsultarSaldo.NUEVA_TARJETA);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) return;

		    switch (requestCode) {
		    	case ConsultarSaldo.NUEVA_TARJETA:
		    		Bundle extas = data.getExtras();
		    		if(extas != null){
		    			if(extas.containsKey("success")){
		    				if(extas.getBoolean("success"))finish();
		    			}
		    		}
		    }
	}
	

}
