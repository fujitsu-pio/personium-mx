/**
 * personium.io
 * Copyright 2014 FUJITSU LIMITED
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fujitsu.dc.mx.jersey.http;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * Httpクライアントクラス.
 */
public class DcHttpClient {

    /** リクエスト送信先URLを取得するプロパティのキー. */
    private static final String PROP_TARGET_URL = "com.fujitsu.dc.test.target";

    // FIXME ローカルホストのJerseyポートで実行すると404となってしまうため、デフォルトIT環境にしてあります
    private final String baseUrl = System.getProperty(PROP_TARGET_URL,
            "https://shinoda.vpdc.sg.soft.fujitsu.com");

    /** テストで使用するhttpClient. */
    private CloseableHttpClient httpclient;

    /**
     * テスト用のDcHttpClient.
     * @throws NoSuchAlgorithmException エラー
     * @throws KeyStoreException エラー
     * @throws KeyManagementException エラー
     */
    public DcHttpClient() throws NoSuchAlgorithmException, KeyStoreException,
            KeyManagementException {
        if (baseUrl.startsWith("https")) {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
            this.httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } else {
            this.httpclient = HttpClients.createDefault();
        }
    }

    /**
     * HTTP Getリクエストを実行する.
     * @param requestPath リクエストパス
     * @return レスポンス
     * @throws IOException リクエスト実行中にエラー
     */
    public DcHttpResponse get(String requestPath) throws IOException {
        HttpGet httpGet = new HttpGet(baseUrl + requestPath);
        HttpResponse response = httpclient.execute(httpGet);
        return new DcHttpResponse(response);
    }
}
