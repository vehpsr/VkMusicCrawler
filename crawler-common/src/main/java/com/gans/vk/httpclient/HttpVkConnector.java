package com.gans.vk.httpclient;

import static com.gans.vk.context.SystemProperties.NumericProperty.VK_PORT;
import static com.gans.vk.context.SystemProperties.Property.VK_HEADER_CONTENT_TYPE;
import static com.gans.vk.context.SystemProperties.Property.VK_HEADER_COOKIES;
import static com.gans.vk.context.SystemProperties.Property.VK_HEADER_USER_AGENT;
import static com.gans.vk.context.SystemProperties.Property.VK_HOST;
import static com.gans.vk.context.SystemProperties.Property.VK_LOGIN;
import static com.gans.vk.context.SystemProperties.Property.VK_PASS;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.gans.vk.context.SystemProperties;

public class HttpVkConnector {

    private static final Log LOG = LogFactory.getLog(HttpVkConnector.class);
    private static HttpClient _httpClient = getHttpClient();
    private static HttpVkConnector _instance = new HttpVkConnector();

    private HttpVkConnector(){}

    public static HttpVkConnector getInstance() {
        return _instance;
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

    public String get(String url) {
        if (StringUtils.isEmpty(url)) {
            return "";
        }
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Cookie", SystemProperties.get(VK_HEADER_COOKIES));
        httpGet.setHeader("User-Agent", SystemProperties.get(VK_HEADER_USER_AGENT));
        return executeHttpMethod(httpGet);
    }

    public String post(String url, String postEntity) {
        if (StringUtils.isEmpty(url)) {
            return "";
        }
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Cookie", SystemProperties.get(VK_HEADER_COOKIES));
        httpPost.setHeader("User-Agent", SystemProperties.get(VK_HEADER_USER_AGENT));
        httpPost.setHeader("Content-type", SystemProperties.get(VK_HEADER_CONTENT_TYPE));
        try {
            httpPost.setEntity(new StringEntity(postEntity));
        } catch (UnsupportedEncodingException e) {
            LOG.error(e.getMessage());
            return "";
        }
        return executeHttpMethod(httpPost);
    }

    private String executeHttpMethod(HttpUriRequest method) {
        try {
            HttpResponse response = _httpClient.execute(method);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                LOG.error(MessageFormat.format("Fail to reach {0}, response: {1}", method.getURI(), response.getStatusLine().getStatusCode()));
                return "";
            }

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity);
            }
        } catch (Exception e) {
            if (e instanceof ClientProtocolException || e instanceof IOException) {
                LOG.error(MessageFormat.format("Fail to parse response from {0}: {1}", method.getURI(), e.getMessage()));
            } else {
                throw new IllegalStateException("System error", e);
            }
        }
        return "";
    }
}
