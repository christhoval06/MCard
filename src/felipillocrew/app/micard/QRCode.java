package felipillocrew.app.micard;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;
import com.viewpagerindicator.LinePageIndicator;

import felipillocrew.app.micard.utils.MyPagerAdapter;
import felipillocrew.app.micard.utils.ZoomOutPageTransformer;

public class QRCode extends SherlockFragment {

	public static final String SCAN = "la.droid.qr.scan";
	public static final String COMPLETE = "la.droid.qr.complete";
	public static final String RESULT = "la.droid.qr.result";
	private static final int ACTIVITY_RESULT_QR_DRDROID = 0;
	

	private View view;
	private Button scan;
	public String myCode;
	private ConsultarSaldo cs;
	
	private PagerAdapter mPagerAdapter;
	private LinePageIndicator mIndicator;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.qr, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		this.scan = (Button) view.findViewById(R.id.scan);
		scan.setOnClickListener(open_scaner);

		ViewPager myPager = (ViewPager) view.findViewById(R.id.pager);
		

		 ArrayList<View> vistas = new ArrayList<View>();
		 vistas.add(getSherlockActivity().getLayoutInflater().inflate(R.layout.qr_step_1, null));
		 vistas.add(getSherlockActivity().getLayoutInflater().inflate(R.layout.qr_step_2, null));
		 vistas.add(getSherlockActivity().getLayoutInflater().inflate(R.layout.qr_step_3, null));
		 
		 mPagerAdapter= new MyPagerAdapter(getSherlockActivity().getApplicationContext(),myPager,vistas);
		 myPager.setAdapter(mPagerAdapter);
		 myPager.setPageTransformer(true,new ZoomOutPageTransformer());
		 myPager.setCurrentItem(0);
		  
		mIndicator = (LinePageIndicator) view.findViewById(R.id.indicator);
		mIndicator.setViewPager(myPager);
	}

	View.OnClickListener open_scaner = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (scan.getId() == ((Button) v).getId()) {
				scan();
			}
		}
	};

	private void scan() {
		Intent qrDroid = new Intent(SCAN);
		qrDroid.putExtra(COMPLETE, true);
		
		try {
			startActivityForResult(qrDroid, ACTIVITY_RESULT_QR_DRDROID);
		} catch (ActivityNotFoundException activity) {
			qrDroidRequired(getSherlockActivity());
		}
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if( ACTIVITY_RESULT_QR_DRDROID==requestCode && null!=data && data.getExtras()!=null ) {
			String result = data.getExtras().getString(RESULT);
			myCode = result;
			cs = new ConsultarSaldo(procesarCodigo(myCode), getSherlockActivity());
			cs.enviarCodigo();
		}
	}
	
	public String procesarCodigo(String codigo){
		String code = codigo.substring(3, 13);
		return code;
	}
		
	
	
	 /**
     * Display a message stating that QR Droid is requiered, and lets the user download it for free
     * @param activity
     */
    public static void qrDroidRequired( final Activity activity ) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage( activity.getString(R.string.qrdroid_missing) )
		       .setCancelable(true)
		       .setNegativeButton( activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
		           @Override
				public void onClick(DialogInterface dialog, int id) {
		        	   dialog.cancel();
		           }
		       })
		       .setPositiveButton( activity.getString(R.string.from_market), new DialogInterface.OnClickListener() {
		    	   @Override
				public void onClick(DialogInterface dialog, int id) {
		    		   activity.startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse( activity.getString(R.string.url_market) ) ) );
		           }
		       })
	           .setNeutralButton(activity.getString(R.string.direct), new DialogInterface.OnClickListener() {
	        	   @Override
				public void onClick(DialogInterface dialog, int id) {
	        		   activity.startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse( activity.getString(R.string.url_direct) ) ) );
	        	   }
	           });
		builder.create().show();
    }
		

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
