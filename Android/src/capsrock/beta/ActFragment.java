package capsrock.beta;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import capsrock.alpha.R;

//Fragment for the Activities tab
//Not yet implemented
public class ActFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
	    View rootView = inflater.inflate(R.layout.fragment_act_display, container, false);
	    return rootView;
	}
}
