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
package com.fujitsu.dc.mx.rs.diskusage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujitsu.dc.mx.DcMxConfig;
import com.fujitsu.dc.mx.model.diskusage.DiskUsages;
import com.fujitsu.dc.mx.process.DiskUsageMainProcess;

/**
 * DC-MX Disk使用量APIのリソースクラス.
 */
@Path("/stats")
public class DiskUsageResource {

    static Logger log = LoggerFactory.getLogger(DiskUsageResource.class);

    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String MX_HEADER = "X-Mx-Version";
    private static final int HTTP_RESPONSE_STATUS = 200;

    /**
     * リソースパス /stats に対する GETメソッドの処理.
     * @return JSON形式のレスポンス
     */
    @GET
    public Response getStats() {
        DiskUsages diskUsages = new DiskUsages();
        try {
            DiskUsageMainProcess processor = new DiskUsageMainProcess();
            // Disk使用量の取得
            diskUsages = processor.collectDiskUsages();
            // 上記で取得された Disk使用量情報を基に、PCS ReadDeleteOnlyモードを設定
            processor.handlePCSMode(diskUsages);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            diskUsages.setError(e);
        }

        // レスポンスボディの生成
        JSONObject response = ResponseBodyBuilder.getDiskUsage(diskUsages);
        return Response.status(HTTP_RESPONSE_STATUS)
                .header(MX_HEADER, DcMxConfig.getMxVersion())
                .header(CONTENT_TYPE_HEADER, "application/json")
                .entity(response.toJSONString())
                .build();
    }
}
