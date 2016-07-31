package randomness.akhil.timetable;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private static final String[] DOW = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private static boolean isConnectivityAvailable=false;
    private static final String LOG_TAG = "TimeTable";
    public static String s = "";
    private static final String LINK = "http://akhilnarang.github.io/BT-TimeTable";
    private static final String prefixUrl = "https://drive.google.com/viewerng/viewer?embedded=true&url=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        FloatingActionButton fab= (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateConnectivityStatus();
                    if (isConnectivityAvailable)
                        openLink(prefixUrl+getBTTTLink());
                    else
                        Snackbar.make(v, "Internet Connection Not Available!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                }
            });
            fab.setLongClickable(true);
            fab.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    updateConnectivityStatus();
                    if (isConnectivityAvailable)
                        openLink(getBTTTLink());
                    else
                        Snackbar.make(v, "Internet Connection Not Available!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    return true;
                }
            });


    }

    private void updateConnectivityStatus()
    {
        ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
        isConnectivityAvailable = (netInfo != null && netInfo.isConnected());
    }

    private void openLink(String link) {
        try {
            Log.i(LOG_TAG, "Opening link = " + link);
            Uri webpage = Uri.parse(link);
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } catch (Exception ioe) {
            Log.e(LOG_TAG, "Exception Occurred\n", ioe);
        }
    }

    public String getBTTTLink() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(LINK);
                    BufferedReader stdin = new BufferedReader(new InputStreamReader(url.openStream()));
                    s=stdin.readLine();
                    stdin.close();
                } catch (IOException ioe) {
                   Log.e(LOG_TAG, "IOException Occurred trying to check URL\n" + LINK, ioe);
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.e(LOG_TAG, "Error", e);
        }
        return s;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

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

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            TextView timeTable = (TextView) rootView.findViewById(R.id.day_timetable);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            int position=getArguments().getInt(ARG_SECTION_NUMBER)-1;
            switch (position) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    textView.setText("Time Table for "+DOW[position]+"\n\n");
            }
            timeTable.setText("\n\n"+giveTimeTable(position+1));
            return rootView;
        }
        public String giveTimeTable(int dow)
        {
            String s="";
            switch (dow)
            {
                case 1:
                s+=     "8:30-9:30 Physics/Chemistry Practicals\n" +
                        "9:30-10:10 Chemistry\n" +
                        "10:10-11:20 English\n" +
                        "11:20-11:35 Break\n" +
                        "11:35-2:30 SME Classes\n";
                    break;
                case 2:
                s+=     "8:30-8:50 Assembly\n" +
                        "8:50-10:10 IP\n" +
                        "10:10-11:20 Maths\n" +
                        "11:20-11:35 Break\n" +
                        "11:35-2:30 SME Classes\n";
                break;
                case 3:
                s+=     "8:30-9:30 IP\n" +
                        "9:30-10:10 LSO/WE\n" +
                        "10:10-11:20 Physics/Chemistry Practicals\n" +
                        "11:20-11:35 Break\n" +
                        "11:35-2:30 SME Classes\n";
                break;
                case 4:
                s+=     "8:30-9:30 Physics\n" +
                        "9:30-10:10 English\n" +
                        "10:10-11:20 IP\n" +
                        "11:20-11:35 Break\n" +
                        "11:35-2:30 SME Classes\n";
                break;
                case 5:
                s+=     "8:30-10:10 Balewadi/SKP/Whatever\n" +
                        "10:10-10:25 Break\n" +
                        "10:25-11:35 English\n" +
                        "11:35-2:30 SME Classes\n";
                break;
                case 6:
                s+=     "9:00-9:35 Physics\n" +
                        "9:35-10:10 IP\n" +
                        "10:10-10:45 Mathematics\n" +
                        "10:45-11:20 Chemistry\n" +
                        "11:20-11:35 Break\n" +
                        "11:35-2:30 SME Classes\n";
                break;
                default: s="No school";
            }
            return s;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return DOW.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}
