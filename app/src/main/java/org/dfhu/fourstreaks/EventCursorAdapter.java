package org.dfhu.fourstreaks;

import android.app.LauncherActivity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import org.dfhu.fourstreaks.DaysEventHelper.C;

public class EventCursorAdapter extends CursorAdapter {
    Context mContext;

    public EventCursorAdapter(Context context, Cursor c, boolean flags) {
        super(context, c, flags);
        mContext = context;
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
                int visibility = gym.getVisibility();
                int id = (int) view.getTag();
                if (visibility == View.VISIBLE) {
                    gym.setVisibility(View.INVISIBLE);
                } else {
                    gym.setVisibility(View.VISIBLE);
                }
            }
        });

        final ImageView noWeekend = (ImageView) view.findViewById(R.id.markerNoWeekend);
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int visibility = noWeekend.getVisibility();
                int id = (int) view.getTag();
                if (visibility == View.VISIBLE) {
                    noWeekend.setVisibility(View.INVISIBLE);
                } else {
                    noWeekend.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });


        TextView nch = (TextView) view.findViewById(R.id.markerNCH);
        TextView soc = (TextView) view.findViewById(R.id.markerSOC);
        TextView ket = (TextView) view.findViewById(R.id.markerKET);
        TextView np = (TextView) view.findViewById(R.id.markerNP);
        TextView dateOfEvent = (TextView) view.findViewById(R.id.dateOfEvent);

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
        String dateOfEventString = cursor.getString(cursor.getColumnIndexOrThrow(C.date_of_event));
        dateOfEvent.setText(dateOfEventString);
    }

    private int getColor(int id) {
        return mContext.getResources().getColor(id);
    }
}
