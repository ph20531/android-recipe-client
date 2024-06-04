package com.cheezestudio.recipe;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APIClient {

    private OkHttpClient client;

    public APIClient() {
        client = new OkHttpClient();
    }

    public JSONObject makeAPIRequest(APIRequest request, RequestType requestType) throws IOException, JSONException {
        Request httpRequest = createHttpRequest(request);

        if (requestType == RequestType.SYNC) {
            Response response = client.newCall(httpRequest).execute();
            String responseData = response.body().string();
            return new JSONObject(responseData);
        } else {
            client.newCall(httpRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // 비동기식 실패 처리
                    request.getListener().onFailure(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        // 비동기식 성공 처리
                        request.getListener().onSuccess(jsonResponse);
                    } catch (JSONException e) {
                        // 비동기식 실패 처리
                        request.getListener().onFailure(e);
                    }
                }
            });
            return null;
        }
    }

    private Request createHttpRequest(APIRequest request) {
        // URL에 쿼리 파라미터 추가
        HttpUrl.Builder httpBuilder = HttpUrl.parse(request.getUrl()).newBuilder();
        if (request.getParams() != null) {
            for (Map.Entry<String, String> entry : request.getParams().entrySet()) {
                httpBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        String finalUrl = httpBuilder.build().toString();

        Request.Builder requestBuilder = new Request.Builder().url(finalUrl);

        // 헤더 추가
        if (request.getHeaders() != null) {
            for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        // 요청 본문 추가
        RequestBody requestBody = null;
        if (request.getBody() != null) {
            requestBody = RequestBody.create(MediaType.parse("application/json"), request.getBody().toString());
        }

        // 요청 메서드 설정
        switch (request.getMethod()) {
            case GET:
                requestBuilder.get();
                break;
            case POST:
                requestBuilder.post(requestBody);
                break;
            case PUT:
                requestBuilder.put(requestBody);
                break;
            case PATCH:
                requestBuilder.patch(requestBody);
                break;
            case DELETE:
                requestBuilder.delete();
                break;
            case HEAD:
                requestBuilder.head();
                break;
            case OPTIONS:
                requestBuilder.method("OPTIONS", null);
                break;
            case CONNECT:
                requestBuilder.method("CONNECT", null);
                break;
            case TRACE:
                requestBuilder.method("TRACE", null);
                break;
            // 다른 HTTP 메서드도 필요에 따라 추가할 수 있습니다.
        }

        return requestBuilder.build();
    }

    public enum HttpMethod {
        GET,
        POST,
        PUT,
        PATCH,
        DELETE,
        HEAD,
        OPTIONS,
        CONNECT,
        TRACE
    }

    public enum RequestType {
        SYNC,
        ASYNC
    }

    public static class APIRequest {
        private String url;
        private HttpMethod method;
        private Map<String, String> headers;
        private JSONObject body;
        private Map<String, String> params;
        private OnAPIResponseListener listener;

        public APIRequest(String url, HttpMethod method) {
            this.url = url;
            this.method = method;
        }

        public APIRequest(String url, HttpMethod method, OnAPIResponseListener listener) {
            this.url = url;
            this.method = method;
            this.listener = listener;
        }

        public void setOnAPIResponseListener(OnAPIResponseListener listener) {
            this.listener = listener;
        }

        public void setHeaders(Map<String, String> headers) {
            this.headers = headers;
        }

        public void setBody(JSONObject body) {
            this.body = body;
        }

        public void setParams(Map<String, String> params) {
            this.params = params;
        }

        public String getUrl() {
            return url;
        }

        public HttpMethod getMethod() {
            return method;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public JSONObject getBody() {
            return body;
        }

        public Map<String, String> getParams() {
            return params;
        }

        public OnAPIResponseListener getListener() {
            return listener;
        }
    }

    public interface OnAPIResponseListener {
        void onSuccess(JSONObject response);

        void onFailure(Exception e);
    }
}