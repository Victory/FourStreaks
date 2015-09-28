package org.dfhu.fourstreaks;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * holds the steak statistics
 */
public class StreaksFragment extends Fragment {

    private View rootView;
    private static MainActivity mainActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.streaks, container, false);

        mainActivity = (MainActivity) getActivity();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCurrentStreaks();
        setLongestStreaks();
    }

    private void setLongestStreaks() {
        new SetLongestStreaksAsyncTask(mainActivity).execute();
    }

    private static class SetLongestStreaksAsyncTask extends AsyncTask<Void, Void, SetLongestStreaksAsyncTask.StreakResults> {

        private MainActivity mainActivity;

        private TextView longestNCH;
        private TextView longestSOC;
        private TextView longestNP;
        private TextView longestKET;

        public class StreakResults {
            int nch;
            int soc;
            int np;
            int ket;
        }

        public SetLongestStreaksAsyncTask(MainActivity activity) {
            mainActivity = activity;
            longestNCH = (TextView) mainActivity.findViewById(R.id.longestNCH);
            longestSOC = (TextView) mainActivity.findViewById(R.id.longestSOC);
            longestNP = (TextView) mainActivity.findViewById(R.id.longestNP);
            longestKET = (TextView) mainActivity.findViewById(R.id.longestKET);
        }

        @Override
        protected StreakResults doInBackground(Void... voids) {
            StreakResults streaksResults = new StreakResults();
            streaksResults.nch = 1;
            streaksResults.soc = 2;
            streaksResults.np = 3;
            streaksResults.ket = 4;

            return streaksResults;
        }

        @Override
        protected void onPostExecute(StreakResults result) {
            super.onPostExecute(result);

            longestNCH.setText(String.format("%d", result.nch));
            longestSOC.setText(String.format("%d", result.soc));
            longestNP.setText(String.format("%d", result.np));
            longestKET.setText(String.format("%d", result.ket));
        }


    }

    private static class SetCurrentStreaksAsyncTask extends AsyncTask<MainActivity, Void, SetCurrentStreaksAsyncTask.StreakResults> {

        private TextView curNCH;
        private TextView curSOC;
        private TextView curNP;
        private TextView curKET;

        public class StreakResults {
            int nch;
            int soc;
            int np;
            int ket;
        }

        private boolean isInStreak (Cursor cursor, boolean found, String columnName) {
            return !found && cursor.getInt(cursor.getColumnIndexOrThrow(columnName)) == 1;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            curNCH = (TextView) mainActivity.findViewById(R.id.curNCH);
            curSOC = (TextView) mainActivity.findViewById(R.id.curSOC);
            curNP = (TextView) mainActivity.findViewById(R.id.curNP);
            curKET = (TextView) mainActivity.findViewById(R.id.curKET);
        }

        @Override
        protected StreakResults doInBackground(MainActivity... mainActivities) {
            mainActivity = mainActivities[0]; // FIXME: 9/27/15 should be decoupled

            DaysEventSource source = new DaysEventSource(mainActivity);
            Cursor cursor = source.getAllTopLevel();

            StreakResults result = new StreakResults();

            if (!cursor.moveToFirst() || !isInStreak(cursor)) {
                return result;
            }

            boolean foundNCH = false;
            boolean foundSOC = false;
            boolean foundNP = false;
            boolean foundKET = false;

            String last = cursor.getString(cursor.getColumnIndexOrThrow(DaysEventHelper.C.date_of_event));
            String cur;
            do {
                if (foundNCH && foundSOC && foundNP && foundKET) {
                    break; // found all current streaks
                }

                if (isInStreak(cursor, foundNCH, DaysEventHelper.C.flag_NCH)) {
                    result.nch += 1;
                } else {
                    foundNCH = true;
                }
                if (isInStreak(cursor, foundSOC, DaysEventHelper.C.flag_SOC)) {
                    result.soc += 1;
                } else {
                    foundSOC = true;
                }
                if (isInStreak(cursor, foundNP, DaysEventHelper.C.flag_NP)) {
                    result.np += 1;
                } else {
                    foundNP = true;
                }
                if (isInStreak(cursor, foundKET, DaysEventHelper.C.flag_KET)) {
                    result.ket += 1;
                } else {
                    foundKET = true;
                }

                cur = cursor.getString(cursor.getColumnIndexOrThrow(DaysEventHelper.C.date_of_event));
                if (isGapInDate(cur, last)) {
                    break;
                }
                last = cur;

            } while (cursor.moveToNext());
            cursor.close();

            return result;
        }

        private boolean isGapInDate(String cur, String last) {
            if (cur.equals(last)) {
                return false;
            }

            DateFormat dateFormat = MainActivity.mDateFormat;

            Date curDate;
            Date lastDate;
            try {
                curDate = dateFormat.parse(cur);
                lastDate = dateFormat.parse(last);
            } catch (ParseException e) {
                return false;
            }

            long diff = lastDate.getTime() - curDate.getTime();
            return TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS) > 24;
        }

        /**
         * return true if the cursor points to a date that is less than 24 hours ago
         * @param cursor must have DaysEventHelper.C.date_of_event column
         * @return true if cursor is within the last day
         */
        private boolean isInStreak(Cursor cursor) {
            String lastRecordedDateString = cursor.getString(cursor.getColumnIndexOrThrow(DaysEventHelper.C.date_of_event));
            DateFormat dateFormat = MainActivity.mDateFormat;

            Date last;
            try {
                last = dateFormat.parse(lastRecordedDateString);
            } catch (ParseException e) {
                return false;
            }
            Date today = new Date();

            long diff = today.getTime() - last.getTime();

            return TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS) < 48;
        }

        @Override
        protected void onPostExecute(StreakResults result) {
            super.onPostExecute(result);

            curNCH.setText(String.format("%d", result.nch));
            curSOC.setText(String.format("%d", result.soc));
            curNP.setText(String.format("%d", result.np));
            curKET.setText(String.format("%d", result.ket));
        }
    }

    public void setCurrentStreaks() {
        new SetCurrentStreaksAsyncTask().execute(mainActivity);
    }
}
