package org.dfhu.fourstreaks;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class InputFragment extends Fragment {


    private EditText mDate;
    private Button mSave;
    private MyDatePickerDialog myDatePickerDialog;

    private Switch toggleSOC;
    private Switch toggleNCH;
    private Switch toggleNP;
    private Switch toggleKET;
    private View rootView;
    private static MainActivity mainActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_main, container, false);

        mainActivity = (MainActivity) getActivity();

        setUpDate();
        setUpSave();

        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fillList();
    }

    public void fillList () {
        new FillListAsyncTask().execute(mainActivity);
    }

    private static class FillListAsyncTask extends AsyncTask<MainActivity, Void, EventCursorAdapter> {

        private MainActivity context;
        private ListView list;
        private ImageView listLoading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list = (ListView) mainActivity.findViewById(R.id.eventsList);
            list.setVisibility(View.INVISIBLE);

            listLoading = (ImageView) mainActivity.findViewById(R.id.listLoading);
            listLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected EventCursorAdapter doInBackground(MainActivity... context) {
            this.context = context[0];

            if (this.context == null) {
                return null;
            }

            DaysEventSource source = new DaysEventSource(this.context);
            Cursor cursor = source.getAllTopLevel();

            // safe check and set because myLooper() is per thread
            if (Looper.myLooper() == null) {
                Looper.prepare(); // need for CursorAdapter
            }

            return new EventCursorAdapter(this.context, cursor, false);
        }

        @Override
        protected void onPostExecute(EventCursorAdapter eventCursorAdapter) {

            // context has likely gone away
            if (eventCursorAdapter == null) {
                return;
            }

            list.setAdapter(eventCursorAdapter);
            list.setVisibility(View.VISIBLE);
            listLoading.setVisibility(View.INVISIBLE);
        }
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

                mainActivity.setCurrentStreaks();
            }
        });
    }


    private boolean isValidDate (String dateString) {
        Date date;

        try {
            date = MainActivity.mDateFormat.parse(dateString);
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
        mDate.setText(MainActivity.mDateFormat.format(cal.getTime()));

        myDatePickerDialog = new MyDatePickerDialog(
                mainActivity, new MyOnDateSetListener(), cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        mDate.setOnClickListener(new DatePickerClickListener());

    }

    private class MyOnDateSetListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker datePicker, int yyyy, int mm, int dd) {
            Calendar cal = Calendar.getInstance();
            cal.set(yyyy, mm, dd);
            mDate.setText(MainActivity.mDateFormat.format(cal.getTime()));
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
