package com.stopwatch;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static String TAG = MainActivity.class.getName();
	private static long SLEEP_TIME = 1; // Sleep for some time
	private TextView timeText;
	
	// states: running, paused, stopped
	private String state;
    private int curTime;
    
    private Button startButton;
	private Button stopButton;
	private Button pauseButton;

	@Override
	public void onSaveInstanceState(Bundle bundle) {
	    super.onSaveInstanceState(bundle);
	    // store data in the bundle
	    bundle.putInt("CURTIME", curTime);
	    state = "paused";
	    System.out.println("on save state");
	}
	
	@Override
	public void onRestart(){
		super.onRestart();
		state = "paused";
		System.out.println("on restart");
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	  super.onRestoreInstanceState(savedInstanceState);
	  // Restore UI state from the savedInstanceState.
	  // This bundle has also been passed to onCreate.
	  state = "paused";
	  curTime = savedInstanceState.getInt("CURTIME");
	  
	  System.out.println("on restore state");
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        startButton = (Button) findViewById(R.id.start);
    	stopButton = (Button) findViewById(R.id.stop);
    	pauseButton = (Button) findViewById(R.id.pause);
        
    	startButton.setOnClickListener(new View.OnClickListener() {
    	    @Override
    	    public void onClick(View v) {
    	    	startTimer();
    	    }
    	});
    	
    	stopButton.setOnClickListener(new View.OnClickListener() {
    	    @Override
    	    public void onClick(View v) {
    	    	stopTimer();
    	    }
    	});
    	
    	pauseButton.setOnClickListener(new View.OnClickListener() {
    	    @Override
    	    public void onClick(View v) {
    	    	pauseTimer();
    	    }
    	});
        
        timeText = (TextView) findViewById(R.id.timeView);
 
        // TODO: Get start time here

        // Start timer and launch main activity
        ClockUpdater clockUpdater = new ClockUpdater();
        
        if(savedInstanceState == null){
        	curTime = 0;
        	state = "stopped";
        	System.out.println("instance state is null");
        }
        else{
        	System.out.println("instance state is NOT null");
        }
        clockUpdater.start();
    }
	
	private class ClockUpdater extends Thread {
        @Override
        /**
         * Sleep for some time and than start new activity.
         */
        public void run() {
            try {
                // Sleeping
                while (true) {
                	//sleep for SLEEP_TIME (set to 1 milisecond)
                    Thread.sleep(SLEEP_TIME);
                    
                    if(state == "running"){
                    	curTime++;
                        updateTimeText(curTime);
                    }
                    else if(state == "stopped"){
                    	//reset the start time if the clock is stopped
                    	curTime = 0;
                    	updateTimeText(0);
                    }
                    //else the timer is paused and keeps the old value
                }

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

        }
        
        private void updateTimeText(int time){
        	
        	int mili = time;
            int seconds = time / 1000;
            int minutes = seconds / 60;
            int hours = minutes / 60;
            mili = mili % 1000;
            seconds = seconds % 60;
            
            // Set new values
            
            Message msg = new Message();
            msg.obj = String.format("%d:%02d:%02d.%d", hours, minutes, seconds, mili);
            timeViewHandler.sendMessage(msg);
        }
    }

	public void startTimer(){
		if(state == "stopped"){
			state = "running";
		}	
    }
    
    public void stopTimer(){
		state = "stopped";
    }
    
    public void pauseTimer(){
    	if(state == "paused"){
    		state = "running";
    	}
    	else if(state == "running"){
    		state = "paused";
    	}
    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    Handler timeViewHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String time = (String) msg.obj;
            timeText.setText(time);
        }
    };
}
