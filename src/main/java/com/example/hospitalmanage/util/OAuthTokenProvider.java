package com.example.hospitalmanage.util;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

@Component
public class OAuthTokenProvider {


    @Value("${spring.security.oauth2.client.introspection-uri}")
    private String TOKEN_URI;
    @Value("${spring.security.oauth2.client.client-id}")
    private String CLIENT_ID;
    @Value("${spring.security.oauth2.client.client-secret}")
    private String CLIENT_SECRET;
    @Value("${spring.security.oauth2.client.scope}")
    private String SCOPE;
    @Value("${spring.security.oauth2.client.grand-type}")
    private String GRANT_TYPE;

    private String token;

    private Date saveTokenDate;
    private Date expiredTokenTime;

    public OAuthTokenProvider() { }

    public OAuthTokenProvider(String TOKEN_URI,
                              String CLIENT_ID,
                              String CLIENT_SECRET,
                              String SCOPE,
                              String GRANT_TYPE,
                              String token,
                              Date saveTokenDate,
                              Date expiredTokenTime) {
        this.TOKEN_URI = TOKEN_URI;
        this.CLIENT_ID = CLIENT_ID;
        this.CLIENT_SECRET = CLIENT_SECRET;
        this.SCOPE = SCOPE;
        this.GRANT_TYPE = GRANT_TYPE;
        this.token = token;
        this.saveTokenDate = saveTokenDate;
        this.expiredTokenTime = expiredTokenTime;
    }

    public void createTokenICD() throws IOException {

        URL url = new URL(TOKEN_URI);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestMethod("POST");

        // set parameters to post
        String urlParameters =
                "client_id=" + URLEncoder.encode(CLIENT_ID, "UTF-8") +
                        "&client_secret=" + URLEncoder.encode(CLIENT_SECRET, "UTF-8") +
                        "&scope=" + URLEncoder.encode(SCOPE, "UTF-8") +
                        "&grant_type=" + URLEncoder.encode(GRANT_TYPE, "UTF-8");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONObject jsonObj = new JSONObject(response.toString());
        saveTokenDate = new Date();
        expiredTokenTime = new Date();
        expiredTokenTime.setMinutes(expiredTokenTime.getMinutes() + 20);
        this.token = jsonObj.getString("access_token");
    }

    public String getToken() throws IOException {
        createTokenICD();
        return token;
    }

}
