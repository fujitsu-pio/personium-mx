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

import org.json.simple.JSONObject;

import com.fujitsu.dc.mx.model.diskusage.DiskUsages;

/**
 * DC-MX Disk使用量APIのレスポンスボディビルダークラス.
 */
public class ResponseBodyBuilder {

    private ResponseBodyBuilder() {
    }

    /**
     * レスポンスボディを生成するために各ボリュームのディスク使用量をJSONObjectで返却する.
     * @param diskUsages 各ボリュームのディスク使用量のリスト
     * @return JSON形式のレスポンスボディ
     */
    public static JSONObject getDiskUsage(DiskUsages diskUsages) {
        return diskUsages.toJson();
    }
}
