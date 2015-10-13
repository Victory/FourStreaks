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
import java.util.HashMap;
import java.util.Map;
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
    }

    private static class SetCurrentStreaksAsyncTask extends AsyncTask<Void, Void, SetCurrentStreaksAsyncTask.StreakResults[]> {

        private TextView curNCH;
        private TextView curSOC;
        private TextView curNP;
        private TextView curKET;

        private TextView longestNCH;
        private TextView longestSOC;
        private TextView longestNP;
        private TextView longestKET;

        private TextView datesNCH;


        /**
         * uses DaysEventHelper.C.flag_SOC type notation to store integer values
         */
        public class StreakResults {

            private String[] flags = new String[]{
                    DaysEventHelper.C.flag_NCH,
                    DaysEventHelper.C.flag_SOC,
                    DaysEventHelper.C.flag_NP,
                    DaysEventHelper.C.flag_KET
            };

            private HashMap<String, Integer> values = new HashMap<>();

            // maps keys e.g. DaysEventHelper.C.flag_SOC to a string representation of the date
            private HashMap<String, String> startDates = new HashMap<>();
            private HashMap<String, String> endDates = new HashMap<>();

            public StreakResults() {
                resetAll();
            }

            public void increment(String key, String dateString) {
                if (values.get(key) == 0) {
                    startDates.put(key, dateString);
                }

                values.put(key, values.get(key) + 1);
            }

            /**
             * set the associated key back to zero
             *
             * @param key e.g. DaysEventHelper.C.flag_SOC
             */
            public void reset(String key) {
                values.put(key, 0);
            }

            /**
             * set all values to zero
             */
            public void resetAll () {
                for (String flag: flags) {
                    values.put(flag, 0);
                }
            }

            public Integer get(String key) {
                return values.get(key);
            }

            public void copy (String key, StreakResults rhs, String dateOfLastEvent) {
                endDates.put(key, dateOfLastEvent);
                startDates.put(key, rhs.getStartDate(key));
                values.put(key, rhs.get(key));
            }

            private String getStartDate(String key) {
                return startDates.get(key);
            }

            public String getDateString(String key) {
                String startDate = startDates.get(key);
                String endDate = endDates.get(key);
                return String.format("%s - %s", endDate, startDate);
            }
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            curNCH = (TextView) mainActivity.findViewById(R.id.curNCH);
            curSOC = (TextView) mainActivity.findViewById(R.id.curSOC);
            curNP = (TextView) mainActivity.findViewById(R.id.curNP);
            curKET = (TextView) mainActivity.findViewById(R.id.curKET);

            longestNCH = (TextView) mainActivity.findViewById(R.id.longestNCH);
            longestSOC = (TextView) mainActivity.findViewById(R.id.longestSOC);
            longestNP = (TextView) mainActivity.findViewById(R.id.longestNP);
            longestKET = (TextView) mainActivity.findViewById(R.id.longestKET);

            datesNCH = (TextView) mainActivity.findViewById(R.id.datesNCH);
        }

        @Override
        protected StreakResults[] doInBackground(Void... voids) {

            DaysEventSource source = new DaysEventSource(mainActivity);
            Cursor cursor = source.getAllTopLevel();

            StreakResults longest = new StreakResults();
            StreakResults current = new StreakResults();

            // we don't have any values
            if (!cursor.moveToFirst()) {
                cursor.close();
                return new StreakResults[]{current, longest};
            }

            boolean foundNCH = false;
            boolean foundSOC = false;
            boolean foundNP = false;
            boolean foundKET = false;

            if (!isInStreak(cursor)) {
                foundNCH = foundSOC = foundNP = foundKET = true;
            }

            String last = cursor.getString(cursor.getColumnIndexOrThrow(DaysEventHelper.C.date_of_event));
            String cur;
            StreakResults tmp = new StreakResults();
            do {
                cur = cursor.getString(cursor.getColumnIndexOrThrow(DaysEventHelper.C.date_of_event));
                boolean isInGap = isGapInDate(cur, last);
                last = cur;

                if (isInGap) {
                    tmp.resetAll();
                    foundNCH = foundSOC = foundNP = foundKET = true;
                }

                foundNCH = calculateStreak(cursor, DaysEventHelper.C.flag_NCH, foundNCH, current, longest, tmp);
                foundSOC = calculateStreak(cursor, DaysEventHelper.C.flag_SOC, foundSOC, current, longest, tmp);
                foundNP = calculateStreak(cursor, DaysEventHelper.C.flag_NP, foundNP, current, longest, tmp);
                foundKET = calculateStreak(cursor, DaysEventHelper.C.flag_KET, foundKET, current, longest, tmp);

            } while (cursor.moveToNext());
            cursor.close();

            return new StreakResults[]{current, longest};
        }

        /**
         *
         * @param cursor points to DaysEventSource.getAllTopLevel
         * @param key e.g.
         * @param found DaysEventHelper.C.flag_NCH
         * @param current holds current streaks
         * @param longest holds longest streaks
         * @param tmp holds the temp streaks calculation
         * @return true if current streak has been found
         */
        private boolean calculateStreak (
                Cursor cursor, String key, boolean found, StreakResults current, StreakResults longest, StreakResults tmp) {

            String dateOfEventString = cursor.getString(cursor.getColumnIndexOrThrow(DaysEventHelper.C.date_of_event));

            boolean isInStreak = isInStreak(cursor, key);
            if (isInStreak) {
                tmp.increment(key, dateOfEventString);
            } else {
                found = true;
                tmp.reset(key);
            }

            if (!found && tmp.get(key) > current.get(key)) {
                current.copy(key, tmp, dateOfEventString);
            }

            if (tmp.get(key) > longest.get(key)) {
                longest.copy(key, tmp, dateOfEventString);
            }

            return found;
        }

        @Override
        protected void onPostExecute(StreakResults[] result) {
            super.onPostExecute(result);

            StreakResults current = result[0];
            StreakResults longest = result[1];

            curNCH.setText(String.format("%d", current.get(DaysEventHelper.C.flag_NCH)));
            curSOC.setText(String.format("%d", current.get(DaysEventHelper.C.flag_SOC)));
            curNP.setText(String.format("%d", current.get(DaysEventHelper.C.flag_NP)));
            curKET.setText(String.format("%d", current.get(DaysEventHelper.C.flag_KET)));

            longestNCH.setText(String.format("%d", longest.get(DaysEventHelper.C.flag_NCH)));
            longestSOC.setText(String.format("%d", longest.get(DaysEventHelper.C.flag_SOC)));
            longestNP.setText(String.format("%d", longest.get(DaysEventHelper.C.flag_NP)));
            longestKET.setText(String.format("%d", longest.get(DaysEventHelper.C.flag_KET)));

            datesNCH.setText(longest.getDateString(DaysEventHelper.C.flag_NCH));
        }

        private boolean isInStreak(Cursor cursor, String columnName) {
            return cursor.getInt(cursor.getColumnIndexOrThrow(columnName)) == 1;

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
            // if we don't have any values then we can't be in a streak
            if (cursor.getCount() < 1) {
                return false;
            }

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


    }

    public void setCurrentStreaks() {
        new SetCurrentStreaksAsyncTask().execute();
    }
}
