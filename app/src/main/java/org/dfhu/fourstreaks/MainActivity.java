package org.dfhu.fourstreaks;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends FragmentActivity implements EventCursorAdapter.FillListInterface {

    private AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    private ActionBar actionBar;
    private ViewPager mViewPager;
    private InputFragment inputFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pager);

        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        actionBar = getActionBar();
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);

        /*

        */

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
                default:
                    Fragment dummySectionFragment = new DummySectionFragment();
                    Bundle args = new Bundle();
                    args.putInt(DummySectionFragment.DUMMY_POSITION, position);
                    dummySectionFragment.setArguments(args);
                    return dummySectionFragment;

            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public static class DummySectionFragment extends Fragment {

        public static final String DUMMY_POSITION = "dummyPosition";

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View dummyView = inflater.inflate(R.layout.dummy, container, false);
            Bundle args = getArguments();
            TextView textView = (TextView) dummyView.findViewById(R.id.dummyText);
            textView.setText(String.format("%d", args.getInt(DUMMY_POSITION)));
            return dummyView;
        }
    }

    public void fillList () {
        inputFragment.fillList();
    }

    public static class InputFragment extends Fragment {

        private EditText mDate;
        private Button mSave;
        private DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        private MyDatePickerDialog myDatePickerDialog;
        private ListView mEventsList;

        private Switch toggleSOC;
        private Switch toggleNCH;
        private Switch toggleNP;
        private Switch toggleKET;
        private View rootView;
        private MainActivity mainActivity;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.activity_main, container, false);

            mainActivity = (MainActivity) getActivity();

            setUpDate();
            setUpSave();
            mEventsList = (ListView) rootView.findViewById(R.id.eventsList);
            fillList();

            return rootView;
        }


        public void fillList () {
            DaysEventSource source = new DaysEventSource(mainActivity);
            Cursor cursor = source.getAllTopLevel();
            EventCursorAdapter eventCursorAdapter = new EventCursorAdapter(mainActivity, cursor, false);
            mEventsList.setAdapter(eventCursorAdapter);
        }

        private void setUpSave() {
            mSave = (Button) rootView.findViewById(R.id.buttonSave);

            toggleSOC = (Switch) rootView.findViewById(R.id.toggleSOC);
            toggleNCH = (Switch) rootView.findViewById(R.id.toggleNCH);
            toggleNP = (Switch) rootView.findViewById(R.id.toggleNP);
            toggleKET = (Switch) rootView.findViewById(R.id.toggleKET);

            mSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DaysEventRow row = new DaysEventRow();
                    String dateString = mDate.getText().toString();

                    if (!isValidDate(dateString)) {
                        MyToast.makeText(mainActivity, getString(R.string.invalidDate), Toast.LENGTH_SHORT).show();

                        return;
                    }

                    row.set(DaysEventHelper.C.date_of_event, dateString);

                    row.set(DaysEventHelper.C.flag_SOC, toggleSOC.isChecked());
                    row.set(DaysEventHelper.C.flag_NCH, toggleNCH.isChecked());
                    row.set(DaysEventHelper.C.flag_NP, toggleNP.isChecked());
                    row.set(DaysEventHelper.C.flag_KET, toggleKET.isChecked());

                    DaysEventSource source = new DaysEventSource(mainActivity);
                    source.insert(row);

                    fillList();
                }
            });
        }


        private boolean isValidDate (String dateString) {
            Date date;

            try {
                date = mDateFormat.parse(dateString);
            } catch (ParseException e) {
                return false;
            }

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            Date latestDate = new Date(cal.getTimeInMillis());

            return latestDate.compareTo(date) >= 0;
        }

        private void setUpDate() {
            mDate = (EditText) rootView.findViewById(R.id.editTextDate);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            mDate.setText(mDateFormat.format(cal.getTime()));

            myDatePickerDialog = new MyDatePickerDialog(
                    mainActivity, new MyOnDateSetListener(), cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            mDate.setOnClickListener(new DatePickerClickListener());

        }

        private class MyOnDateSetListener implements DatePickerDialog.OnDateSetListener {
            @Override
            public void onDateSet(DatePicker datePicker, int yyyy, int mm, int dd) {
                Calendar cal = Calendar.getInstance();
                cal.set(yyyy, mm, dd);
                mDate.setText(mDateFormat.format(cal.getTime()));
            }
        }

        private class DatePickerClickListener implements View.OnClickListener {
            @Override
            public void onClick(View view) {
                myDatePickerDialog.show();
            }
        }

        private class MyDatePickerDialog extends DatePickerDialog {
            public MyDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
                super(context, callBack, year, monthOfYear, dayOfMonth);
            }

            @Override
            public void onDateChanged(DatePicker view, int year, int month, int day) {
                super.onDateChanged(view, year, month, day);
            }
        }

    }
}
