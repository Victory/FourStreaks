package org.dfhu.fourstreaks;

import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.view.View;
import android.widget.Switch;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mActivity;
    private Switch toggleNCH;
    private Switch toggleSOC;
    private Switch toggleNP;
    private Switch toggleKET;
    private View origin;
    private Switch[] switches;

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

    }

    public void testAllSwitchesVisible () {
        for (Switch toggle: switches) {
            ViewAsserts.assertOnScreen(origin, toggle);
        }
    }
}
