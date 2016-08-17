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
package com.fujitsu.dc.mx.model.diskusage;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.fujitsu.dc.mx.DcMxException;
import com.fujitsu.dc.mx.model.diskusage.DiskUsage.Status;

/**
 * ディスク使用量の情報を格納するクラス.
 */
public class DiskUsages extends AbstractList<DiskUsage> {

    private List<DiskUsage> diskUsages;
    private Status systemStatus;
    private Exception error = null;

    /**
     * コンストラクタ.
     */
    public DiskUsages() {
        this.diskUsages = new ArrayList<DiskUsage>();
        this.systemStatus = Status.OK;
    }

    /**
     * ボリューム全体のSystemStatusを取得する.
     * @return ボリューム全体のSystemStatus
     */
    public Status getSystemStatus() {
        return this.systemStatus;
    }

    private void setStatus(Status diskStatus) {
        if (this.systemStatus.equals(Status.ERROR)) {
            return;
        }
        if (this.systemStatus.equals(Status.FULL) && diskStatus.equals(Status.ERROR)) {
            this.systemStatus = diskStatus;
            return;
        }
        if (this.systemStatus.equals(Status.OK)) {
            this.systemStatus = diskStatus;
        }
    }

    @Override
    public boolean add(DiskUsage usage) {
        boolean res = this.diskUsages.add(usage);
        setStatus(usage.getStatus());
        return res;
    }

    /**
     * エラー情報を設定する.
     * @param e Exception
     */
    public void setError(Exception e) {
        this.systemStatus = Status.ERROR;
        this.error = e;
    }

    /**
     * ディスク使用量の情報をJSON形式に変換する.
     * @return ディスク使用量の情報
     */
    @SuppressWarnings("unchecked")
    public JSONObject toJson() {
        JSONObject responseJson = new JSONObject();
        JSONObject statusJson = new JSONObject();
        responseJson.put("status", statusJson);

        JSONArray volumeStatusesJson = new JSONArray();
        for (DiskUsage diskUsage : diskUsages) {
            JSONObject volumeStatusJson = diskUsage.toJson();
            volumeStatusesJson.add(volumeStatusJson);
        }
        statusJson.put("volumeStatus", volumeStatusesJson);
        if (this.error != null) {
            this.systemStatus = Status.ERROR;
            JSONObject errorJson = new JSONObject();
            statusJson.put("error", errorJson);
            if (error instanceof DcMxException) {
                errorJson.put("code", ((DcMxException) error).getMessageId());
            } else {
                errorJson.put("code", "MX500-SV-0999");
            }
            JSONObject errorMessageJson = new JSONObject();
            errorJson.put("message", errorMessageJson);
            errorMessageJson.put("lang", "en");
            errorMessageJson.put("value", error.getMessage());
        }
        statusJson.put("systemStatus", systemStatus.name());
        return responseJson;
    }

    @Override
    public DiskUsage get(int index) {
        return this.diskUsages.get(index);
    }

    @Override
    public int size() {
        return this.diskUsages.size();
    }
}
