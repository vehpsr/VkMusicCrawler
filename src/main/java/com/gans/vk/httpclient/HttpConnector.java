package com.gans.vk.httpclient;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;

import com.gans.vk.context.SystemProperties;
import static com.gans.vk.context.SystemProperties.NumericProperty.*;
import static com.gans.vk.context.SystemProperties.Property.*;

public class HttpConnector {

    private static HttpClient _httpClient = getHttpClient();

    private HttpConnector(){/* singleton */}

    public static HttpClient getInstance() {
        return _httpClient;
    }

    private static HttpClient getHttpClient() {
        String login = SystemProperties.get(VK_LOGIN);
        String pass = SystemProperties.get(VK_PASS);
        String host = SystemProperties.get(VK_HOST);
        int port = SystemProperties.get(VK_PORT, 80);

        HttpClientBuilder httpClient = HttpClientBuilder.create();
        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(
                new AuthScope(host, port),
                new UsernamePasswordCredentials(login, pass)
            );
        httpClient.setDefaultCredentialsProvider(provider);

        return httpClient.build();
    }
}
