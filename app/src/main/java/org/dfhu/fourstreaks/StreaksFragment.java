package org.dfhu.fourstreaks;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * holds the steak statistics
 */
public class StreaksFragment extends Fragment {

    private TextView curNCH;
    private TextView curSOC;
    private TextView curNP;
    private TextView curKET;
    private View rootView;
    private MainActivity mainActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.streaks, container, false);

        mainActivity = (MainActivity) getActivity();

        curNCH = (TextView) rootView.findViewById(R.id.curNCH);
        curSOC = (TextView) rootView.findViewById(R.id.curSOC);
        curNP = (TextView) rootView.findViewById(R.id.curNP);
        curKET = (TextView) rootView.findViewById(R.id.curKET);

        setCurrentStreaks();

        return rootView;
    }

    private static class SetCurrentStreaksAsyncTask extends AsyncTask<MainActivity, Void, Integer[]> {

        private MainActivity mainActivity;
        private TextView curNCH;
        private TextView curSOC;
        private TextView curNP;
        private TextView curKET;

        private boolean isInStreak (Cursor cursor, boolean found, String columnName) {
            return !found && cursor.getInt(cursor.getColumnIndexOrThrow(columnName)) == 1;

        }

        @Override
        protected Integer[] doInBackground(MainActivity... mainActivities) {
            mainActivity = mainActivities[0];

            DaysEventSource source = new DaysEventSource(mainActivity);
            Cursor cursor = source.getAllTopLevel();

            if (!cursor.moveToFirst()) {
                return null;
            }

            int cntNCH = 0;
            int cntSOC = 0;
            int cntNP = 0;
            int cntKET = 0;
            boolean foundNCH = false;
            boolean foundSOC = false;
            boolean foundNP = false;
            boolean foundKET = false;
            do {
                if (foundNCH && foundSOC && foundNP && foundKET) {
                    break; // found all current streaks
                }

                if (isInStreak(cursor, foundNP, DaysEventHelper.C.flag_NCH)) {
                    cntNCH += 1;
                } else {
                    foundNCH = true;
                }
                if (isInStreak(cursor, foundSOC, DaysEventHelper.C.flag_SOC)) {
                    cntSOC += 1;
                } else {
                    foundSOC = true;
                }
                if (isInStreak(cursor, foundNP, DaysEventHelper.C.flag_NP)) {
                    cntNP += 1;
                } else {
                    foundNP = true;
                }
                if (isInStreak(cursor, foundKET, DaysEventHelper.C.flag_KET)) {
                    cntKET += 1;
                } else {
                    foundKET = true;
                }

            } while (cursor.moveToNext());
            cursor.close();

            // FIXME: 9/22/15 this should be some sort of struct
            return new Integer[]{cntNCH, cntSOC, cntNP, cntKET};
        }

        @Override
        protected void onPostExecute(Integer[] integers) {
            super.onPostExecute(integers);

            if (integers == null) {
                return;
            }

            int cntNCH = integers[0];
            int cntSOC = integers[1];
            int cntNP = integers[2];
            int cntKET = integers[3];

            curNCH = (TextView) mainActivity.findViewById(R.id.curNCH);
            curSOC = (TextView) mainActivity.findViewById(R.id.curSOC);
            curNP = (TextView) mainActivity.findViewById(R.id.curNP);
            curKET = (TextView) mainActivity.findViewById(R.id.curKET);

            curNCH.setText(String.format("%d", cntNCH));
            curSOC.setText(String.format("%d", cntSOC));
            curNP.setText(String.format("%d", cntNP));
            curKET.setText(String.format("%d", cntKET));
        }
    }

    private void setCurrentStreaks() {
        new SetCurrentStreaksAsyncTask().execute(mainActivity);




    }


}
