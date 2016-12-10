
package com.example.alex.olk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    public static final int [][]  J_VALS = {
            {360,-360},
            {360, -60},
            {360,-360},
            {0,-360},
            {360,0},
            {40,-40}
    };

    boolean flgDekart;
    SeekBar [] sArr = new SeekBar[6];

    Integer state [] = new Integer [6];
    AndroidClient ac;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for (int i = 0; i < 6; i++) {
            state[i]=0;
        }
        flgDekart = true;
        ac = new AndroidClient();
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                ac.openSocket(40001,"192.168.1.145");
            }
        }).start();


        Timer mTimer =  new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    ac.sendState(state);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("ERRRROR","SDFASFASFAS");
                }

            }
        }, 4000, 500);
        Button btnXm = (Button)findViewById(R.id.btnXm);
        btnXm.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    state[0]=1;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    state[0]=0;
                }
                return true;
            }
        });
        Button btnXp = (Button)findViewById(R.id.btnXp);
        btnXp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    state[1]=1;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    state[1]=0;
                }
                return true;
            }
        });

        Button btnYm = (Button)findViewById(R.id.btnYm);
        btnYm.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    state[2]=1;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    state[2]=0;
                }
                return true;
            }
        });
        Button btnYp = (Button)findViewById(R.id.btnYp);
        btnYp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    state[3]=1;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    state[3]=0;
                }
                return true;
            }
        });
        Button btnZm = (Button)findViewById(R.id.btnZm);
        btnZm.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    state[4]=1;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    state[4]=0;
                }
                return true;
            }
        });
        Button btnZp = (Button)findViewById(R.id.btnZp);
        btnZp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    state[5]=1;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    state[5]=0;
                }
                return true;
            }
        });

        sArr[0] = (SeekBar)findViewById(R.id.seekBar1);
        sArr[1] = (SeekBar)findViewById(R.id.seekBar2);
        sArr[2] = (SeekBar)findViewById(R.id.seekBar3);
        sArr[3] = (SeekBar)findViewById(R.id.seekBar4);
        sArr[4] = (SeekBar)findViewById(R.id.seekBar5);
        sArr[5] = (SeekBar)findViewById(R.id.seekBar6);

        updateSeeks();
        Button btnSend = (Button)findViewById(R.id.btnControl);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendStats();
            }
        });
    }

    void sendStats(){
        String str = "";
        for (int i = 0; i <6 ; i++) {
            int val;
            if (!flgDekart) {
                val = sArr[i].getProgress() + J_VALS[i][1];
            } else {
                val = sArr[i].getProgress() - 1000;
            }
            str += val + " ";
        }
        Log.e("SEEKS",str);
    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_dekart:
                if (checked)
                        flgDekart = false;
                    break;
            case R.id.radio_joints:
                if (checked)
                        flgDekart = true;
                    break;
        }
        updateSeeks();
    }
    void updateSeeks(){
        if (flgDekart){
            for (int j = 0; j < 6; j++) {
                sArr[j].setMax(2000);
                sArr[j].setProgress(ac.getPositions()[j]+1000);
            }
        }else{
            for (int j = 0; j < 6; j++) {
                sArr[j].setMax(J_VALS[j][0]-J_VALS[j][1]);
                sArr[j].setProgress(ac.getRotations()[j]-J_VALS[j][1]);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ac.close();
    }
}
