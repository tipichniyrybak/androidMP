package nikifor.fishka;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostRequest extends AsyncTask<String, Void, String> {

    protected String doInBackground(String... args) {


        String json_response = "";
        try{
            Log.e("--------url---------  ", args[0]);
            String url = args[0];
            URL urlObj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Charset", "UTF-8");

            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);

            conn.connect();

            String paramsString = "";  //  " filters: \"{ \"is_selfPlaces\" : false, \"is_Base\" : false, \"is_carAccessibility\": false,  \"is_busAccessibility\": false }\" " ;

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(paramsString);
            wr.flush();
            wr.close();

            try {
                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                json_response = result.toString();
                //Log.e("test", "result from server: " + json_response);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return json_response;
    }
}
