package com.eis.transteinle.gigloungeprototype;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Button;

import com.eis.transteinle.gigloungeprototype.band.BandListFragment;
import com.eis.transteinle.gigloungeprototype.band.CreateBandFragment;
import com.eis.transteinle.gigloungeprototype.connection.ServerRequest;
import com.eis.transteinle.gigloungeprototype.event.CreateEventFragment;
import com.eis.transteinle.gigloungeprototype.event.EventListFragment;
import com.eis.transteinle.gigloungeprototype.user.UserFragment;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    static SharedPreferences pref;
    ServerRequest sr;

    private DlTask mDlTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getSharedPreferences("AppPref", MODE_PRIVATE);
        if (!pref.contains("cookie")) {
            Intent logIntent = new Intent(this, LoginActivity.class);
            finish();
            startActivity(logIntent);
        } /*else if(!pref.contains("REG_ID")) {
            if (checkPlayServices()) {
                new RegisterGCM().execute();
            }
        }*/
        else {
            attemptDl();
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    private void attemptDl() {
        if (mDlTask != null) {
            return;
        }
        mDlTask = new DlTask();
        mDlTask.execute();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        Fragment fragment;
        pref = getSharedPreferences("AppPref", MODE_PRIVATE);
        switch (position) {
            case 0:
                fragment = PlaceholderFragment.newInstance(position + 1);
                break;
            case 1:
                fragment = UserFragment.newInstance(pref.getString("id", ""));
                break;
            case 2:
                fragment = BandListFragment.newInstance(position + 1);
                break;
            case 3:
                fragment = PlaceholderFragment.newInstance(position + 1);
                break;
            case 4:
                fragment = CreateBandFragment.newInstance("","");
                break;
            default:
                fragment = PlaceholderFragment.newInstance(position + 1);
                break;
        }
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_activity_main);
                break;
            case 2:
                mTitle = getString(R.string.title_profile);
                break;
            case 3:
                mTitle = getString(R.string.title_bands);
                break;
            case 4:
                mTitle = getString(R.string.title_events);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DlTask extends AsyncTask<String, String,JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            sr = new ServerRequest(MainActivity.this);

            JSONObject json = sr.getJSON("/home");

            return json;
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);

            if (json != null) {
                try {
                    Log.d("JSON", json.toString());
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString("id", json.getJSONObject("user").getString("_id"));
                    edit.putString("email", json.getJSONObject("user").getString("email"));
                    edit.putString("firstName", json.getJSONObject("user").getString("firstName"));
                    edit.putString("lastName", json.getJSONObject("user").getString("lastName"));
                    edit.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        protected void onCancelled() {
            mDlTask = null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment{
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        View rootView;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            Button mCreateGigBtn = (Button)rootView.findViewById(R.id.create_gig_btn);

            mCreateGigBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = EventListFragment.newInstance("", "");
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, fragment)
                            .commit();
                }
            });

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
