package org.dfhu.fourstreaks;

import android.app.AlertDialog;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
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

import java.util.concurrent.atomic.AtomicBoolean;

public class EventCursorAdapter extends CursorAdapter {
    MainActivity mContext;
    AtomicBoolean isLongClick = new AtomicBoolean(false);

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

        view.setTag(cursor.getInt(cursor.getColumnIndexOrThrow(C._id)));

        final ImageView gym = (ImageView) view.findViewById(R.id.markerGYM);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLongClick.compareAndSet(true, false)) {
                    return;
                }
                isLongClick.set(false);

                DaysEventRow row = new DaysEventRow();
                int visibility = gym.getVisibility();
                int id = (int) view.getTag();
                if (visibility == View.VISIBLE) {
                    gym.setVisibility(View.INVISIBLE);
                    row.set(C.flag_GYM, 0);
                } else {
                    gym.setVisibility(View.VISIBLE);
                    row.set(C.flag_GYM, 1);
                }
                DaysEventSource source = new DaysEventSource(mContext);
                source.updateRow(row, id);

                mContext.fillList();
            }
        });


        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                isLongClick.set(true);
                int id = (int) view.getTag();
                openExtraOptionsDialogue(id);
                return false;
            }
        });


        TextView nch = (TextView) view.findViewById(R.id.markerNCH);
        TextView soc = (TextView) view.findViewById(R.id.markerSOC);
        TextView ket = (TextView) view.findViewById(R.id.markerKET);
        TextView np = (TextView) view.findViewById(R.id.markerNP);
        TextView w = (TextView) view.findViewById(R.id.w);
        TextView bf = (TextView) view.findViewById(R.id.bf);

        TextView dateOfEvent = (TextView) view.findViewById(R.id.dateOfEvent);
        ImageView noWeekend = (ImageView) view.findViewById(R.id.markerNoWeekend);

        int gymFlag = cursor.getInt(cursor.getColumnIndexOrThrow(C.flag_GYM));
        if (gymFlag == 1) {
            gym.setVisibility(View.VISIBLE);
        }
        int noWeekendFlag = cursor.getInt(cursor.getColumnIndexOrThrow(C.flag_no_weekend));
        if (noWeekendFlag == 1) {
            noWeekend.setVisibility(View.VISIBLE);
        }

        int nchFlag = cursor.getInt(cursor.getColumnIndexOrThrow(C.flag_NCH));
        if (nchFlag == 1) {
            nch.setBackgroundColor(getColor(R.color.nch));
        }
        int socFlag = cursor.getInt(cursor.getColumnIndexOrThrow(C.flag_SOC));
        if (socFlag == 1) {
            soc.setBackgroundColor(getColor(R.color.soc));
        }
        int ketFlag = cursor.getInt(cursor.getColumnIndexOrThrow(C.flag_KET));
        if (ketFlag == 1) {
            ket.setBackgroundColor(getColor(R.color.ket));
        }
        int npFlag = cursor.getInt(cursor.getColumnIndexOrThrow(C.flag_NP));
        if (npFlag == 1) {
            np.setBackgroundColor(getColor(R.color.np));
        }

        int wValue = cursor.getInt(cursor.getColumnIndexOrThrow(C.weight));
        if (wValue > 0) {
            w.setText(String.format("w: %d", wValue));
        }

        float bfValue = cursor.getFloat(cursor.getColumnIndexOrThrow(C.bf));
        if (bfValue > 0) {
            bf.setText(String.format("bf: %.1f", bfValue));
        }

        String dateOfEventString = cursor.getString(cursor.getColumnIndexOrThrow(C.date_of_event));
        dateOfEvent.setText(dateOfEventString);
    }

    private int getColor(int id) {
        return mContext.getResources().getColor(id);
    }

    private void openExtraOptionsDialogue(final int id) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("The extras will be set here");
        builder.setTitle("Set extras");
        LayoutInflater inflater = mContext.getLayoutInflater();
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.extras_dialog, null);

        builder.setView(layout);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText wEdit = (EditText) layout.findViewById(R.id.wEdit);
                        EditText bfEdit = (EditText) layout.findViewById(R.id.bfEdit);
                        Switch toggleNoWeekend = (Switch) layout.findViewById(R.id.toggleNoWeekend);

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

                        DaysEventRow row = new DaysEventRow();
                        row.set(C.weight, w);
                        row.set(C.bf, bf);
                        row.set(C.flag_no_weekend, noWeekend);

                        DaysEventSource source = new DaysEventSource(mContext);
                        source.updateRow(row, id);

                        mContext.fillList();
                    }
                }
        );
        builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
