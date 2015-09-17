package org.dfhu.fourstreaks;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private EditText mDate;
    private Button mSave;
    private DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private MyDatePickerDialog myDatePickerDialog;
    private ListView mEventsList;

    private Switch toggleSOC;
    private Switch toggleNCH;
    private Switch toggleNP;
    private Switch toggleKET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpDate();
        setUpSave();

        mEventsList = (ListView) findViewById(R.id.eventsList);
        fillList();

    }

    public void fillList () {
        DaysEventSource source = new DaysEventSource(MainActivity.this);
        Cursor cursor = source.getAllTopLevel();
        EventCursorAdapter eventCursorAdapter = new EventCursorAdapter(this, cursor, false);
        mEventsList.setAdapter(eventCursorAdapter);
    }

    private void setUpSave() {
        mSave = (Button) findViewById(R.id.buttonSave);

        toggleSOC = (Switch) findViewById(R.id.toggleSOC);
        toggleNCH = (Switch) findViewById(R.id.toggleNCH);
        toggleNP = (Switch) findViewById(R.id.toggleNP);
        toggleKET = (Switch) findViewById(R.id.toggleKET);

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DaysEventRow row = new DaysEventRow();
                row.set(DaysEventHelper.C.date_of_event, mDate.getText().toString());

                row.set(DaysEventHelper.C.flag_SOC, toggleSOC.isChecked());
                row.set(DaysEventHelper.C.flag_NCH, toggleNCH.isChecked());
                row.set(DaysEventHelper.C.flag_NP, toggleNP.isChecked());
                row.set(DaysEventHelper.C.flag_KET, toggleKET.isChecked());

                DaysEventSource source = new DaysEventSource(MainActivity.this);
                source.insert(row);

                fillList();
            }
        });
    }

    private void setUpDate() {
        mDate = (EditText) findViewById(R.id.editTextDate);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        mDate.setText(mDateFormat.format(cal.getTime()));

        myDatePickerDialog = new MyDatePickerDialog(
                this, new MyOnDateSetListener(), cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
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

}
