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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Httpレスポンスを処理するためのクラス.
 */
public class DcHttpResponse {

    private Header[] headers;
    private int statusCode;
    private String body;

    /**
     * テスト用のレスポンス.
     * @param response レスポンス
     * @throws IllegalStateException エラー
     * @throws IOException エラー
     */
    public DcHttpResponse(HttpResponse response) throws IllegalStateException, IOException {
        this.headers = response.getAllHeaders();
        this.statusCode = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();

        // レスポンスボディ取得
        InputStream content = entity.getContent();
        InputStreamReader inputStreamReader = new InputStreamReader(content, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder builder = new StringBuilder();
        String read;
        while ((read = bufferedReader.readLine()) != null) {
            builder.append(read);
        }
        this.body = builder.toString();

        System.out.println("----Response----");
        System.out.println("stats code :" + statusCode);
        System.out.println("headers :");
        for (Header header : headers) {
            System.out.println("    " + header.getName() + ":" + header.getValue());
        }
        System.out.println("body :" + body);

    }

    /**
     * レスポンスをJSONに変換する.
     * @return JSONObject
     * @throws ParseException パースエラー
     */
    public JSONObject toJson() throws ParseException {
        return (JSONObject) new JSONParser().parse(body);
    }

    /**
     * X-Mx-Versionヘッダを取得する.
     * @return X-Mx-Versionヘッダ
     */
    public String getPcsVersion() {
        return getHeader("X-Mx-Version");
    }

    /**
     * Content-Typeヘッダを取得する.
     * @return Content-Typeヘッダ
     */
    public String getContentType() {
        return getHeader(HttpHeaders.CONTENT_TYPE);
    }

    private String getHeader(String headerName) {
        for (Header header : headers) {
            if (headerName.equals(header.getName())) {
                return header.getValue();
            }
        }
        return null;
    }

    /**
     * ステータスコートを取得する.
     * @return ステータスコード
     */
    public int getStatusCode() {
        return this.statusCode;
    }
}

