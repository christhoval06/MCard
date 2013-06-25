package felipillocrew.app.micard;

import static com.nineoldandroids.view.ViewHelper.setAlpha;
import static com.nineoldandroids.view.ViewHelper.setTranslationX;
import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;

import felipillocrew.app.micard.utils.CropOption;
import felipillocrew.app.micard.utils.CropOptionAdapter;
import felipillocrew.app.micard.utils.Dialogs;
import felipillocrew.app.micard.utils.TarjetaItem;

public class DataBase extends SherlockListFragment {

	private List<TarjetaItem> rowItems;
	private TarjetasAdapter adapter = null;
	private ConsultarSaldo cs;
	public Uri mImageCaptureUri = null;
	public static final String[] items = new String[] { "Camara", "Galeria" };

	public static final int PICK_FROM_CAMERA = 1;
	public static final int CROP_FROM_CAMERA = 2;
	public static final int PICK_FROM_FILE = 3;
	public static final int NUEVA_TARJETA = 12;

	public ImageView clicked;
	public String clicked_item_id;

	SwipeListViewTouchListener touchListener;
	private Dialogs dialogos;

	public interface OnSwipeCallback {
		void onSwipeLeft(ListView listView, int[] reverseSortedPositions);

		void onSwipeRight(ListView listView, int[] reverseSortedPositions);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null)
			return null;
		View view = inflater.inflate(R.layout.db, container, false);
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cs = new ConsultarSaldo(getSherlockActivity());
		dialogos = new Dialogs(getSherlockActivity());
		rowItems = new ArrayList<TarjetaItem>();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(getView());
		buscarTarjetas();

		touchListener = new SwipeListViewTouchListener(getListView(),
				new OnSwipeCallback() {
					@Override
					public void onSwipeLeft(ListView listView,
							int[] reverseSortedPositions) {
						YourSlideRightToLeft(listView, reverseSortedPositions[0]);
					}

					@Override
					public void onSwipeRight(ListView listView,
							int[] reverseSortedPositions) {
						YourSlideLeftToRight(listView, reverseSortedPositions[0]);
					}
				}, false, false, false, true);
		
		getListView().setOnTouchListener(touchListener);
		getListView().setOnScrollListener(touchListener.makeScrollListener());
	}

	private void buscarTarjetas() {
		JSONObject json = cs.tarjetas();
		if (json != null) {
			try {
				if (json.has("tarjetas")) {

					if (adapter != null) {
						adapter = null;
					}

					JSONArray pac = json.getJSONArray("tarjetas");

					for (int i = 0; i < pac.length(); i++) {
						JSONObject e = pac.getJSONObject(i);
						TarjetaItem item = new TarjetaItem(e);
						rowItems.add(item);
					}
					if (adapter == null) {
						adapter = new TarjetasAdapter();
						getListView().setFastScrollEnabled(true);
						setListAdapter(adapter);
					}
				}
			} catch (JSONException e) {
				Log.e("log_tag", "Error parsing data " + e.toString());
			}
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		TarjetaItem item = (TarjetaItem) getListView().getItemAtPosition(
				position);
		cs.enviarCodigo(item.gettarjeta());
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

	public class TarjetasAdapter extends BaseAdapter implements SectionIndexer {
		String[] sections;
		HashMap<String, Integer> alphaIndexer;

		private static final int VIEW_TYPE_GROUP_START = 0;
		private static final int VIEW_TYPE_GROUP_CONT = 1;
		private static final int VIEW_TYPE_COUNT = 2;

		public TarjetasAdapter() {
			Collections.sort(rowItems, new Comparator<TarjetaItem>() {

				@Override
				public int compare(TarjetaItem lhs, TarjetaItem rhs) {
					String nombre1 = lhs.getnombre();
					String nombre2 = rhs.getnombre();

					int orden = nombre1.compareToIgnoreCase(nombre2);
					if (orden > 0) {
						return 1;
					} else if (orden < 0) {
						return -1;
					} else {
						return 0;
					}
				}
			});

			alphaIndexer = new HashMap<String, Integer>();
			int size = rowItems.size();
			for (int i = size - 1; i >= 0; i--) {
				String element = rowItems.get(i).getnombre();
				alphaIndexer.put(element.substring(0, 1), i);
			}

			Set<String> keys = alphaIndexer.keySet();
			Iterator<String> it = keys.iterator();
			ArrayList<String> keyList = new ArrayList<String>();

			while (it.hasNext()) {
				keyList.add(it.next());
			}

			Collections.sort(keyList);

			sections = new String[keyList.size()];
			keyList.toArray(sections);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			// return data.size();
			return rowItems.size();
		}

		@Override
		public int getViewTypeCount() {
			return VIEW_TYPE_COUNT;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0) {
				return VIEW_TYPE_GROUP_START;
			}
			boolean newGroup = isNewGroup(position);
			if (newGroup) {
				return VIEW_TYPE_GROUP_START;
			} else {
				return VIEW_TYPE_GROUP_CONT;
			}
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			// return position;
			return rowItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			// return position;
			return rowItems.indexOf(getItem(position));
		}

		private class ViewHolder {
			TextView grupo = null;
			ImageView icon;
			TextView nombre;
			TextView tarjeta;
			TextView fecha;
			TextView balance;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			int nViewType = getItemViewType(position);

			if (convertView == null) {
				holder = new ViewHolder();
				if (nViewType == VIEW_TYPE_GROUP_START) {
					convertView = LayoutInflater.from(getSherlockActivity())
							.inflate(R.layout.tarjeta_item_header, null);
				} else {
					convertView = LayoutInflater.from(getSherlockActivity())
							.inflate(R.layout.tarjeta_item, null);
				}

				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder.nombre = (TextView) convertView
						.findViewById(R.id.nombre);
				holder.tarjeta = (TextView) convertView
						.findViewById(R.id.tarjeta);
				holder.fecha = (TextView) convertView.findViewById(R.id.fecha);
				holder.balance = (TextView) convertView
						.findViewById(R.id.balance);
				holder.grupo = (TextView) convertView
						.findViewById(R.id.nombre_grupo);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final TarjetaItem rowItem = (TarjetaItem) getItem(position);

			holder.nombre.setText(rowItem.getnombre());
			holder.tarjeta.setText("# Tarjeta: " + rowItem.gettarjeta());
			holder.fecha.setText("ultimo uso: " + rowItem.getfecha());
			holder.balance.setText("Saldo: $" + rowItem.getbalance());
			Bitmap bmp = loadimagen(rowItem.getImagen());
			if (bmp != null)
				holder.icon.setImageBitmap(bmp);

			holder.icon.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					clicked = (ImageView) view;
					clicked_item_id = rowItem.gettarjeta();
					Dialogo().show();
				}
			});

			if (holder.grupo != null) {
				holder.grupo.setText(rowItem.getnombre().substring(0, 1));
			}
			return convertView;
		}

		@Override
		public int getPositionForSection(int section) {
			String letter = sections[section];
			return alphaIndexer.get(letter);
		}

		@Override
		public int getSectionForPosition(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object[] getSections() {
			return sections;
		}

		private boolean isNewGroup(int position) {
			TarjetaItem prev, actual = rowItems.get(position);
			try {
				prev = rowItems.get(position - 1);
			} catch (NullPointerException e) {
				prev = null;
			}
			if (prev == null) {
				return true;
			}

			int orden = prev.getnombre().substring(0, 1)
					.compareToIgnoreCase(actual.getnombre().substring(0, 1));
			if (orden > 0) {
				return false;
			} else if (orden < 0) {
				return true;
			} else {
				return false;
			}
		}
	}

	public AlertDialog Dialogo() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				getSherlockActivity(), android.R.layout.select_dialog_item,
				items);

		AlertDialog.Builder builder = new AlertDialog.Builder(
				getSherlockActivity());
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

							startActivityForResult(intent, PICK_FROM_CAMERA);
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
							PICK_FROM_FILE);
				}
			}
		});

		return builder.create();
	}

	protected void createFileImage() {
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ File.separatorChar
				+ "Android/data/"
				+ getSherlockActivity().getPackageName()
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

	public void doCrop() {
		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");

		List<ResolveInfo> list = getSherlockActivity().getPackageManager()
				.queryIntentActivities(intent, 0);

		int size = list.size();

		if (size == 0) {
			Toast.makeText(getSherlockActivity(),
					"Can not find image crop app", Toast.LENGTH_SHORT).show();

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

				startActivityForResult(i, CROP_FROM_CAMERA);
			} else {
				for (ResolveInfo res : list) {
					final CropOption co = new CropOption();

					co.title = getSherlockActivity().getPackageManager()
							.getApplicationLabel(
									res.activityInfo.applicationInfo);
					co.icon = getSherlockActivity().getPackageManager()
							.getApplicationIcon(
									res.activityInfo.applicationInfo);
					co.appIntent = new Intent(intent);

					co.appIntent
							.setComponent(new ComponentName(
									res.activityInfo.packageName,
									res.activityInfo.name));

					cropOptions.add(co);
				}

				CropOptionAdapter adapter = new CropOptionAdapter(
						getSherlockActivity().getApplicationContext(),
						cropOptions);

				AlertDialog.Builder builder = new AlertDialog.Builder(
						getSherlockActivity());
				builder.setTitle("Seleccione Recorte:");
				builder.setAdapter(adapter,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int item) {
								startActivityForResult(
										cropOptions.get(item).appIntent,
										CROP_FROM_CAMERA);
							}
						});

				builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {

						if (mImageCaptureUri != null) {
							getSherlockActivity().getContentResolver().delete(
									mImageCaptureUri, null, null);
							mImageCaptureUri = null;
						}
					}
				});

				AlertDialog alert = builder.create();

				alert.show();
			}
		}
	}

	public Bitmap loadimagen(String url) {
		if (url != null) {
			File img = new File(url);
			if (img.exists()) {
				Uri uri = Uri.fromFile(img);
				Bitmap bmp = null;
				try {
					bmp = MediaStore.Images.Media.getBitmap(
							getSherlockActivity().getContentResolver(), uri);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return bmp;
			}
		}
		return null;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK)
			return;

		switch (requestCode) {
		case PICK_FROM_CAMERA:
			doCrop();
			break;

		case PICK_FROM_FILE:
			mImageCaptureUri = data.getData();

			doCrop();
			break;

		case CROP_FROM_CAMERA:
			Bundle extras = data.getExtras();

			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				clicked.setImageBitmap(photo);

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
						cs.saveImage(mImageCaptureUri.getPath(),
								clicked_item_id);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			break;

		}
	}

	public class SwipeListViewTouchListener implements View.OnTouchListener {
		// Cached ViewConfiguration and system-wide constant values
		private int mSlop;
		private int mMinFlingVelocity;
		private int mMaxFlingVelocity;
		private long mAnimationTime;

		// Fixed properties
		private ListView mListView;
		private OnSwipeCallback mCallback;
		private int mViewWidth = 1; // 1 and not 0 to prevent dividing by zero
		private boolean dismissLeft = true;
		private boolean dismissRight = true;

		// Transient properties
		private List<PendingSwipeData> mPendingSwipes = new ArrayList<PendingSwipeData>();
		private int mDismissAnimationRefCount = 0;
		private float mDownX;
		private boolean mSwiping;
		private VelocityTracker mVelocityTracker;
		private int mDownPosition;
		private View mDownView;
		private boolean mPaused;

		private boolean onlyLeft = false;
		private boolean onlyRight = false;

		public SwipeListViewTouchListener(ListView listView,
				OnSwipeCallback callback) {
			ViewConfiguration vc = ViewConfiguration.get(listView.getContext());
			mSlop = vc.getScaledTouchSlop();
			mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
			mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
			mAnimationTime = listView.getContext().getResources()
					.getInteger(android.R.integer.config_shortAnimTime);
			mListView = listView;
			mCallback = callback;
		}

		public SwipeListViewTouchListener(ListView listView,
				OnSwipeCallback callback, boolean dismissLeft,
				boolean dismissRight) {
			this(listView, callback);
			this.dismissLeft = dismissLeft;
			this.dismissRight = dismissRight;
		}

		public SwipeListViewTouchListener(ListView listView,
				OnSwipeCallback callback, boolean dismissLeft,
				boolean dismissRight, boolean onlyLeft, boolean onlyRight) {
			this(listView, callback, dismissLeft, dismissRight);
			this.onlyLeft = onlyLeft;
			this.onlyRight = onlyRight;
		}

		public void setEnabled(boolean enabled) {
			mPaused = !enabled;
		}

		public AbsListView.OnScrollListener makeScrollListener() {
			return new AbsListView.OnScrollListener() {
				@Override
				public void onScrollStateChanged(AbsListView absListView,
						int scrollState) {
					setEnabled(scrollState != AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
				}

				@Override
				public void onScroll(AbsListView absListView, int i, int i1,
						int i2) {
				}
			};
		}

		@SuppressLint("Recycle")
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (mViewWidth < 2) {
				mViewWidth = mListView.getWidth();
			}

			switch (motionEvent.getActionMasked()) {
			case MotionEvent.ACTION_DOWN: {
				if (mPaused) {
					return false;
				}

				// TODO: ensure this is a finger, and set a flag

				// Find the child view that was touched (perform a hit test)
				Rect rect = new Rect();
				int childCount = mListView.getChildCount();
				int[] listViewCoords = new int[2];
				mListView.getLocationOnScreen(listViewCoords);
				int x = (int) motionEvent.getRawX() - listViewCoords[0];
				int y = (int) motionEvent.getRawY() - listViewCoords[1];
				View child;
				for (int i = 0; i < childCount; i++) {
					child = mListView.getChildAt(i);
					child.getHitRect(rect);
					if (rect.contains(x, y)) {
						mDownView = child;
						break;
					}
				}

				if (mDownView != null) {
					mDownX = motionEvent.getRawX();
					mDownPosition = mListView.getPositionForView(mDownView);

					mVelocityTracker = VelocityTracker.obtain();
					mVelocityTracker.addMovement(motionEvent);
				}
				view.onTouchEvent(motionEvent);
				return true;
			}

			case MotionEvent.ACTION_UP: {
				if (mVelocityTracker == null) {
					break;
				}

				float deltaX = motionEvent.getRawX() - mDownX;
				mVelocityTracker.addMovement(motionEvent);
				mVelocityTracker.computeCurrentVelocity(500);
				float velocityX = Math.abs(mVelocityTracker.getXVelocity());
				float velocityY = Math.abs(mVelocityTracker.getYVelocity());
				boolean swipe = false;
				boolean swipeRight = false;

				if (Math.abs(deltaX) > mViewWidth / 2) {
					swipe = true;
					swipeRight = deltaX > 0;
				} else if (mMinFlingVelocity <= velocityX
						&& velocityX <= mMaxFlingVelocity
						&& velocityY < velocityX) {
					swipe = true;
					swipeRight = mVelocityTracker.getXVelocity() > 0;
				}
				if (swipe) {
					// sufficent swipe value
					final View downView = mDownView; // mDownView gets null'd
														// before animation ends
					final int downPosition = mDownPosition;
					final boolean toTheRight = swipeRight;
					++mDismissAnimationRefCount;
					animate(mDownView)
							.translationX(
									swipeRight ? (this.onlyRight ? mViewWidth
											: 0) : (this.onlyLeft ? -mViewWidth
											: 0))
							.alpha(swipeRight ? (this.onlyRight ? 0 : 1f)
									: (this.onlyLeft ? 0 : 1f))
							.setDuration(mAnimationTime)
							.setListener(new AnimatorListenerAdapter() {
								@Override
								public void onAnimationEnd(Animator animation) {
									performSwipeAction(downView, downPosition,
											toTheRight,
											toTheRight ? dismissRight
													: dismissLeft);
								}
							});
				} else {
					// cancel
					animate(mDownView).translationX(0).alpha(1)
							.setDuration(mAnimationTime).setListener(null);
				}
				mVelocityTracker = null;
				mDownX = 0;
				mDownView = null;
				mDownPosition = ListView.INVALID_POSITION;
				mSwiping = false;
				break;
			}

			case MotionEvent.ACTION_MOVE: {
				if (mVelocityTracker == null || mPaused) {
					break;
				}
				boolean swipeRight = false;
				mVelocityTracker.addMovement(motionEvent);
				float deltaX = motionEvent.getRawX() - mDownX;
				if (Math.abs(deltaX) > mSlop) {
					mSwiping = true;
					swipeRight = deltaX > 0;
					mListView.requestDisallowInterceptTouchEvent(true);

					// Cancel ListView's touch (un-highlighting the item)
					MotionEvent cancelEvent = MotionEvent.obtain(motionEvent);
					cancelEvent
							.setAction(MotionEvent.ACTION_CANCEL
									| (motionEvent.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
					mListView.onTouchEvent(cancelEvent);
				}

				if (mSwiping) {
					setTranslationX(mDownView,
							swipeRight ? (this.onlyRight ? deltaX : 0)
									: (this.onlyLeft ? -mViewWidth : 0));
					setAlpha(
							mDownView,
							swipeRight ? (this.onlyRight ? Math.max(
									0f,
									Math.min(1f, 1f - 2f * Math.abs(deltaX)
											/ mViewWidth)) : 1f)
									: (this.onlyLeft ? Math.max(
											0f,
											Math.min(1f,
													1f - 2f * Math.abs(deltaX)
															/ mViewWidth)) : 1f));
					return true;
				}
				break;
			}
			}
			return false;
		}

		class PendingSwipeData implements Comparable<PendingSwipeData> {
			public int position;
			public View view;

			public PendingSwipeData(int position, View view) {
				this.position = position;
				this.view = view;
			}

			@Override
			public int compareTo(PendingSwipeData other) {
				// Sort by descending position
				return other.position - position;
			}
		}

		private void performSwipeAction(final View swipeView,
				final int swipePosition, boolean toTheRight, boolean dismiss) {

			@SuppressWarnings("unused")
			final ViewGroup.LayoutParams lp = swipeView.getLayoutParams();
			final int originalHeight = swipeView.getHeight();
			final boolean swipeRight = toTheRight;
			swipeView.getHeight();
			final SwipeListViewTouchListener self = this;
			ValueAnimator animator;
			if (dismiss)
				animator = ValueAnimator.ofInt(originalHeight, 1).setDuration(
						mAnimationTime);
			else
				animator = ValueAnimator.ofInt(originalHeight,
						originalHeight - 1).setDuration(mAnimationTime);

			animator.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					--mDismissAnimationRefCount;
					if (mDismissAnimationRefCount == 0) {
						// No active animations, process all pending dismisses.
						// Sort by descending position
						Collections.sort(mPendingSwipes);

						int[] swipePositions = new int[mPendingSwipes.size()];
						for (int i = mPendingSwipes.size() - 1; i >= 0; i--) {
							swipePositions[i] = mPendingSwipes.get(i).position;
						}
						if (swipeRight)
							if (self.onlyRight)
								mCallback.onSwipeRight(mListView,
										swipePositions);
							else if (self.onlyLeft)
								mCallback
										.onSwipeLeft(mListView, swipePositions);

						@SuppressWarnings("unused")
						ViewGroup.LayoutParams lp;
						for (PendingSwipeData pendingDismiss : mPendingSwipes) {
							// Reset view presentation
							setAlpha(pendingDismiss.view, 1f);
							setTranslationX(pendingDismiss.view, 0);
						}

						mPendingSwipes.clear();
					}
				}
			});

			animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator valueAnimator) {
				}
			});

			mPendingSwipes.add(new PendingSwipeData(swipePosition, swipeView));
			animator.start();
		}
	}

	// (<-----)
	public void YourSlideRightToLeft(ListView listView, int position) {
	}

	// (----->)
	public void YourSlideLeftToRight(ListView listView,int position) {
		final int _position = position;
		dialogos.notification("", "Desea eliminar a " + rowItems.get(position).getnombre(), "Aceptar", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				cs.borrar(rowItems.get(_position).gettarjeta());
				rowItems.remove(rowItems.get(_position));
				adapter.notifyDataSetChanged();
			}
		});
	}

}
