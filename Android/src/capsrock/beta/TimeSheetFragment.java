package capsrock.beta;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import capsrock.alpha.R;

//Fragment for the TimeSheet tab
//Not yet implemented
public class TimeSheetFragment extends Fragment {
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timesheet_display, container, false);
        return rootView;
    }
}
