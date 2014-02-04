package capsrock.beta;

import java.util.Calendar;
import java.util.UUID;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.Button;
import android.widget.TextView;
import capsrock.alpha.R;
import capsrock.beta.Structures.GLocation;
import capsrock.beta.Structures.LocationTimeSheet;
import capsrock.beta.Structures.TimeEntry;
import capsrock.beta.Structures.TimeSheet;
import capsrock.beta.Structures.WebTimeEntry;


//Fragment for the TimeEntry tab
public class TimeEntryFragment extends Fragment implements OnClickListener {
	
    ActivityPasser mCallback;

    // Container Activity must implement this interface
    public interface ActivityPasser {
        public void setStart();
        public void stopStart();
        public void startThread();
        public void stopThread();
        public void handleWBChange(boolean fromAndroid);
        public void handleStop(boolean fromAndroid);
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		  View rootView = inflater.inflate(R.layout.fragment_timeentry_display, container, false);
			((Button) rootView.findViewById(R.id.StartButton)).setOnClickListener(this);
			((Button) rootView.findViewById(R.id.StopButton)).setOnClickListener(this);
			return rootView;
    }
	
	@Override
	public void onClick(View v) {
		onTimeEntry(v, true);
	}
	
	public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (ActivityPasser) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ActivityPasser");
        }
    }
	
	public void onTimeEntry(View v, boolean fromAndroid) {
		
		switch (v.getId()) {
			case R.id.StartButton:
				mCallback.setStart();
				mCallback.startThread();
				mCallback.handleWBChange(fromAndroid);
				break;
			case R.id.StopButton:
				mCallback.stopThread();
				mCallback.stopStart();
				mCallback.handleStop(fromAndroid);
				break;
		}
	}
}
