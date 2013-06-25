package felipillocrew.app.micard.utils;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class MyPagerAdapter extends PagerAdapter{
	
	@SuppressWarnings("unused")
	private Context context;
	@SuppressWarnings("unused")
	private ViewPager pager;
	private ArrayList<View> views;
	
	public  MyPagerAdapter(Context context, ViewPager pager, ArrayList<View> lista) {
		this.pager = pager;
		this.context = context;
		this.views = lista;
	}
	
	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == (View) arg1;
	}
	
	@Override
	public Object instantiateItem(View container, int position) {
		View v = views.get(position);
		((ViewPager) container).addView(v);
		return v;
	}
	
	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView((View) arg2);
	}
	

}
