package com.example.hospitalmanage.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@Component
@AllArgsConstructor
@NoArgsConstructor
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
