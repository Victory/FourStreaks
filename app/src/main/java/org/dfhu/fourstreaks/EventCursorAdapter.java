package org.dfhu.fourstreaks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import org.dfhu.fourstreaks.DaysEventHelper.C;


public class EventCursorAdapter extends CursorAdapter {

    public interface FillListInterface {
        void fillList();
    }

    MainActivity mContext;

    private class ExtraValuesParameters {
        int rowId;
        float bfValue;
        int wValue;
        int noWeekendFlag;
        int gymFlag;
    }

    public EventCursorAdapter(Context context, Cursor c, boolean flags) {
        super(context, c, flags);
        mContext = (MainActivity) context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        long rowId = cursor.getInt(cursor.getColumnIndexOrThrow(C._id));

        view.setTag(rowId);

        ImageView gym = (ImageView) view.findViewById(R.id.markerGYM);
        TextView nch = (TextView) view.findViewById(R.id.markerNCH);
        TextView soc = (TextView) view.findViewById(R.id.markerSOC);
        TextView ket = (TextView) view.findViewById(R.id.markerKET);
        TextView np = (TextView) view.findViewById(R.id.markerNP);
        TextView w = (TextView) view.findViewById(R.id.w);
        TextView bf = (TextView) view.findViewById(R.id.bf);
        TextView dateOfEvent = (TextView) view.findViewById(R.id.dateOfEvent);
        ImageView noWeekend = (ImageView) view.findViewById(R.id.markerNoWeekend);

        TextView rowIdView = (TextView) view.findViewById(R.id.rowId);
        rowIdView.setText(Long.toString(rowId));

        String dateOfEventString = cursor.getString(cursor.getColumnIndexOrThrow(C.date_of_event));
        dateOfEvent.setText(dateOfEventString);

        final int gymFlag = cursor.getInt(cursor.getColumnIndexOrThrow(C.flag_GYM));
        if (gymFlag == 1) {
            gym.setVisibility(View.VISIBLE);
        }
        final int noWeekendFlag = cursor.getInt(cursor.getColumnIndexOrThrow(C.flag_no_weekend));
        if (noWeekendFlag == 1) {
            noWeekend.setVisibility(View.VISIBLE);
        }

        int nchFlag = cursor.getInt(cursor.getColumnIndexOrThrow(C.flag_NCH));
        if (nchFlag == 1) {
            nch.setBackgroundColor(getColor(R.color.nch));
        } else {
            nch.setBackgroundColor(getColor(R.color.abc_background_cache_hint_selector_material_dark));
        }

        int socFlag = cursor.getInt(cursor.getColumnIndexOrThrow(C.flag_SOC));
        if (socFlag == 1) {
            soc.setBackgroundColor(getColor(R.color.soc));
        } else {
            soc.setBackgroundColor(getColor(R.color.abc_background_cache_hint_selector_material_dark));
        }
        int ketFlag = cursor.getInt(cursor.getColumnIndexOrThrow(C.flag_KET));
        if (ketFlag == 1) {
            ket.setBackgroundColor(getColor(R.color.ket));
        } else {
            ket.setBackgroundColor(getColor(R.color.abc_background_cache_hint_selector_material_dark));
        }
        int npFlag = cursor.getInt(cursor.getColumnIndexOrThrow(C.flag_NP));
        if (npFlag == 1) {
            np.setBackgroundColor(getColor(R.color.np));
        } else {
            np.setBackgroundColor(getColor(R.color.abc_background_cache_hint_selector_material_dark));
        }

        final int wValue = cursor.getInt(cursor.getColumnIndexOrThrow(C.weight));
        if (wValue > 0) {
            w.setText(String.format("w: %d", wValue));
        }

        final float bfValue = cursor.getFloat(cursor.getColumnIndexOrThrow(C.bf));
        if (bfValue > 0) {
            bf.setText(String.format("bf: %.1f", bfValue));
        }





        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int id = (int) view.getTag();

                ExtraValuesParameters extraValues = new ExtraValuesParameters();
                extraValues.rowId = id;
                extraValues.bfValue = bfValue;
                extraValues.wValue = wValue;
                extraValues.noWeekendFlag = noWeekendFlag;
                extraValues.gymFlag = gymFlag;

                openExtraOptionsDialogue(extraValues);

                mContext.fillList();
            }
        });
    }

    private int getColor(int id) {
        return mContext.getResources().getColor(id);
    }

    private void openExtraOptionsDialogue(final ExtraValuesParameters vals) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("The extras will be set here");
        builder.setTitle("Set extras");
        LayoutInflater inflater = mContext.getLayoutInflater();
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.extras_dialog, null);

        final EditText wEdit = (EditText) layout.findViewById(R.id.wEdit);
        if (vals.wValue > 0) {
            wEdit.setText(String.format("%d", vals.wValue));
        }
        final EditText bfEdit = (EditText) layout.findViewById(R.id.bfEdit);
        final Switch toggleNoWeekend = (Switch) layout.findViewById(R.id.toggleNoWeekend);
        toggleNoWeekend.setChecked(vals.noWeekendFlag == 1);

        final Switch toggleGym = (Switch) layout.findViewById(R.id.toggleGym);
        toggleGym.setChecked(vals.gymFlag == 1);

        builder.setView(layout);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        float w;
                        try {
                            w = Float.parseFloat(wEdit.getText().toString());
                        } catch (NumberFormatException exc) {
                            w = 0;
                        }

                        float bf;
                        try {
                            bf = Float.parseFloat(bfEdit.getText().toString());
                        } catch (NumberFormatException exc) {
                            bf = 0;
                        }

                        int noWeekend = toggleNoWeekend.isChecked() ? 1 : 0;
                        int gym = toggleGym.isChecked() ? 1 : 0;

                        DaysEventRow row = new DaysEventRow();
                        row.set(C.weight, w);
                        row.set(C.bf, bf);
                        row.set(C.flag_no_weekend, noWeekend);
                        row.set(C.flag_GYM, gym);

                        DaysEventSource source = new DaysEventSource(mContext);
                        source.updateRow(row, vals.rowId);

                        mContext.fillList();
                    }
                }
        );
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
