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
package com.fujitsu.dc.mx.jersey;

import static org.fest.assertions.Assertions.assertThat;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.fujitsu.dc.mx.categories.Integration;
import com.fujitsu.dc.mx.jersey.http.DcHttpClient;
import com.fujitsu.dc.mx.jersey.http.DcHttpResponse;
import com.sun.jersey.test.framework.JerseyTest;

/**
 * システムの稼働状態取得.
 */
@Category({Integration.class })
public class StatsTest extends JerseyTest {

    /**
     * コンストラクタ.
     */
    public StatsTest() {
        super("com.fujitsu.dc.mx.rs.diskusage");
    }

    /**
     * システムの稼動状態を取得できること.
     * @throws Exception エラー
     */
    @Test
    public void システムの稼動状態を取得できること() throws Exception {
        DcHttpClient httpClient = new DcHttpClient();
        DcHttpResponse response = httpClient.get("/__mx/stats");

        // レスポンスコード
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SC_OK);

        // レスポンスヘッダ
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getPcsVersion()).isNotNull();

        // レスポンスボディ
        JSONObject body = response.toJson();
        assertThat(((JSONObject) body.get("status")).get("systemStatus")).isNotNull();
    }

}
