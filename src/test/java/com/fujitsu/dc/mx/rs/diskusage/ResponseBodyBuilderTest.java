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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

import com.fujitsu.dc.mx.DcMxException;
import com.fujitsu.dc.mx.DcMxMessageId;
import com.fujitsu.dc.mx.model.diskusage.DiskUsage;
import com.fujitsu.dc.mx.model.diskusage.DiskUsages;

/**
 * DC-MX Disk使用量APIのリソースクラス.
 */
public class ResponseBodyBuilderTest {

    /**
     * ディスク使用量のリストが空のリストの場合にvolumeStatusが空配列となること.
     */
    @Test
    public void ディスク使用量のリストが空のリストの場合にvolumeStatusが空配列となること() {
        DiskUsages diskUsages = new DiskUsages();

        JSONObject json = ResponseBodyBuilder.getDiskUsage(diskUsages);
        JSONArray volumeStatus = (JSONArray) ((JSONObject) json.get("status")).get("volumeStatus");
        assertTrue(volumeStatus.isEmpty());
    }

    /**
     * ディスク使用量のリストの要素が1件の場合に正常に取得できること.
     */
    @Test
    public void ディスク使用量のリストの要素が1件の場合に正常に取得できること() {
        DiskUsages diskUsages = new DiskUsages();
        DiskUsage usage = new DiskUsage("elasticsearch1", 10000000L, 7000000L, 0.9);
        diskUsages.add(usage);

        JSONObject response = ResponseBodyBuilder.getDiskUsage(diskUsages);
        JSONObject status = (JSONObject) response.get("status");
        assertEquals("OK", status.get("systemStatus"));

        JSONArray volumeStatus = (JSONArray) status.get("volumeStatus");
        assertEquals(1, volumeStatus.size());
        JSONObject json = (JSONObject) volumeStatus.get(0);
        assertEquals("elasticsearch1", json.get("volume"));
        assertEquals(10000000L, json.get("volumeDiskSize"));
        assertEquals(9000000L, json.get("allocatedDiskSize"));
        assertEquals(7000000L, json.get("usedDiskSize"));
        assertEquals("OK", json.get("status"));
    }

    /**
     * ディスク使用量のリストの要素が複数の場合に正常に取得できること.
     */
    @Test
    public void ディスク使用量のリストの要素が複数の場合に正常に取得できること() {
        DiskUsages diskUsages = new DiskUsages();
        DiskUsage usage1 = new DiskUsage("elasticsearch1", 10000000L, 7000000L, 0.9);
        diskUsages.add(usage1);

        DiskUsage usage2 = new DiskUsage("_ads", 10000000L, 5000000L, 0.9);
        diskUsages.add(usage2);

        DiskUsage usage3 = new DiskUsage("dav", 10000000L, 5000000L, 0.9);
        diskUsages.add(usage3);

        JSONObject response = ResponseBodyBuilder.getDiskUsage(diskUsages);
        JSONObject status = (JSONObject) response.get("status");
        assertEquals("OK", status.get("systemStatus"));

        JSONArray volumeStatus = (JSONArray) status.get("volumeStatus");
        assertEquals(3, volumeStatus.size());
        JSONObject json1 = (JSONObject) volumeStatus.get(0);
        assertEquals("elasticsearch1", json1.get("volume"));
        assertEquals(10000000L, json1.get("volumeDiskSize"));
        assertEquals(9000000L, json1.get("allocatedDiskSize"));
        assertEquals(7000000L, json1.get("usedDiskSize"));
        assertEquals("OK", json1.get("status"));
        JSONObject json2 = (JSONObject) volumeStatus.get(1);
        assertEquals("_ads", json2.get("volume"));
        assertEquals(10000000L, json2.get("volumeDiskSize"));
        assertEquals(9000000L, json2.get("allocatedDiskSize"));
        assertEquals(5000000L, json2.get("usedDiskSize"));
        assertEquals("OK", json2.get("status"));
        JSONObject json3 = (JSONObject) volumeStatus.get(2);
        assertEquals("dav", json3.get("volume"));
        assertEquals(10000000L, json3.get("volumeDiskSize"));
        assertEquals(9000000L, json3.get("allocatedDiskSize"));
        assertEquals(5000000L, json3.get("usedDiskSize"));
        assertEquals("OK", json3.get("status"));
    }

    /**
     * ディスク使用量の取得ができない場合にstatusがERRORで返却されること.
     */
    @Test
    public void ディスク使用量の取得ができない場合にstatusがERRORで返却されること() {
        DiskUsages diskUsages = new DiskUsages();
        DiskUsage usage = DiskUsage.getErrorInstance("elasticsearch1",
                new DcMxException(new DcMxMessageId("MX500-SV-0003")));
        diskUsages.add(usage);

        JSONObject response = ResponseBodyBuilder.getDiskUsage(diskUsages);
        JSONObject status = (JSONObject) response.get("status");
        assertEquals("ERROR", status.get("systemStatus"));

        JSONArray volumeStatus = (JSONArray) status.get("volumeStatus");
        assertEquals(1, volumeStatus.size());
        JSONObject json = (JSONObject) volumeStatus.get(0);
        assertEquals("elasticsearch1", json.get("volume"));
        assertEquals("ERROR", json.get("status"));
        assertTrue(json.containsKey("error"));
        JSONObject error = (JSONObject) json.get("error");
        assertEquals("MX500-SV-0003", error.get("code"));
        assertTrue(error.containsKey("message"));
        JSONObject message = (JSONObject) error.get("message");
        assertEquals("en", message.get("lang"));
        assertEquals("Failed to read disk status.", message.get("value"));
    }

    /**
     * ディスク使用量の取得で想定外のエラーが発生した場合にstatusがERRORで返却されること.
     */
    @Test
    public void ディスク使用量の取得で想定外のエラーが発生した場合にstatusがERRORで返却されること() {
        DiskUsages diskUsages = new DiskUsages();
        DiskUsage usage = DiskUsage.getErrorInstance("elasticsearch1", new Exception("test message"));
        diskUsages.add(usage);

        JSONObject response = ResponseBodyBuilder.getDiskUsage(diskUsages);
        JSONObject status = (JSONObject) response.get("status");
        assertEquals("ERROR", status.get("systemStatus"));

        JSONArray volumeStatus = (JSONArray) status.get("volumeStatus");
        assertEquals(1, volumeStatus.size());
        JSONObject json = (JSONObject) volumeStatus.get(0);
        assertEquals("elasticsearch1", json.get("volume"));
        assertEquals("ERROR", json.get("status"));
        assertTrue(json.containsKey("error"));
        JSONObject error = (JSONObject) json.get("error");
        assertEquals("MX500-SV-0999", error.get("code"));
        assertTrue(error.containsKey("message"));
        JSONObject message = (JSONObject) error.get("message");
        assertEquals("en", message.get("lang"));
        assertEquals("test message", message.get("value"));
    }

    /**
     * ディスク使用量の取得制御レベルで問題が発生した場合にsysytemstatusがERRORで返却されること.
     */
    @Test
    public void ディスク使用量の取得制御レベルで問題が発生した場合にsysytemstatusがERRORで返却されること() {
        DiskUsages diskUsages = new DiskUsages();
        DiskUsage usage = new DiskUsage("elasticsearch1", 10000000L, 7000000L, 0.9);
        diskUsages.add(usage);
        diskUsages.setError(new DcMxException(new DcMxMessageId("MX500-SV-0002")));

        JSONObject response = ResponseBodyBuilder.getDiskUsage(diskUsages);
        JSONObject status = (JSONObject) response.get("status");
        assertEquals("ERROR", status.get("systemStatus"));
        assertTrue(status.containsKey("error"));
        JSONObject systemError = (JSONObject) status.get("error");
        assertEquals("MX500-SV-0002", systemError.get("code"));
        assertTrue(systemError.containsKey("message"));
        JSONObject systemErrorMessage = (JSONObject) systemError.get("message");
        assertEquals("en", systemErrorMessage.get("lang"));
        assertEquals("Failed to set system status.", systemErrorMessage.get("value"));

        JSONArray volumeStatus = (JSONArray) status.get("volumeStatus");
        assertEquals(1, volumeStatus.size());
        JSONObject json = (JSONObject) volumeStatus.get(0);
        assertEquals("elasticsearch1", json.get("volume"));
        assertEquals(10000000L, json.get("volumeDiskSize"));
        assertEquals(9000000L, json.get("allocatedDiskSize"));
        assertEquals(7000000L, json.get("usedDiskSize"));
        assertEquals("OK", json.get("status"));
    }

    /**
     * ディスク使用量の取得制御レベルで想定外の例外が発生した場合にsysytemstatusがERRORで返却されること.
     */
    @Test
    public void ディスク使用量の取得制御レベルで想定外の例外が発生した場合にsysytemstatusがERRORで返却されること() {
        DiskUsages diskUsages = new DiskUsages();
        DiskUsage usage = new DiskUsage("elasticsearch1", 10000000L, 7000000L, 0.9);
        diskUsages.add(usage);
        diskUsages.setError(new Exception("test message"));

        JSONObject response = ResponseBodyBuilder.getDiskUsage(diskUsages);
        JSONObject status = (JSONObject) response.get("status");
        assertEquals("ERROR", status.get("systemStatus"));
        assertTrue(status.containsKey("error"));
        JSONObject systemError = (JSONObject) status.get("error");
        assertEquals("MX500-SV-0999", systemError.get("code"));
        assertTrue(systemError.containsKey("message"));
        JSONObject systemErrorMessage = (JSONObject) systemError.get("message");
        assertEquals("en", systemErrorMessage.get("lang"));
        assertEquals("test message", systemErrorMessage.get("value"));

        JSONArray volumeStatus = (JSONArray) status.get("volumeStatus");
        assertEquals(1, volumeStatus.size());
        JSONObject json = (JSONObject) volumeStatus.get(0);
        assertEquals("elasticsearch1", json.get("volume"));
        assertEquals(10000000L, json.get("volumeDiskSize"));
        assertEquals(9000000L, json.get("allocatedDiskSize"));
        assertEquals(7000000L, json.get("usedDiskSize"));
        assertEquals("OK", json.get("status"));
    }

    /**
     * ディスク使用量がディスク使用の上限値を超えた場合にstatusがFULLで返却されること.
     */
    @Test
    public void ディスク使用量がディスク使用の上限値を超えた場合にstatusがFULLで返却されること() {
        DiskUsages diskUsages = new DiskUsages();
        DiskUsage usage = new DiskUsage("elasticsearch1", 10000000L, 9000000L, 0.9);
        diskUsages.add(usage);

        JSONObject response = ResponseBodyBuilder.getDiskUsage(diskUsages);
        JSONObject status = (JSONObject) response.get("status");
        assertEquals("FULL", status.get("systemStatus"));

        JSONArray volumeStatus = (JSONArray) status.get("volumeStatus");
        assertEquals(1, volumeStatus.size());
        JSONObject json = (JSONObject) volumeStatus.get(0);
        assertEquals("elasticsearch1", json.get("volume"));
        assertEquals(10000000L, json.get("volumeDiskSize"));
        assertEquals(9000000L, json.get("allocatedDiskSize"));
        assertEquals(9000000L, json.get("usedDiskSize"));
        assertEquals("FULL", json.get("status"));
    }

}
