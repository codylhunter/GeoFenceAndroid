/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package capsrock.beta;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import capsrock.alpha.R;
import capsrock.beta.Structures.GLocation;
import capsrock.beta.Structures.LocationTimeSheet;
import capsrock.beta.Structures.TimeEntry;
import capsrock.beta.Structures.TimeSheet;
import capsrock.beta.Structures.WebTimeEntry;
import capsrock.beta.TimeEntryFragment.ActivityPasser;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements ActivityPasser, ActionBar.TabListener {

    AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    ViewPager mViewPager;
	private final static UUID PEBBLE_APP_UUID = UUID.fromString("08b8ed0c-d3c1-49c6-88ba-ad34f0732e94");

	public String startDate;
	Calendar strDate;
	public Thread thr;
	private TimeSheet timeSheet;
	LocationTimeSheet loca;
	TimeEntry newTE, oldTE;
	private Button startBtn;
	PebbleDictionary PebbleData;
	Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message m) {
			if (m.obj != null)
				if (findViewById(R.id.time) != null)
					((TextView) findViewById(R.id.time)).setText((String)m.obj);
		}
	};
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timeSheet = new TimeSheet();
        PebbleData = new PebbleDictionary();
        //Set up to Receive messages from the pebble and handle them correctly
		PebbleKit.registerReceivedDataHandler(this, new PebbleKit.PebbleDataReceiver(PEBBLE_APP_UUID) {
			@Override
			public void receiveData(final Context context, final int transactionId, final PebbleDictionary data) {
				String mode = data.getString(1);
				//((TextView)findViewById(R.id.pebbleText)).setText(mode);
				if (mode.substring(9).equals("Break") || mode.substring(9).equals("Work")) {
				((TimeEntryFragment) getSupportFragmentManager().findFragmentByTag(
	                       "android:switcher:"+R.id.pager+":0")).onTimeEntry(findViewById(R.id.StartButton), false);
				}
				else {
					((TimeEntryFragment) getSupportFragmentManager().findFragmentByTag(
		                       "android:switcher:"+R.id.pager+":0")).onTimeEntry(findViewById(R.id.StopButton), false);
				}
				PebbleKit.sendAckToPebble(getApplicationContext(), transactionId);
			}
		});
		//Set up the timer thread
		thr = new Thread(new Runnable() {
	        @Override
	        public void run() {
	            while (true) {
	                try {
	                    Thread.sleep(1000);
	                    mHandler.post(new Runnable() {
	                        @Override
	                        public void run() {
	                        	Message mes = new Message();
	                        	if (strDate != null) {
									long seconds = Calendar.getInstance().getTimeInMillis() - strDate.getTimeInMillis();
		                        	long minutes = seconds / 1000 / 60;
		                        	minutes %= 60;
		                        	
		                        	long hours = seconds / 1000 / 60 / 60;
		                        	hours %= 24;
		                        	
		                        	seconds /= 1000;
		                        	seconds %= 60;
		                        	
		                        	String sec = hours + ":" + minutes + ":" + seconds;
		                        	mes.obj = sec;
		                        	mHandler.sendMessage(mes);
	                        	}
	                        	else {
	                        		mes.obj = "00:00:00";
	                        		mHandler.sendMessage(mes);
	                        	}
	                        }
	                    });
	                } catch (Exception e) {
	                   
	                }
	            }
	        }
	    });
		
		//Start Login Screen
		Intent intent = new Intent();
		intent.setClassName("capsrock.beta", "capsrock.beta.LoginActivity");
		//startActivity(intent);
		setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
                
               
            }
            
            
        });


        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(actionBar.newTab().setText(mAppSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
    	if (thr.isAlive()) {
      	   thr.interrupt();
         }
    	mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new TimeEntryFragment();
                case 1:
                	
                	return new TimeSheetFragment();
                case 2:
                	return new ActFragment();
                //Default case is never reached
                default:
                	throw new RuntimeException("Reached a tab that doesn't exist");
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
        	switch(position) {
	        	case 0: return "Time Entry";
	        	case 1: return "Time Sheet";
	        	case 2: return "Activities";
	        	default: return "BAD TAB";
        	}
        }
    }
    /* --------------------------------
     * Implement Interface Methods Here
     * -------------------------------*/
    
	//Set the start time to now
	public void setStart() {
		strDate = Calendar.getInstance();
	}
	//Nullify the start time
	public void stopStart() {
		strDate = null;
	}
	//Stop the timer thread
	public void stopThread() {
		thr.interrupt();
	}
	//Start the timer thread
	public void startThread() {
		if (!thr.isAlive())
			thr.start();
	}
	//Dummy Function Until We get locations running
	public String getLocation() {
		return "main street";
	}
	/*
	 * Handle the TIMEENTRY UI when start button is pressed or Work/Break message
	 * is received from the watch
	 */
	public void handleWBChange(boolean fromAndroid) {
		//If there is no location time sheet, make a new. Else use found Sheet
		if(timeSheet.findLocaSheet(getLocation()) == null) {
			loca = new LocationTimeSheet(new GLocation("123", getLocation()));
			timeSheet.AddLocaSheet(loca);
		}
		else {
			loca = timeSheet.findLocaSheet(getLocation());
		}
		
		//Set Time Entry to the open entry. If there is an open entry, complete it with an
		//end time and send the data to the server.
		oldTE = loca.GetOpenEntry();
		if (oldTE != null) {
			oldTE.AddEndTime();
			new sendToServer().execute(new WebTimeEntry(oldTE, loca.location.name));
		}
		
		//Update the UI on both the pebble and android depending on what state you are in
		startBtn = (Button)findViewById(R.id.StartButton);
		if (startBtn.getText().equals("Log Work Time")) {
			startBtn.setText("Log Break Time");
			PebbleData.addString(0, "work");
			oldTE = new TimeEntry(true);		
		}
		else {
			startBtn.setText("Log Work Time");
			PebbleData.addString(0, "break");
			oldTE = new TimeEntry(false);
		}
		loca.AddTimeEntry(oldTE);
		if (fromAndroid)
			PebbleKit.sendDataToPebble(this, PEBBLE_APP_UUID, PebbleData);
	}
	//Handle the TIMEENTRY UI when the stop button is pressed or stop message
	//is received from the watch. 
	public void handleStop(boolean fromAndroid) {
		((Button)findViewById(R.id.StartButton)).setText("Log Work Time");
		((TextView)findViewById(R.id.time)).setText("00:00:00");
		if (loca != null) {
			oldTE = loca.GetOpenEntry();
			if (oldTE != null) {
				oldTE.AddEndTime();
				new sendToServer().execute(new WebTimeEntry(oldTE, loca.location.name));
				PebbleData.addString(0, "stop");
				if (fromAndroid)
					PebbleKit.sendDataToPebble(this, PEBBLE_APP_UUID, PebbleData);
				oldTE = null;
			}
		}
	}
}
