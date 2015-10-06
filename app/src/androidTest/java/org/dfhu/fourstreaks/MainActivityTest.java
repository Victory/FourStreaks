package org.dfhu.fourstreaks;

import android.content.pm.ActivityInfo;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mActivity;
    private Switch toggleNCH;
    private Switch toggleSOC;
    private Switch toggleNP;
    private Switch toggleKET;
    private View origin;
    private Switch[] switches;
    private EditText editTextDate;
    private ListView eventsList;

    private ImageView listLoading;

    private DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Button buttonSave;

    private TextView curNCH;

    private TextView longestNCH;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = getActivity();
        origin = mActivity.getWindow().getDecorView();

        DaysEventSource eventsSource = new DaysEventSource(mActivity);
        eventsSource.deleteAllRecords();

        toggleNCH = (Switch) mActivity.findViewById(R.id.toggleNCH);
        toggleSOC = (Switch) mActivity.findViewById(R.id.toggleSOC);
        toggleNP = (Switch) mActivity.findViewById(R.id.toggleNP);
        toggleKET = (Switch) mActivity.findViewById(R.id.toggleKET);
        switches = new Switch[]{toggleNCH, toggleSOC, toggleNP, toggleKET};

        editTextDate = (EditText) mActivity.findViewById(R.id.editTextDate);
        buttonSave = (Button) mActivity.findViewById(R.id.buttonSave);
        eventsList = (ListView) mActivity.findViewById(R.id.eventsList);
        listLoading = (ImageView) mActivity.findViewById(R.id.listLoading);

        curNCH = (TextView) mActivity.findViewById(R.id.curNCH);

        longestNCH = (TextView) mActivity.findViewById(R.id.longestNCH);
    }


    protected void setTextInEditText (final EditText elm, String text) {

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                elm.setText("");
                elm.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync(text);
        getInstrumentation().waitForIdleSync();
    }

    protected void clickButton (final Button button) {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                button.performClick();
            }
        });
    }

    protected void clickToggle (final Switch toggle) {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                toggle.performClick();
            }
        });
    }

    @SmallTest
    public void testAllSwitchesVisible() {
        for (Switch toggle: switches) {
            ViewAsserts.assertOnScreen(origin, toggle);
        }
    }

    @SmallTest
    public void testInvalidDate() {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        String tomorrow = mDateFormat.format(cal.getTime());
        setTextInEditText(editTextDate, tomorrow);
        clickButton(buttonSave);

        String expected = mActivity.getString(R.string.invalidDate);
        String actual = MyToast.getLastText();

        assertEquals(expected, actual);
    }

    @SmallTest
    public void testInsertingItemWithNoExtras () throws InterruptedException {
        for (Switch toggle: switches) {
            clickToggle(toggle);
        }

        clickButton(buttonSave);
        getInstrumentation().waitForIdleSync();
        ViewAsserts.assertOnScreen(origin, listLoading);
        assertEquals(listLoading.getVisibility(), View.VISIBLE);
        assertEquals(eventsList.getVisibility(), View.INVISIBLE);
        getInstrumentation().waitForIdleSync();

        Thread.sleep(3000); // would be better if didn't have to sleep

        ViewAsserts.assertOnScreen(origin, eventsList);
        assertEquals(listLoading.getVisibility(), View.INVISIBLE);
        assertEquals(eventsList.getVisibility(), View.VISIBLE);
    }

    @SmallTest
    public void testOrientationChange() throws InterruptedException {
        Random rand = new Random();
        int max = 3000;
        int min = 100;
        int randomSleep;

        for (int ii = 0; ii < 5; ii++) {

            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            getInstrumentation().waitForIdleSync();
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);

            randomSleep = rand.nextInt((max - min) + 1) + min;
            Thread.sleep(randomSleep);

            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        }
        Thread.sleep(3000);
    }


    @SmallTest
    public void testOneCurStreak () throws InterruptedException {

        clickToggle(toggleNCH);

        clickButton(buttonSave);
        getInstrumentation().waitForIdleSync();
        ViewAsserts.assertOnScreen(origin, listLoading);
        assertEquals(listLoading.getVisibility(), View.VISIBLE);
        assertEquals(eventsList.getVisibility(), View.INVISIBLE);
        getInstrumentation().waitForIdleSync();

        Thread.sleep(3000); // would be better if didn't have to sleep

        ViewAsserts.assertOnScreen(origin, eventsList);
        assertEquals(listLoading.getVisibility(), View.INVISIBLE);
        assertEquals(eventsList.getVisibility(), View.VISIBLE);

        int actual = Integer.parseInt(curNCH.getText().toString());
        int expected = 1;
        assertEquals(expected, actual);
    }

    @SmallTest
    public void testOneLongestStreak () throws InterruptedException {

        clickToggle(toggleNCH);

        clickButton(buttonSave);
        getInstrumentation().waitForIdleSync();
        ViewAsserts.assertOnScreen(origin, listLoading);
        assertEquals(listLoading.getVisibility(), View.VISIBLE);
        assertEquals(eventsList.getVisibility(), View.INVISIBLE);
        getInstrumentation().waitForIdleSync();

        Thread.sleep(3000); // would be better if didn't have to sleep

        ViewAsserts.assertOnScreen(origin, eventsList);
        assertEquals(listLoading.getVisibility(), View.INVISIBLE);
        assertEquals(eventsList.getVisibility(), View.VISIBLE);

        int actual = Integer.parseInt(longestNCH.getText().toString());
        int expected = 1;
        assertEquals(expected, actual);
    }
}
