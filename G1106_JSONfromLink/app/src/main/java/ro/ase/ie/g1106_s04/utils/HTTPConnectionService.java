package ro.ase.ie.g1106_s04.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

public class HTTPConnectionService {
    private String URL;
    private HttpsURLConnection HTTPConnection;

    public HTTPConnectionService(String urlAddress) {
        this.URL = urlAddress;
    }

    public String getData() {
        return getDataFromHttp();
    }

    private String getDataFromHttp()
    {
        StringBuilder result = new StringBuilder();
        try {
            HTTPConnection = (HttpsURLConnection) new URL(URL).openConnection();
            InputStream inputStream  = HTTPConnection.getInputStream();
            InputStreamReader isw = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isw);
            String line ="";
            while((line = br.readLine())!=null)
            {
                result.append(line);
            }

            br.close();
            isw.close();
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result.toString();
    }
}
