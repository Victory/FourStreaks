package org.dfhu.fourstreaks;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MainActivity extends FragmentActivity implements EventCursorAdapter.FillListInterface {

    public static DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    private ViewPager mViewPager;
    private InputFragment inputFragment;
    private StreaksFragment streaksFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pager);

        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
    }

    public class AppSectionsPagerAdapter extends FragmentPagerAdapter {
        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    inputFragment = new InputFragment();
                    return inputFragment;
                case 1:
                    streaksFragment = new StreaksFragment();
                    return streaksFragment;
                default:
                    throw new IndexOutOfBoundsException("internal error only two positions");

            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public void fillList () {
        inputFragment.fillList();
    }

    public void setCurrentStreaks() {
        streaksFragment.setCurrentStreaks();
    }

}
