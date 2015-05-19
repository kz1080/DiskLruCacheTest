package com.mlxy.disklrucachetest.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkAdministrator {
    /** 打开指定地址的输入流。 */
    public static InputStream openUrlInputStream(String url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        return urlConnection.getInputStream();
    }
}
