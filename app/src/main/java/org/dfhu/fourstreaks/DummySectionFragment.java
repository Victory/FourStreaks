package org.dfhu.fourstreaks;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DummySectionFragment extends Fragment {

    public static final String DUMMY_POSITION = "dummyPosition";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dummyView = inflater.inflate(R.layout.dummy, container, false);
        Bundle args = getArguments();
        TextView textView = (TextView) dummyView.findViewById(R.id.dummyText);
        textView.setText(String.format("%d", args.getInt(DUMMY_POSITION)));
        return dummyView;
    }
}

