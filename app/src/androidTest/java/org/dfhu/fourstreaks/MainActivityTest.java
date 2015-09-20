package org.dfhu.fourstreaks;

import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mActivity;
    private Switch toggleNCH;
    private Switch toggleSOC;
    private Switch toggleNP;
    private Switch toggleKET;
    private View origin;
    private Switch[] switches;
    private EditText editTextDate;

    private DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Button buttonSave;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = getActivity();
        origin = mActivity.getWindow().getDecorView();

        toggleNCH = (Switch) mActivity.findViewById(R.id.toggleNCH);
        toggleSOC = (Switch) mActivity.findViewById(R.id.toggleSOC);
        toggleNP = (Switch) mActivity.findViewById(R.id.toggleNP);
        toggleKET = (Switch) mActivity.findViewById(R.id.toggleKET);
        switches = new Switch[]{toggleNCH, toggleSOC, toggleNP, toggleKET};

        editTextDate = (EditText) mActivity.findViewById(R.id.editTextDate);
        buttonSave = (Button) mActivity.findViewById(R.id.buttonSave);
    }

    public void testAllSwitchesVisible() {
        for (Switch toggle: switches) {
            ViewAsserts.assertOnScreen(origin, toggle);
        }
    }

    public void testValidDate() {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        String tomorrow = mDateFormat.format(cal.getTime());

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                editTextDate.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync(tomorrow);
        getInstrumentation().waitForIdleSync();

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                buttonSave.performClick();
            }
        });


        String expected = mActivity.getString(R.string.invalidDate);
        String actual = MyToast.getLastText();

        assertEquals(expected, actual);
    }
}
