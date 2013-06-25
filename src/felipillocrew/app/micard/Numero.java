package felipillocrew.app.micard;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.viewpagerindicator.LinePageIndicator;

import felipillocrew.app.micard.utils.MyPagerAdapter;
import felipillocrew.app.micard.utils.ZoomOutPageTransformer;

public class Numero extends SherlockFragment {

	private View view;
	private ImageButton enviar;
	private EditText numero;
	private ConsultarSaldo cs;
	
	private PagerAdapter mPagerAdapter;
	private LinePageIndicator mIndicator;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.numero, container, false);
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		cs = new ConsultarSaldo(getSherlockActivity());
		this.enviar = (ImageButton) view.findViewById(R.id.enviar);
		this.numero = (EditText) view.findViewById(R.id.numero);
		enviar.setOnClickListener(buscar);
		this.numero.setOnEditorActionListener(enviar_listener);

		
		 ViewPager myPager = (ViewPager) view.findViewById(R.id.pager);
		 
		 ArrayList<View> vistas = new ArrayList<View>();
		 vistas.add(getSherlockActivity().getLayoutInflater().inflate(R.layout.numero_step_1, null));
		 vistas.add(getSherlockActivity().getLayoutInflater().inflate(R.layout.numero_step_2, null));
		 vistas.add(getSherlockActivity().getLayoutInflater().inflate(R.layout.numero_step_3, null));
		 
		 mPagerAdapter= new MyPagerAdapter(getSherlockActivity().getApplicationContext(),myPager,vistas);
		 myPager.setAdapter(mPagerAdapter);
		 myPager.setPageTransformer(true,new ZoomOutPageTransformer());
		 myPager.setCurrentItem(0);
		 
		 mIndicator = (LinePageIndicator) view.findViewById(R.id.indicator);
		 mIndicator.setViewPager(myPager);
		 

	}

	OnEditorActionListener enviar_listener = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_SEARCH) {
				cs.enviarCodigo(numero.getText().toString());
				return true;
			}
			return false;
		}
	};

	View.OnClickListener buscar = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (enviar.getId() == ((ImageButton) v).getId()) {
				cs.enviarCodigo(numero.getText().toString());
			}
		}
	};

	public static Bundle createBundle(JSONObject data) {
		Bundle bundle = new Bundle();
		@SuppressWarnings("unchecked")
		Iterator<Object> keys = data.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			try {
				bundle.putString(key, data.getString(key));
			} catch (JSONException e) {
				bundle.putString(key, null);
				e.printStackTrace();
			}
		}
		return bundle;
	}

	public static Bundle createBundle(String nombre, String telefono) {
		Bundle bundle = new Bundle();
		bundle.putString("EXTRA_NOMBRE", nombre);
		bundle.putString("EXTRA_TELEFONO", telefono);
		return bundle;
	}

}
