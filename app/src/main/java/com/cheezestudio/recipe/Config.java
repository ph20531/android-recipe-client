package com.cheezestudio.recipe;

public class Config {
    // aws
    // private static final String HOST = "https://2lfpserotg.execute-api.ap-northeast-2.amazonaws.com/dev";

    // localhost
    // 안드로이드에서는 localhost를 127.0.0.1이 아니라 10.0.2.2로 사용한다.
    private static final String HOST = "http://10.0.2.2:5000";

    public static String getHost() {
        return HOST;
    }

    private static final String DEFAULT_URL = HOST + "/recipes";

    private static final int DEFAULT_METHOD = 0;

    private static final String DEFAULT_HEADERS = "[{\"first\": \"Authorization\", \"second\": \"Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJmcmVzaCI6ZmFsc2UsImlhdCI6MTcxNjc4NTMzMiwianRpIjoiYjQ3YTUzMDctOTg0Yi00MzcwLWEyYjYtZDMyMzkzZTY4YzU2IiwidHlwZSI6ImFjY2VzcyIsInN1YiI6NTEsIm5iZiI6MTcxNjc4NTMzMiwiY3NyZiI6IjlkNmQ0Y2JmLWIxZDgtNGVhZi04N2E3LTIzMjIyNjVlOTI5MiJ9.E4yGdMyCTcsxIaJNhFCqjhs91XBM2sj1V_Vh8mja9xs\"}]";

    private static final String DEFAULT_PARAMS = "[{\"first\": \"offset\", \"second\": 0},{\"first\": \"limit\", \"second\": 25}]";

    private static final String DEFAULT_BODY = "";

    public static String getDefaultUrl() {
        return DEFAULT_URL;
    }

    public static int getDefaultMethod() {
        return DEFAULT_METHOD;
    }

    public static String getDefaultHeaders() {
        return DEFAULT_HEADERS;
    }

    public static String getDefaultParams() {
        return DEFAULT_PARAMS;
    }

    public static String getDefaultBody() {
        return DEFAULT_BODY;
    }
}