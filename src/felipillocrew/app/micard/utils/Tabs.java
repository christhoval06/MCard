package felipillocrew.app.micard.utils;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import felipillocrew.app.micard.R;

  
public abstract class Tabs extends SherlockFragmentActivity {
  
    private TabsAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        adapter = new TabsAdapter( this );
        super.onCreate(savedInstanceState);
    }
  
    /**
     * Add a tab with a backing Fragment to the action bar
     * @param titleRes A string resource pointing to the title for the tab
     * @param fragmentClass The class of the Fragment to instantiate for this tab
     * @param args An optional Bundle to pass along to the Fragment (may be null)
     */
    protected void addTab(int titleRes, Class<? extends Fragment> fragmentClass, Bundle args ) {
        adapter.addTab( getString( titleRes ), fragmentClass, args );
    }
    /**
     * Add a tab with a backing Fragment to the action bar
     * @param titleRes A string to be used as the title for the tab
     * @param fragmentClass The class of the Fragment to instantiate for this tab
     * @param args An optional Bundle to pass along to the Fragment (may be null)
     */
    protected void addTab(CharSequence title, Class<? extends Fragment> fragmentClass, Bundle args ) {
        adapter.addTab( title, fragmentClass, args );
    }
    
    /**
     * Add a tab with a backing Fragment to the action bar
     * @param titleRes A string to be used as the title for the tab
     * @param icon A int R.drawable to be used as the icon for the tab
     * @param fragmentClass The class of the Fragment to instantiate for this tab
     * @param args An optional Bundle to pass along to the Fragment (may be null)
     */
    protected void addTab(CharSequence title, int icon, Class<? extends Fragment> fragmentClass, Bundle args ) {
        adapter.addTab( title, icon, fragmentClass, args );
    }
    
    /**
     * Add a tab with a backing Fragment to the action bar
     * @param icon A int R.drawable to be used as the icon for the tab
     * @param fragmentClass The class of the Fragment to instantiate for this tab
     * @param args An optional Bundle to pass along to the Fragment (may be null)
     */
    protected void addTab(Class<? extends Fragment> fragmentClass, Bundle args, int icon) {
        adapter.addTab( fragmentClass, args, icon);
    }
  
    private static class TabsAdapter extends FragmentPagerAdapter implements TabListener{
  
        private final SherlockFragmentActivity mActivity;
        private final ActionBar mActionBar;
  
        /**
         * @param fm
         * @param fragments
         */
        public TabsAdapter(SherlockFragmentActivity activity) {
            super(activity.getSupportFragmentManager());
            this.mActivity = activity;
            this.mActionBar = activity.getSupportActionBar();
  
            mActionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_TABS | ActionBar.NAVIGATION_MODE_STANDARD );
            mActionBar.setDisplayShowHomeEnabled(false);
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setHomeButtonEnabled(false);
        }
  
        private static class TabInfo {
            public final Class<? extends Fragment> fragmentClass;
            public final Bundle args;
            public TabInfo(Class<? extends Fragment> fragmentClass, Bundle args) {
                this.fragmentClass = fragmentClass;
                this.args = args;
            }
        }
  
        private List<TabInfo> mTabs = new ArrayList<TabInfo>();
  
        public void addTab( CharSequence title, Class<? extends Fragment> fragmentClass, Bundle args ) {
            final TabInfo tabInfo = new TabInfo( fragmentClass, args );
  
            Tab tab = mActionBar.newTab();
            tab.setText( title );
            tab.setTabListener( this );
            tab.setTag( tabInfo );
  
            mTabs.add( tabInfo );
  
            mActionBar.addTab( tab );
            notifyDataSetChanged();
        }
        
        public void addTab( CharSequence title, int icon, Class<? extends Fragment> fragmentClass, Bundle args ) {
            final TabInfo tabInfo = new TabInfo( fragmentClass, args );
  
            Tab tab = mActionBar.newTab();
            tab.setText( title );
            tab.setIcon(icon);
            tab.setTabListener( this );
            tab.setTag( tabInfo );
  
            mTabs.add( tabInfo );
  
            mActionBar.addTab( tab );
            notifyDataSetChanged();
        }
        
        public void addTab(Class<? extends Fragment> fragmentClass, Bundle args, int icon) {
            final TabInfo tabInfo = new TabInfo( fragmentClass, args );
  
            Tab tab = mActionBar.newTab();
            tab.setIcon(icon);
            tab.setTabListener( this );
            tab.setTag( tabInfo );
  
            mTabs.add( tabInfo );
  
            mActionBar.addTab( tab );
            notifyDataSetChanged();
        }
  
        @Override
        public Fragment getItem(int position) {
            final TabInfo tabInfo = mTabs.get( position );
            return Fragment.instantiate( mActivity, tabInfo.fragmentClass.getName(), tabInfo.args );
        }
  
        @Override
        public int getCount() {
            return mTabs.size();
        }
 
  
        @Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
        	/*
        	 * Slide to selected fragment when user selected tab
        	 */
            TabInfo tabInfo = (TabInfo) tab.getTag();
            for ( int i = 0; i < mTabs.size(); i++ ) {
                if ( mTabs.get( i ) == tabInfo ) {
                	ft.replace(R.id.fragment_container, getItem(i)); //camniar cuando sea need
                }
            }
        }
  
        @Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        }
  
        @Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
        }
    }
}