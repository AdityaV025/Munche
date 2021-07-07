package utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class JSONParser {
    static JSONObject jObj = null;

    HttpURLConnection urlConnection = null;
    // variable to hold context
    private Context context;
    // constructor
    public JSONParser(Context context){
        this.context=context;
    }


    public JSONObject makeHttpRequest(String url, String method, String params) {

        // boolean isReachable =Config.isURLReachable(context);
        // Making HTTP request
        try {
            String retSrc="";
            char current = '0';

            URL url1 = new URL(url);
            // check for request method
            HttpURLConnection urlConnection = (HttpURLConnection) url1.openConnection();
            if (method == "POST") {
                // request method is POST
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setFixedLengthStreamingMode(params.getBytes().length);
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                out.print(params);
                out.close();
            }
            InputStream in = urlConnection.getInputStream();

            byte[] bytes = new byte[10000];
            StringBuilder x = new StringBuilder();
            int numRead = 0;
            while ((numRead = in.read(bytes)) >= 0) {
                x.append(new String(bytes, 0, numRead));
            }
            retSrc=x.toString();

            jObj = new JSONObject(retSrc);
        } catch (Exception e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, "Connectivity issue. Please try again later.", Toast.LENGTH_LONG).show());
            return null;
        }finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return jObj;
    }
}