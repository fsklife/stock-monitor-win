package org.fsk.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * @author fanshk
 * @date 2020/9/2 15:36
 */
@Slf4j
public class OkHttpUtil {
    private static final OkHttpClient client = new OkHttpClient();

    public static final String MEDIA_TYPE_JSON = "application/json;charset=UTF-8";
    public static final String MEDIA_TYPE_TEXT = "text/plain;charset=UTF-8";

    public static String post(String url, String body) throws IOException {
        return post(url, null, null, MEDIA_TYPE_JSON, body);
    }

    public static String post(String url, Map<String, String> headerMap, String body) throws IOException {
        return post(url, headerMap, null, MEDIA_TYPE_JSON, body);
    }

    public static String post(String url, Map<String, String> headerMap, Map<String, Object> paramsMap, String body) throws IOException {
        return post(url, headerMap, paramsMap, MEDIA_TYPE_JSON, body);
    }

    public static String post(String url, Map<String, String> headerMap, Map<String, Object> paramsMap,
                              String mediaType, String body) throws IOException {
        RequestBody requestBody = RequestBody.create(MediaType.parse(
                mediaType), body);
        url = concatUrlParams(url, paramsMap);

        Request request = generateRequest(url, requestBody, headerMap);
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String result = response.body().string();
            log.info("post请求，url：{}，headers：{}，params：{}，body：{}，返回结果：{} ", url, headerMap, paramsMap, body, result);
            return result;
        }
    }

    public static String postForm(String url, Map<String, String> headerMap, Map<String, String> paramsMap) throws IOException {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        Set<String> keySet = paramsMap.keySet();
        for (String key : keySet) {
            String value = paramsMap.get(key);
            formBodyBuilder.add(key, value);
        }
        FormBody formBody = formBodyBuilder.build();
        Request request = generateRequest(url, formBody, headerMap);
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String result = response.body().string();
            log.info("post请求，url：{}，headers：{}，form-params：{}，返回结果：\n{} ", url, headerMap, paramsMap, result);
            return result;
        }
    }

    private static Request generateRequest(String url, RequestBody requestBody, Map<String, String> headerMap) {
        Request.Builder builder = new Request.Builder();
        builder.url(url).post(requestBody);
        if (headerMap != null && !headerMap.isEmpty()) {
            headerMap.forEach((k, v) -> {
                builder.addHeader(k, v);
            });
        }
        Request request = builder.build();
        return request;
    }

    public static String get(String url, Map<String, String> headerMap) {
        return get(url, headerMap, null);
    }

    public static String get(String url, Map<String, String> headerMap, Map<String, Object> paramsMap) {
        url = concatUrlParams(url, paramsMap);
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        if(headerMap != null && !headerMap.isEmpty()) {
            headerMap.forEach((k, v) -> {
                builder.addHeader(k, v);
            });
        }
        Request request = builder.build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String result = response.body().string();
            log.info("get请求，url：{}, 返回结果：\n{} ", url, result);
            return result;
        } catch (Exception e) {

        }
        return null;
    }

    private static String concatUrlParams(String url, Map<String, Object> paramsMap) {
        if (paramsMap != null && !paramsMap.isEmpty()) {
            StringBuilder sb = new StringBuilder("?");
            paramsMap.forEach((k, v) -> {
                sb.append(k).append("=").append(v).append("&");
            });
            String str = sb.toString();
            url = url + str.substring(0, str.length() - 1);
        }
        return url;
    }
}
