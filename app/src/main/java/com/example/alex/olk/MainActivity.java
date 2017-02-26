
package com.example.alex.olk;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {

    TextView textResponse;
    EditText textRequest;
    Button buttonConnect, buttonClear, buttonSend, buttonDisconnect;

    Socket socket;
    String socketOutMessage = "";
    PrintWriter out = null;
    DataInputStream in = null;
    Timer myTimer;

    void home(){
        addtoOutMessage(new int[]{0, Constants.C_HOME1, 0, 0, 0, 0, 0, 0, 0});
    }
    void home2(){
        addtoOutMessage(new int[]{0, Constants.C_HOME2, 0, 0, 0, 0, 0, 0, 0});
    }
    void addtoOutMessage(int[] arr) {
        for (int i = 0; i < 9; i++) {
            socketOutMessage += Constants.getVali(arr[i], 6) + " ";
        }
    }

    boolean openSocket() {
        try {
            socket = new Socket("192.168.1.57", 30000);
            in = new DataInputStream(socket.getInputStream());
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    void processSocket() {
        // читаем все данные из сокета
        try {
            String s = "";
            while (in.available() != -0) {
                // читаем символ
                char c = (char) in.readByte();
                s += c;
            }
            if (!s.equals("")) {
                Log.d("IN_TEXT_FROM_SOCKET",s);
                textResponse.setText(s);
            }
        } catch (IOException e) {
            Log.d("IN_TEXT_SOCKET_ERROR",""+e);
        }
        // пытаемся вывести все накопившиеся данные в сокет
        if (socketOutMessage != "" && out != null) {
            out.print(socketOutMessage);
            socketOutMessage = "";
            out.flush();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonConnect = (Button) findViewById(R.id.btnConnect);
        buttonDisconnect = (Button) findViewById(R.id.btnDisconnect);
        buttonClear = (Button) findViewById(R.id.btnClear);
        textResponse = (TextView) findViewById(R.id.responseText);
        textRequest = (EditText) findViewById(R.id.requestText);
        buttonSend = (Button) findViewById(R.id.btnSend);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addtoOutMessage(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
            }
        });

        buttonDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                myTimer.cancel();
            }
        });
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                new MyClientTask().execute();
            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textResponse.setText("");
            }
        });
        myTimer = new Timer(); // Создаем таймер
        findViewById(R.id.buttonHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home();
            }
        });
        findViewById(R.id.buttonHome2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)  {
                home2();
            }
        });
    }

    public class MyClientTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (openSocket()) {
                myTimer.schedule(new TimerTask() { // Определяем задачу
                    @Override
                    public void run() {
                        processSocket();
                    }
                }, 0L, 1000); // интервал - 60000 миллисекунд, 0 миллисекунд до первого запуска
            }
            return null;
        }
    }
}