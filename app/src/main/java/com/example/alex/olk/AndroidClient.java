package com.example.alex.olk;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;



public class AndroidClient {

    public static final int STATE = 0;
    public static final int JOINTS = 1;
    public static final int DEKART = 2;
    public static final int HOME = 3;
    public static final int HOME2 = 4;

    AndroidClient(){
        Log.e("Android","2Конструктe11111ор");
    }
    Socket socket;
    DataOutputStream out;
    DataInputStream in;
    int rotations[] = new int [6];
    int positions[] = new int [6];
    boolean flgOpenSocket=false;

    public int[] getRotations() {
        return rotations;
    }

    public int[] getPositions() {
        return positions;
    }

    void openSocket(int serverPort, String address){
        Log.e("SCOKET","openin socket");
        try {
            InetAddress ipAddress = InetAddress.getByName(address); // создаем объект который отображает вышеописанный IP-адрес.
            socket = new Socket(ipAddress, serverPort); // создаем сокет используя IP-адрес и порт сервера.

            // Берем входной и выходной потоки сокета, теперь можем получать и отсылать данные клиентом.
            InputStream sin = socket.getInputStream();
            OutputStream sout = socket.getOutputStream();

            // Конвертируем потоки в другой тип, чтоб легче обрабатывать текстовые сообщения.
            in = new DataInputStream(sin);
            out = new DataOutputStream(sout);

            flgOpenSocket = true;
            Log.e("SCOKET","works");

        } catch (Exception x) {
            Log.e("SOCKET_ERROR",x+"");
        }
    }

    void sendData(int tp,int arr[])  {
        try {
            out.writeUTF(String.format("%5d ", tp));
            for (int a : arr) {
                out.writeUTF(String.format("%5d ", a));
            }
            out.write('\n');
        }catch (Exception e ){

        }
    }

    void sendState(Integer ar[]) throws IOException {
        if (flgOpenSocket) {
            Integer[] arr = new Integer[9];
            for (int i = 0; i < 6; i++) {
                arr[i + 3] = ar[i];
            }
            arr[1] = 27;
            arr[0] = 0;
            arr[2] = 0;
            sendVals2(arr);
        }
        //Log.e("STATE",str);
    }

    void home(){
        Integer [] arr = new Integer[9];
        arr[1] = 10;
        sendVals2(arr);
    }
    void home2(){
        Integer [] arr = new Integer[9];
        arr[1] = 11;
        sendVals2(arr);
    }

    void sendJoints(int j1,int j2, int j3, int j4, int j5, int j6)  {
        Integer arr[] = {0,8,0,j1,j2,j3,j4,j5,j6};
        sendVals2(arr);
    }

    void sendDekart(int x,int y, int z, int o, int a, int t)  {
        Integer arr[] = {0,9,0,x,y,z,o,a,t};
        sendVals2(arr);
    }

    public void close() {
        flgOpenSocket = false;
    }


    void sendVals2(Integer[] iVal) {

        if (flgOpenSocket) {
            String s = "";
            for (int i = 0; i < 9; i++)
                s += getVali(iVal[i], 6) + " ";
//s+="/"
            try {
                Log.e("SENDED2",s);
                for (char c: s.toCharArray())
                    out.writeByte((byte)c);
            } catch (IOException e) {
                Log.e("SENDED2","shit: "+e);
                e.printStackTrace();
            }
        }
    }

    public static String getVali(Integer i,Integer ln){
        String s = Integer.toString(i);
        String minusS = "";
        if (i<0){
            s = s.substring(1,s.length());
            minusS = "-";
            ln--;
        }

        String tmpS = "";
        if (s.length()<ln){
            for (int j=s.length();j<ln;j++)
                tmpS = tmpS+"0";
            return minusS+tmpS+s;
        }else if( s.length()>ln){
            return minusS+s.substring(0, ln);
        }else return minusS+s;
    }


}
