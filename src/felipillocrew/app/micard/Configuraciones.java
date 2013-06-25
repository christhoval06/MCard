package felipillocrew.app.micard;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class Configuraciones extends PreferenceActivity {
	
	private static int prefs=R.xml.config;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.setTitle("Configuraciones");
		super.onCreate(savedInstanceState);
		try {
            getClass().getMethod("getFragmentManager");
            AddResourceApi11AndGreater();
        } catch (NoSuchMethodException e) { //Api < 11
            AddResourceApiLessThan11();
        }
	}
	
	
	@SuppressWarnings("deprecation")
    protected void AddResourceApiLessThan11()
    {
        addPreferencesFromResource(prefs);
    }
	
	@TargetApi(11)
    protected void AddResourceApi11AndGreater()
    {
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PF()).commit();
    }
	
	 @TargetApi(11)
	    public static class PF extends PreferenceFragment
	    {       
	        @Override
	        public void onCreate(final Bundle savedInstanceState)
	        {
	            super.onCreate(savedInstanceState);
	            addPreferencesFromResource(Configuraciones.prefs); //outer class private members seem to be visible for inner class, and making it static made things so much easier
	        }
	    }
}
