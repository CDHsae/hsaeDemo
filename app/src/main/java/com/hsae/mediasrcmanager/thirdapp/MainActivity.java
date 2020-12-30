package com.hsae.mediasrcmanager.thirdapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private IAgentService myservice;

    private Button btn_vedioStatus;
    private Button btn_Handbrake;
    private Button btn_Bindservice;

    private TextView canPlayTv;
    private TextView mHandbrakeStatusTv;
    public static String TAG = "IAgentService";
    private TestBroadRecevier mTestBroadRecevier;


    private static final String ACTION_CONTROL_PLAYSTATUS = "com.hsae.action.wheel.control.playstatus";
    private static final String ACTION_SRCMANAGER_CANPLAY = "com.hsae.action.srcmanager.canplay";
    private static final String ACTION_SRCMANAGER_HANDBREAK = "com.hsae.action.srcmanager.hand_brake";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initReceiver();

    }

    private void initReceiver() {
        mTestBroadRecevier = new TestBroadRecevier();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_CONTROL_PLAYSTATUS);
        intentFilter.addAction(ACTION_SRCMANAGER_CANPLAY);
        intentFilter.addAction(ACTION_SRCMANAGER_HANDBREAK);
        registerReceiver(mTestBroadRecevier, intentFilter);
    }

    private void initView() {
        btn_Bindservice = (Button) findViewById(R.id.bindservice);
        btn_vedioStatus = (Button) findViewById(R.id.btn_video);
        btn_Handbrake = (Button) findViewById(R.id.btn_brake);
        canPlayTv = (TextView) findViewById(R.id.canplay);
        mHandbrakeStatusTv = (TextView) findViewById(R.id.brakeStatus);

        btn_vedioStatus.setOnClickListener(this);
        btn_Handbrake.setOnClickListener(this);
        btn_Bindservice.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        switch (v.getId()) {
            case R.id.bindservice:
                startService();
                break;

            case R.id.btn_video:
                try {
                    if (myservice != null) {
                        boolean canplay = myservice.getVideoCanplay();
                        canPlayTv.setText("播放状态为  ： " + canplay);
                    } else {
                        Toast.makeText(MainActivity.this, "Please bind the service first", Toast.LENGTH_SHORT).show();
                    }
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;

            case R.id.btn_brake:
                try {
                    if (myservice != null) {
                        int status = myservice.getHandbrakeStatus();
                        mHandbrakeStatusTv.setText("获取手刹状态 :  " + status);
                    } else {
                        Toast.makeText(MainActivity.this, "Please bind the service first", Toast.LENGTH_SHORT).show();
                    }
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }

    }

    private void startService() {
        Intent intent = new Intent();
        intent.setAction("com.hsae.mediasrcmanager.thirdapp.AgentService");
        intent.setPackage("com.hsae.mediasrcmanager");
        bindService(intent, conn, BIND_AUTO_CREATE);
        Toast.makeText(getApplicationContext(), "start service", Toast.LENGTH_LONG).show();
    }

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            myservice = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            myservice = IAgentService.Stub.asInterface(service);
        }
    };


    private class TestBroadRecevier extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "onReceive: action : " + action);
            if (action.equals(ACTION_CONTROL_PLAYSTATUS)) {
                int playStatus = intent.getIntExtra("playStatus", -2);
                Log.i(TAG, "onReceive: playStatus =" + playStatus);
                playStatus(playStatus);
            } else if (action.equals(ACTION_SRCMANAGER_CANPLAY)) {
                int canPlay = intent.getIntExtra("canPlay", -1);
                Log.i(TAG, "onReceive: canPlay =" + canPlay);
                canPlay(canPlay);
            } else if (action.equals(ACTION_SRCMANAGER_HANDBREAK)) {
                int handbrake = intent.getIntExtra("handbrake", -1);
                Log.i(TAG, "onReceive: handbrake =" + handbrake);
                myToast("手刹状态 :  " + handbrake);
            }

        }
    }

    private void playStatus(int status) {
        switch (status) {
            case 0:
                myToast("playStatus:播放或者暂停 ");
                break;
            case 1:
                myToast("playStatus:下一曲");
                break;
            case -1:
                myToast("playStatus:上一曲");
                break;
            default:
                break;
        }
    }

    private void canPlay(int status) {
        if (0 == status) {
            myToast("canPlay: 不能播放");
        } else if (1 == status) {
            myToast("canPlay: 能播放");
        }
    }


    private void myToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mTestBroadRecevier);
        super.onDestroy();
    }
}