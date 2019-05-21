package org.sofwerx.torgi.service;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

public class UDPSwarmSender implements Runnable {

    private final static SimpleDateFormat dateFormatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    // Networking variables
    // using UDP
    private DatagramSocket socket;
    private InetAddress ip;
    private Thread send, receive, listen;
    private String address;
    private int port, id = -1;


    // constructor
    public UDPSwarmSender(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;

    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void run() {

        try {
            socket = new DatagramSocket();
            socket.setReuseAddress(true);


            Log.d("UDP", "Attemping connection to " + address);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        listen();

    }


    // opening connection
    public boolean openConnection() {

        run();

        return true;
    }

    // receiving messages
    // waits for a packet
    public String receive() {
        byte[] data = new byte[1024];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        while (true) {
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String message = new String(packet.getData());
            return message;
        }

    }

    // sending message over UDP
    public void send(final byte[] data) {
        send = new Thread("Send") {
            @Override
            public void run() {
                DatagramPacket packet = new DatagramPacket(data, data.length,
                        ip, port);
                appendLog(new String(data));
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        send.start();
    }

    // listen method
    public void listen() {
        Log.d("UDP Swarm", "Listening...");
        listen = new Thread("Swarm Listen er") {
            @Override
            public void run() {
                while (true) {
                    String message = receive();
                    appendLog(message);
                }
            }
        };
        listen.start();
    }

    public void appendLog(String text) {
        File logFile = new File("/sdcard/Download/swarm-io.log");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                buf.append(LocalDateTime.now().toString()+",");
            }
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
