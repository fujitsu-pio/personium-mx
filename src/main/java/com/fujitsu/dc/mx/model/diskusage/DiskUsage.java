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

import org.json.simple.JSONObject;

import com.fujitsu.dc.mx.DcMxException;

/**
 * 一つのボリュームに対するディスク使用量の情報を格納するクラス.
 */
public class DiskUsage {

    /**
     * ボリュームステータス.
     */
    public static enum Status {
        /** OK. */
        OK,
        /** FULL. */
        FULL,
        /** ERROR. */
        ERROR
    };

    private String volume;
    private long volumeDiskSize;
    private long allocatedDiskSize;
    private long usedDiskSize;
    private Status status;
    private Exception error;

    private DiskUsage(String volume, Exception error) {
        this.volume = volume;
        this.status = Status.ERROR;
        this.error = error;
    }

    /**
     * エラー情報を格納したディスク使用量の情報を取得する.
     * @param volume ボリューム名
     * @param error Exception
     * @return エラー情報を格納したディスク使用量の情報
     */
    public static DiskUsage getErrorInstance(String volume, Exception error) {
        return new DiskUsage(volume, error);
    }

    /**
     * コンストラクタ.
     * @param volume ボリューム名
     * @param volumeDiskSize ディスクサイズ
     * @param usedDiskSize 使用済みディスクサイズ
     * @param threshold しきい値
     */
    public DiskUsage(String volume, long volumeDiskSize, long usedDiskSize, double threshold) {
        this.volume = volume;
        this.volumeDiskSize = volumeDiskSize;
        this.usedDiskSize = usedDiskSize;
        Double realAllocatedDiskSize = volumeDiskSize * threshold;
        this.allocatedDiskSize = realAllocatedDiskSize.longValue();
        this.status = getUsageStatus();
    }

    /**
     * ボリューム名を取得.
     * @return ボリューム名
     */
    public String getVolume() {
        return volume;
    }

    /**
     * ディスクサイズを取得.
     * @return ディスクサイズ
     */
    public long getVolumeDiskSize() {
        return volumeDiskSize;
    }

    /**
     * ディスクの使用可能サイズを取得（空き領域では無く、ユーザが使用可能な領域のサイズ）.
     * @return ディスクの使用可能サイズ
     */
    public long getAllocatedDiskSize() {
        return allocatedDiskSize;
    }

    /**
     * ディスクの使用済みサイズを取得.
     * @return ディスクの使用済みサイズ
     */
    public long getUsedDiskSize() {
        return usedDiskSize;
    }

    /**
     * ステータスを取得.
     * @return ステータス
     */
    public Status getStatus() {
        return this.status;
    }

    private Status getUsageStatus() {
        if (this.allocatedDiskSize <= this.usedDiskSize) {
            return Status.FULL;
        }
        return Status.OK;
    }

    /**
     * 一つのボリュームに対するディスク使用量の情報をJSON形式で取得する.
     * @return 一つのボリュームに対するディスク使用量の情報
     */
    @SuppressWarnings("unchecked")
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("volume", getVolume());

        if (this.error == null) {
            json.put("volumeDiskSize", getVolumeDiskSize());
            json.put("allocatedDiskSize", getAllocatedDiskSize());
            json.put("usedDiskSize", getUsedDiskSize());
        } else {
            this.status = Status.ERROR;
            JSONObject errorJson = new JSONObject();
            json.put("error", errorJson);
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
        json.put("status", this.status.name());
        return json;
    }
}
