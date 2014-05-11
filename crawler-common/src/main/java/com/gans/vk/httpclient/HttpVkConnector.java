package com.gans.vk.httpclient;

import static com.gans.vk.context.SystemProperties.Property.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.text.MessageFormat;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.*;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.*;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.util.EntityUtils;

import com.gans.vk.context.SystemProperties;
import com.gans.vk.utils.RestUtils;

@NotThreadSafe
public class HttpVkConnector {

    private static final Log LOG = LogFactory.getLog(HttpVkConnector.class);
    private static HttpVkConnector _instance = new HttpVkConnector();
    private CloseableHttpClient _httpClient = HttpClients.createDefault();
    private String _cookie = null;

    private HttpVkConnector(){}

    public static HttpVkConnector getInstance() {
        return _instance;
    }

    public String get(String url) {
        if (StringUtils.isEmpty(url)) {
            return "";
        }
        HttpGet httpGet = new HttpGet(url);
        return executeHttpMethod(httpGet);
    }

    public String post(String url, String postEntity) {
        if (StringUtils.isEmpty(url)) {
            return "";
        }
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(new StringEntity(postEntity));
        } catch (UnsupportedEncodingException e) {
            LOG.error(e.getMessage());
            return "";
        }
        return executeHttpMethod(httpPost);
    }

    private String executeHttpMethod(HttpUriRequest method) {
        setHeaders(method);

        CloseableHttpResponse response = null;
        try {
            response = _httpClient.execute(method);
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
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    throw new IllegalStateException("System error", e);
                }
            }
        }
        return "";
    }

    private void setHeaders(HttpUriRequest method) {
        if (StringUtils.isEmpty(_cookie)) {
            _cookie = getAuthenticationCookie();
        }

        method.setHeader("Cookie", _cookie);
        method.setHeader(HttpHeaders.USER_AGENT, SystemProperties.get(VK_HEADER_USER_AGENT));
        method.setHeader(HttpHeaders.CONTENT_TYPE, SystemProperties.get(VK_HEADER_CONTENT_TYPE));
    }

    private String getAuthenticationCookie() {
    	final String VK_SECURITY_COOKIE = "remixsid=";
        String login = SystemProperties.get(VK_LOGIN);
        String pass = SystemProperties.get(VK_PASS);

        CloseableHttpClient sslHttpClient = null;
        CloseableHttpClient httpClient = null;
        try {
            BasicCookieStore cookieStore = new BasicCookieStore();

            SSLContext sslContext = new SSLContextBuilder()
                    .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                    .build();
            sslHttpClient = HttpClients
                    .custom()
                    .setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext))
                    .setDefaultCookieStore(cookieStore)
                    .build();

            String authUrl = MessageFormat.format(SystemProperties.get(VK_AUTH_LOGIN_URL_PATTERN), login, pass);
            HttpPost httpPost = new HttpPost(authUrl);
            HttpResponse response = sslHttpClient.execute(httpPost);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_MOVED_TEMPORARILY) {
                throw new IllegalStateException(MessageFormat.format("Expected redirect after successfull login, but got: {0}", response.getStatusLine().getStatusCode()));
            }

            String redirectUrl = "";
            for (Header location : response.getHeaders(HttpHeaders.LOCATION)) {
                redirectUrl = location.getValue();
            }

            CloseableHttpClient httpclient = HttpClients
                    .custom()
                    .setDefaultCookieStore(cookieStore)
                    .build();

            httpclient.execute(new HttpGet(redirectUrl));

            StringBuilder builder = new StringBuilder();
            String vkDomain = SystemProperties.get(VK_AUTH_COOKIE_DOMAIN);
            List<Cookie> cookies = cookieStore.getCookies();
            for (Cookie cookie : cookies) {
                if (cookie.getDomain().equals(vkDomain)) {
                    builder.append(cookie.getName());
                    builder.append("=");
                    builder.append(cookie.getValue());
                    builder.append(";");
                }
            }
            String result = builder.toString();
            if (!result.contains(VK_SECURITY_COOKIE)) {
                throw new IllegalStateException(MessageFormat.format("Fail to authenticate. Not found secure cookie {0} in response for login {1}", VK_SECURITY_COOKIE, login));
            }
            RestUtils.sleep();
            return result;
        } catch (Exception e) {
            if (e instanceof NoSuchAlgorithmException ||
                    e instanceof KeyStoreException ||
                    e instanceof KeyManagementException ||
                    e instanceof ClientProtocolException ||
                    e instanceof IOException) {
                LOG.error(MessageFormat.format("VK authentication fails with reason: {0}", e.getMessage()));
            }
            throw new IllegalStateException("System error", e);
        } finally {
            try {
                if (sslHttpClient != null) {
                    sslHttpClient.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                throw new IllegalStateException("System error", e);
            }
        }
    }
}
