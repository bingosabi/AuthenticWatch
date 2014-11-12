package com.listomni.authenticwatch;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class AuthenticWatch extends Activity {

    private TextView mTime, mComment, mDate;
    private int mCurrentBattery;
    private String mCurrentComment;
    private SimpleDateFormat mTimeFormatter, mDateFormatter;
    private Random mRand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentic_watch);
        mTimeFormatter = new SimpleDateFormat(TIME_FORMAT_DISPLAYED);
        mDateFormatter = new SimpleDateFormat(DATE_FORMAT_DISPLAYED);
        mRand = new Random();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTime = (TextView) stub.findViewById(R.id.watch_time);
                mDate = (TextView) stub.findViewById(R.id.watch_date);
                mComment = (TextView) stub.findViewById(R.id.watch_comment);
                mTimeInfoReceiver.onReceive(AuthenticWatch.this, registerReceiver(null, INTENT_FILTER));    //  Here, we're just calling our onReceive() so it can set the current time.
                registerReceiver(mTimeInfoReceiver, INTENT_FILTER);
                registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            }
        });
        Log.d("AuthenticWatch", "On Create");
    }

    private final static IntentFilter INTENT_FILTER;
    static {
        INTENT_FILTER = new IntentFilter();
        INTENT_FILTER.addAction(Intent.ACTION_TIME_TICK);
        INTENT_FILTER.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        INTENT_FILTER.addAction(Intent.ACTION_TIME_CHANGED);
    }

    private final String TIME_FORMAT_DISPLAYED = "h:mm a";
    private final String DATE_FORMAT_DISPLAYED = "E d";

    private BroadcastReceiver mTimeInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent intent) {
            updateWatchFace();
        }
    };

    @Override
    protected void onResume() {
        Log.d("AuthenticWatch","ON Resume");
        super.onResume();
    }


    @Override
    protected void onStart() {
        Log.d("AuthenticWatch", "ON Start");
        updateWatchFace();
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d("AuthenticWatch", "ON Stop");
        super.onStop();
        mCurrentComment = null;
        mDate.setText("    ");
        mComment.setText("            ");
        mTime.setText("   ");
    }

    @Override
    protected void onDestroy() {
        Log.d("AuthenticWatch","Destroyed");
        super.onDestroy();
        unregisterReceiver(mTimeInfoReceiver);
        unregisterReceiver(mBatInfoReceiver);
    }


    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent intent) {
            mCurrentBattery = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        }
    };

    private void updateWatchFace() {
        if (mTime != null && mComment != null && mDate != null) {
            Calendar theCal = Calendar.getInstance();
            Date theTime = theCal.getTime();
            mTime.setText(
                    mTimeFormatter
                            .format(theTime));

            mDate.setText(
                    mDateFormatter
                            .format(theTime));

            if (mCurrentComment == null) {
                HashMap<Integer, String> holidays = new HashMap<Integer, String>();
                ArrayList<String> thingICouldSay = new ArrayList<String>();

                int hour = theCal.get(Calendar.HOUR_OF_DAY);
                int minute = theCal.get(Calendar.MINUTE);
                int dayOfWeek = theCal.get(Calendar.DAY_OF_WEEK);
                int dayOfYear = theCal.get(Calendar.DAY_OF_YEAR);
                int dayQuality = 9;
                int currentLocation = 0;  //0==Home 1==Work 2==NYC 3==OTHER  4==A Bar!

                if (hour > 23 || hour < 5) {
                    thingICouldSay.add("SLEEPY\nTIME!!");
                    thingICouldSay.add("ZZZZZZ");
                }

                if (hour >= 1 && hour < 5) {
                    thingICouldSay.add("GO BACK\nTO BED!!");
                }

                if (hour >= 5 && hour <= 8) {
                    thingICouldSay.add("TOO\nFUCKING\nEARLY!!");
                }

                if (hour >= 16 && hour <= 18) {
                    thingICouldSay.add("WINE\nO'CLOCK!!");
                    thingICouldSay.add("  WINE  \nTIME!!");
                }

                if (hour > 11 && hour < 13) {
                    thingICouldSay.add("LUNCH\nTIME!!");
                    thingICouldSay.add("  NOM!  \n  NOM!  \n  NOM!  ");
                }



                if (hour > 18 && hour < 20) {
                    thingICouldSay.add("DINNER\nTIME!!");
                    thingICouldSay.add("NOM!\nNOM!\nNOM!");
                }

                if (dayOfWeek == Calendar.SUNDAY) {
                    thingICouldSay.add("WEEKEND'S\nOVER!!");
                    if (hour > 10 && hour < 12) {
                        thingICouldSay.add("BRUNCH\nTIME!!");
                    }
                } else if (dayOfWeek == Calendar.SATURDAY) {
                    thingICouldSay.add("SATURDAY!!");
                    thingICouldSay.add("WEEKEND'S\nHERE!!");
                    if (hour > 10 && hour < 13) {
                        thingICouldSay.add("BRUNCH\nTIME!!");
                    }
                } else if (dayOfWeek == Calendar.FRIDAY) {
                    thingICouldSay.add("WEEKEND'S\nHERE!!");
                } else if (dayOfWeek == Calendar.MONDAY) {
                    thingICouldSay.add("meh.");
                    thingICouldSay.add("FUCK IT.");
                    thingICouldSay.add("MONDAYS\nSUCK.");
                } else if (dayOfWeek == Calendar.WEDNESDAY) {
                    thingICouldSay.add("HUMPDAY!!");
                } else {
                    thingICouldSay.add("meh.");
                }

                mCurrentComment = thingICouldSay.get(mRand.nextInt(thingICouldSay.size()));
            }

            String comment = mCurrentComment;


            int colorID = Color.WHITE;
            if (mCurrentBattery == 0) {
                //No Reading yet.
            } else if (mCurrentBattery < 5) {
                comment = "FUCKING\nCHARGE\nME!!";
                colorID = Color.RED;
            } else if (mCurrentBattery < 10) {
                colorID = Color.RED;
            } else if (mCurrentBattery < 20) {
                colorID = Color.YELLOW;
            }

            mComment.setText(comment);

            mDate.setTextColor(colorID);
            mTime.setTextColor(colorID);
            mComment.setTextColor(colorID);
        }
    }

    public void onScreenDim() {
        Log.d("AuthenticWatch","Screen Dimmed");
        updateWatchFace();
        mComment = null;
    }

    public void onScreenAwake() {
        Log.d("AuthenticWatch","Screen aWoke");
        updateWatchFace();
    }
}
