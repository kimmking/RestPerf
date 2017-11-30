package io.github.kimmking.restperf;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kk on 2017/11/30.
 */
public class PerfApi {

    public final static String SITE = "http://192.168.20.3:8080";

    final static PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager( );
    //manager.setMaxTotal ( 50);
    static RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).setConnectTimeout ( 60000 ).build();
    static CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).setConnectionManager(manager).setConnectionManagerShared(true).build();
    static String LoginURL = "/api/login/loginUser";
    static String token = "{\"loginSrc\": \"FeiJiaYunChainClient\", \"loginAccount\": \"saas_test_2003\"," +
            " \"pwd\": null, \"password\": \"00000000\"}";

    private static final String APPLICATION_JSON = "application/json";
    //private static final String CONTENT_TYPE_TEXT_JSON = "text/json";

    public static void login(){

        try {
            long startTime = System.currentTimeMillis();

            //StringEntity entity = new StringEntity(URLEncoder.encode(token, HTTP.UTF_8));
            StringEntity entity = new StringEntity(token, Consts.UTF_8);
            entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
            entity.setContentType(APPLICATION_JSON);

            HttpPost post = new HttpPost(SITE+LoginURL);
            post.setEntity(entity);
            HttpResponse response = httpClient.execute(post);

            post.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);

            long endTime = System.currentTimeMillis();
            int statusCode = response.getStatusLine().getStatusCode();

            System.out.println("statusCode:" + statusCode);
            System.out.println("invoke time/ms：" + (endTime - startTime));
            if (statusCode != HttpStatus.SC_OK) {
                System.out.println("Method failed:" + response.getStatusLine());
            }

            // Read the response body
            System.out.println( EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        try {
            List<String> lines = IOUtils.readLines(PerfApi.class.getClassLoader().getResourceAsStream("api.txt"));

            login();

            for (String api : lines){

                String res = fetch(api);
                //System.out.println(res + " " + api);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String fetch(String api){

        try{
            String url = api;

            long startTime = System.currentTimeMillis();

            HttpGet get = new HttpGet(SITE+url);

            HttpResponse response = httpClient.execute(get);


            long endTime = System.currentTimeMillis();
            int statusCode = response.getStatusLine().getStatusCode();

            //System.out.println("statusCode:" + statusCode);
            System.out.println( (endTime - startTime) + ","+ url );
            if (statusCode != HttpStatus.SC_OK) {
                System.out.println("Method failed:" + response.getStatusLine());
            }
            EntityUtils.toString(response.getEntity());
            //System.out.println( EntityUtils.toString(response.getEntity()));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "";
    }

}
