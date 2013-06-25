package felipillocrew.app.micard;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

import felipillocrew.app.micard.utils.CropOption;
import felipillocrew.app.micard.utils.CropOptionAdapter;
import felipillocrew.app.micard.utils.DB;

public class NuevaTarjeta extends SherlockActivity {

	public static final String CODIGO = "codigo", FECHA = "fecha",
			SALDO = "saldo", NOMBRE = "nombre", ACCION = "accion";
	String codigo, saldo, nombre, fecha, image_ruta, accion;
	Uri mImageCaptureUri;
	DB db;

	TextView tarjeta_tv, ruta_imagen;
	EditText nombre_et;
	ImageView image;

	Button guardar, cancelar;
	public static final int guardar_id = R.id.guardar,
			cancelar_id = R.id.cancelar, image_id = R.id.image;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tarjeta);

		Bundle b = getIntent().getExtras();
		if (b != null) {
			this.codigo = b.containsKey(CODIGO) ? b.getString(CODIGO) : null;
			this.fecha = b.containsKey(FECHA) ? b.getString(FECHA) : null;
			this.saldo = b.containsKey(SALDO) ? b.getString(SALDO) : null;
			this.accion = b.containsKey(ACCION) ? b.getString(ACCION) : null;
		}
		
		db = new DB(this);

		super.setTitle(codigo);

		tarjeta_tv = (TextView) findViewById(R.id.tarjeta);
		ruta_imagen = (TextView) findViewById(R.id.rutaimagen);
		nombre_et = (EditText) findViewById(R.id.nombre);
		image = (ImageView) findViewById(image_id);

		tarjeta_tv.setText("Nº " + codigo);

		guardar = (Button) findViewById(guardar_id);
		cancelar = (Button) findViewById(cancelar_id);

		image.setOnClickListener(acciones_botones);
		guardar.setOnClickListener(acciones_botones);
		cancelar.setOnClickListener(acciones_botones);

	}

	View.OnClickListener acciones_botones = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case guardar_id:
				guardarTarjeta();
				break;
			case cancelar_id:
				finalizar(false);
				break;
			case image_id:
				Dialogo().show();
				break;

			default:
				break;
			}
		}
	};

	public AlertDialog Dialogo() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.select_dialog_item, DataBase.items);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Seleccione una imagen");
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) { // pick from
																	// camera
				if (item == 0) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

					createFileImage();
					if (mImageCaptureUri != null) {
						intent.putExtra(MediaStore.EXTRA_OUTPUT,
								mImageCaptureUri);
						try {
							intent.putExtra("return-data", true);

							startActivityForResult(intent,
									DataBase.PICK_FROM_CAMERA);
						} catch (ActivityNotFoundException e) {
							e.printStackTrace();
						}
					}
				} else { // pick from file
					Intent intent = new Intent();

					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);

					startActivityForResult(
							Intent.createChooser(intent, "Completar usando?"),
							DataBase.PICK_FROM_FILE);
				}
			}
		});

		return builder.create();
	}

	protected void guardarTarjeta() {
		boolean success= false;
		if(accion.equalsIgnoreCase("add")) success = db.saveTarjeta(nombre_et.getText().toString(), codigo, fecha, saldo,image_ruta);
		else if(accion.equalsIgnoreCase("save")) success = db.updateTarjeta(nombre_et.getText().toString(), codigo, fecha, saldo,image_ruta);
		finalizar(success);
	}

	private void finalizar(boolean stats) {
		Intent data = new Intent();
		data.putExtra("success", stats );
		if (getParent() == null) {
			 setResult(RESULT_OK, data);
        }
        else {
            getParent().setResult(RESULT_OK, data);
        }
    	finish();
	}

	protected void createFileImage() {
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ File.separatorChar
				+ "Android/data/"
				+ this.getPackageName()
				+ "/files/"
				+ String.valueOf(System.currentTimeMillis()) + ".jpg";
		File _photoFile = new File(path);
		try {
			if (_photoFile.exists() == false) {
				_photoFile.getParentFile().mkdirs();
				_photoFile.createNewFile();
			}

		} catch (IOException e) {
			Log.e("DB", "Could not create file.", e);
		}
		mImageCaptureUri = Uri.fromFile(_photoFile);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK)
			return;

		switch (requestCode) {
		case DataBase.PICK_FROM_CAMERA:
			doCrop();

			break;

		case DataBase.PICK_FROM_FILE:
			mImageCaptureUri = data.getData();

			doCrop();
			break;

		case DataBase.CROP_FROM_CAMERA:
			Bundle extras = data.getExtras();

			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				image.setImageBitmap(photo);

				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.JPEG, 85, bytes);

				File f = new File(mImageCaptureUri.getPath());
				if (f.exists())
					f.delete();
				else {
					createFileImage();
					f = new File(mImageCaptureUri.getPath());
				}
				try {
					if (f.exists())
						f.delete();
					if (f.createNewFile()) {
						FileOutputStream fo = new FileOutputStream(f);
						fo.write(bytes.toByteArray());
						fo.close();
						image_ruta = mImageCaptureUri.getPath();
						ruta_imagen.setText(image_ruta);

					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			break;

		}
	}

	public void doCrop() {
		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");

		List<ResolveInfo> list = getPackageManager().queryIntentActivities(
				intent, 0);

		int size = list.size();

		if (size == 0) {
			Toast.makeText(this, "Can not find image crop app",
					Toast.LENGTH_SHORT).show();

			return;
		} else {
			intent.setData(mImageCaptureUri);

			intent.putExtra("outputX", 200);
			intent.putExtra("outputY", 200);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", true);

			if (size == 1) {
				Intent i = new Intent(intent);
				ResolveInfo res = list.get(0);

				i.setComponent(new ComponentName(res.activityInfo.packageName,
						res.activityInfo.name));

				startActivityForResult(i, DataBase.CROP_FROM_CAMERA);
			} else {
				for (ResolveInfo res : list) {
					final CropOption co = new CropOption();

					co.title = getPackageManager().getApplicationLabel(
							res.activityInfo.applicationInfo);
					co.icon = getPackageManager().getApplicationIcon(
							res.activityInfo.applicationInfo);
					co.appIntent = new Intent(intent);

					co.appIntent
							.setComponent(new ComponentName(
									res.activityInfo.packageName,
									res.activityInfo.name));

					cropOptions.add(co);
				}

				CropOptionAdapter adapter = new CropOptionAdapter(
						getApplicationContext(), cropOptions);

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Seleccione Recorte:");
				builder.setAdapter(adapter,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int item) {
								startActivityForResult(
										cropOptions.get(item).appIntent,
										DataBase.CROP_FROM_CAMERA);
							}
						});

				builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {

						if (mImageCaptureUri != null) {
							getContentResolver().delete(mImageCaptureUri, null,
									null);
							mImageCaptureUri = null;
						}
					}
				});

				AlertDialog alert = builder.create();

				alert.show();
			}
		}
	}

}
