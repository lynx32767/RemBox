package rembox.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class IPGrabber {

    private static String cachedIP = "";
    private static long lastConnectionTime = 0;

    public static String getRawIP() throws IOException {
        boolean useLocalhost = new File("data\\USE_LOCALHOST.txt").exists();

        //IP is read from servers only every 30 seconds at most to minimize traffic
        if(System.currentTimeMillis() - lastConnectionTime >= 30000 && !useLocalhost) {
            //Fetch server IP from pastebin and connect
            URL url = new URL("https://pastebin.com/raw/########"); //Replace the #s with your own Pastebin ID, where you save the IP you want to connect to
            cachedIP = new BufferedReader(new InputStreamReader(url.openStream())).readLine();
        } else if(useLocalhost) {
            cachedIP = "localhost:32767";
        }

        lastConnectionTime = System.currentTimeMillis();
        return cachedIP;
    }

}
