package felipillocrew.app.micard;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.view.Menu;

import felipillocrew.app.micard.utils.Tabs;

public class Main extends Tabs {
	
	public static int CONFIG = 1;
	public static SharedPreferences mPrefs  = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/* solo en caso de Tabs */
		setContentView(R.layout.main);
		
		this.createTabs();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Menu m = menu;
		m.add(Menu.NONE, CONFIG, Menu.NONE, "Configuración").setIntent(new Intent(this, Configuraciones.class));	
		return super.onCreateOptionsMenu(menu);
	}
	
	
	private void createTabs() {
		addTab("Tarjetas", R.drawable.ic_action_tab_tarjetas_ligth, DataBase.class, DataBase.createBundle("", ""));
		addTab("Número", R.drawable.ic_action_123_dark, Numero.class, Numero.createBundle("", ""));		
		addTab("DM Code", R.drawable.ic_action_qrcode_ligth, QRCode.class, QRCode.createBundle("", ""));
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {   
	        Fragment mFragment = Fragment.instantiate(this, DataBase.class.getName());
	        mFragment.onActivityResult(requestCode, resultCode, data);
	        super.onActivityResult(requestCode, resultCode, data);
	}
		
}
