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
package com.fujitsu.dc.mx.control.diskusage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fujitsu.dc.mx.DcMxException;
import com.fujitsu.dc.mx.DcMxMessageId;
import com.fujitsu.dc.mx.model.diskusage.DiskUsage;
import com.fujitsu.dc.mx.model.diskusage.DiskUsages;

/**
 * テストケース.
 */
public class DiskUsageCreatorTest {

    String volumeName = "elasticsearch1";
    String rootPath = ClassLoader.getSystemResource("").getPath() + File.separator + "diskSizeTest";

    /**
     * テストケースの事前準備.
     */
    @Before
    public void before() {
        // テスト用ディレクトリの作成
        File testDir = new File(rootPath + File.separator + volumeName);
        testDir.mkdirs();
    }

    /**
     * テストケースの事後処理.
     */
    @After
    public void after() {
        // テスト用ディレクトリの削除
        File testDir = new File(rootPath + File.separator + volumeName);
        testDir.delete();

        testDir = new File(rootPath);
        testDir.delete();
    }

    /**
     * ディスク使用量の情報を取得できること.
     * @throws DcMxException エラー
     */
    @Test
    public void ディスク使用量の情報を取得できること() throws DcMxException {
        DiskUsageCreator diskUsageCreator = new DiskUsageCreator(rootPath, "0.9");
        DiskUsages diskUsages = diskUsageCreator.create();

        assertEquals(1, diskUsages.size());
        for (DiskUsage diskUsage : diskUsages) {
            assertNotNull(diskUsage);
            assertEquals("elasticsearch1", diskUsage.getVolume());
            assertTrue(diskUsage.getVolumeDiskSize() > 0);
            assertTrue(diskUsage.getAllocatedDiskSize() > 0);
            assertTrue(diskUsage.getUsedDiskSize() > 0);
        }
    }

    /**
     * ルートディレクトリが存在しない場合ディスク使用量採取でDcMxExceptionがスローされること.
     * @throws DcMxException エラー
     */
    @Test(expected = DcMxException.class)
    public void ルートディレクトリが存在しない場合ディスク使用量採取でDcMxExceptionがスローされること() throws DcMxException {
        try {
            String dummy = "/dummy1234567890";
            DiskUsageCreator diskUsageCreator = new DiskUsageCreator(dummy, "0.9");
            diskUsageCreator.create();
        } catch (DcMxException e) {
            assertEquals("MX500-SV-0003", e.getMessageId());
            throw e;
        }
    }

    /**
     * ルートディレクトリがファイルの場合ディスク使用量採取でDcMxExceptionがスローされること.
     * @throws Exception エラー
     */
    @Test(expected = DcMxException.class)
    public void ルートディレクトリがファイルの場合ディスク使用量採取でDcMxExceptionがスローされること() throws Exception {
        File testFile = new File(rootPath + File.separator + "file.txt");
        try {
            testFile.createNewFile();
            String filePath = testFile.getPath();

            DiskUsageCreator diskUsageCreator = new DiskUsageCreator(filePath, "0.9");
            diskUsageCreator.create();
        } catch (DcMxException e) {
            assertEquals("MX500-SV-0003", e.getMessageId());
            throw e;
        } finally {
            testFile.delete();
        }
    }

    /**
     * ルートパス配下にディレクトリが存在しない場合空のディスク使用量の情報を取得できること.
     * @throws DcMxException エラー
     */
    @Test
    public void ルートパス配下にディレクトリが存在しない場合空のディスク使用量の情報を取得できること() throws DcMxException {
        DiskUsageCreator diskUsageCreator = new DiskUsageCreator(rootPath + File.separator + volumeName, "0.9");
        DiskUsages diskUsages = diskUsageCreator.create();

        assertEquals(0, diskUsages.size());
    }

    /**
     * ルートパス配下にディレクトリが複数存在する場合ディレクトリ毎のディスク使用量の情報を取得できること.
     * @throws IOException エラー
     * @throws DcMxException エラー
     */
    @Test
    public void ルートパス配下にディレクトリが複数存在する場合ディレクトリ毎のディスク使用量の情報を取得できること() throws IOException, DcMxException {

        File testDir = new File(rootPath + File.separator + "_ads");
        try {
            testDir.mkdir();

            DiskUsageCreator diskUsageCreator = new DiskUsageCreator(rootPath, "0.9");
            DiskUsages diskUsages = diskUsageCreator.create();

            Map<String, String> expectedVolumes = new HashMap<String, String>();
            expectedVolumes.put("elasticsearch1", "elasticsearch1");
            expectedVolumes.put("_ads", "_ads");

            assertEquals(2, diskUsages.size());
            DiskUsage diskUsage = diskUsages.get(0);
            assertNotNull(diskUsage);
            assertNotNull(expectedVolumes.get(diskUsage.getVolume()));
            expectedVolumes.remove(diskUsage.getVolume());
            assertTrue(diskUsage.getVolumeDiskSize() > 0);
            assertTrue(diskUsage.getAllocatedDiskSize() > 0);
            assertTrue(diskUsage.getUsedDiskSize() > 0);

            diskUsage = diskUsages.get(1);
            assertNotNull(diskUsage);
            assertNotNull(expectedVolumes.get(diskUsage.getVolume()));
            expectedVolumes.remove(diskUsage.getVolume());
            assertTrue(diskUsage.getVolumeDiskSize() > 0);
            assertTrue(diskUsage.getAllocatedDiskSize() > 0);
            assertTrue(diskUsage.getUsedDiskSize() > 0);

        } finally {
            testDir.delete();
        }
    }

    /**
     * ルートパス配下にファイルが存在する場合無視されること.
     * @throws IOException エラー
     * @throws DcMxException エラー
     */
    @Test
    public void ルートパス配下にファイルが存在する場合無視されること() throws IOException, DcMxException {
        File testFile = new File(rootPath + File.separator + "file.txt");
        try {
            testFile.createNewFile();

            DiskUsageCreator diskUsageCreator = new DiskUsageCreator(rootPath, "0.9");
            DiskUsages diskUsages = diskUsageCreator.create();

            assertEquals(1, diskUsages.size());
            for (DiskUsage diskUsage : diskUsages) {
                assertNotNull(diskUsage);
                assertEquals("elasticsearch1", diskUsage.getVolume());
                assertTrue(diskUsage.getVolumeDiskSize() > 0);
                assertTrue(diskUsage.getAllocatedDiskSize() > 0);
                assertTrue(diskUsage.getUsedDiskSize() > 0);
            }
        } finally {
            testFile.delete();
        }
    }

    /**
     * ボリューム配下にunmountedファイルが存在する場合該当ボリュームのみエラー情報が格納されていること.
     * @throws Exception エラー
     */
    @Test
    public void ボリューム配下にunmountedファイルが存在する場合該当ボリュームのみエラー情報が格納されていること() throws Exception {
        File testFile = new File(rootPath + File.separator + volumeName + File.separator + ".unmounted");
        try {
            testFile.createNewFile();

            DiskUsageCreator diskUsageCreator = new DiskUsageCreator(rootPath, "0.9");
            DiskUsages diskUsages = diskUsageCreator.create();

            assertEquals(1, diskUsages.size());

            DiskUsage expected = DiskUsage.getErrorInstance("elasticsearch1",
                    new DcMxException(new DcMxMessageId("MX500-SV-0003")));
            assertEquals(expected.toJson(), diskUsages.get(0).toJson());
        } finally {
            testFile.delete();
        }
    }

    /**
     * ディスク容量のしきい値が1の場合ディスク使用量の情報を取得できること.
     * @throws DcMxException エラー
     */
    @Test
    public void ディスク容量のしきい値が1の場合ディスク使用量の情報を取得できること() throws DcMxException {
        DiskUsageCreator diskUsageCreator = new DiskUsageCreator(rootPath, "1");
        DiskUsages diskUsages = diskUsageCreator.create();

        assertEquals(1, diskUsages.size());
        for (DiskUsage diskUsage : diskUsages) {
            assertNotNull(diskUsage);
            assertEquals("elasticsearch1", diskUsage.getVolume());
            assertTrue(diskUsage.getVolumeDiskSize() > 0);
            assertTrue(diskUsage.getAllocatedDiskSize() > 0);
            assertTrue(diskUsage.getUsedDiskSize() > 0);
        }
    }

    /**
     * ディスク容量のしきい値が0の場合ディスク使用量の情報を取得できること.
     * @throws DcMxException エラー
     */
    @Test
    public void ディスク容量のしきい値が0の場合ディスク使用量の情報を取得できること() throws DcMxException {
        DiskUsageCreator diskUsageCreator = new DiskUsageCreator(rootPath, "0");
        DiskUsages diskUsages = diskUsageCreator.create();

        assertEquals(1, diskUsages.size());
        for (DiskUsage diskUsage : diskUsages) {
            assertNotNull(diskUsage);
            assertEquals("elasticsearch1", diskUsage.getVolume());
            assertTrue(diskUsage.getVolumeDiskSize() > 0);
            assertTrue(diskUsage.getAllocatedDiskSize() == 0);
            assertTrue(diskUsage.getUsedDiskSize() > 0);
        }
    }

    /**
     * ディスク容量のしきい値が負の場合例外がスローされること.
     * @throws Exception ディスク容量のしきい値が不正
     */
    @Test (expected = DcMxException.class)
    public void ディスク容量のしきい値が負の場合例外がスローされること() throws Exception {
            new DiskUsageCreator(rootPath, "-1");
    }

    /**
     * ディスク容量のしきい値が1より大きい場合例外がスローされること.
     * @throws Exception ディスク容量のしきい値が不正
     */
    @Test (expected = DcMxException.class)
    public void ディスク容量のしきい値が1より大きい場合例外がスローされること() throws Exception {
            new DiskUsageCreator(rootPath, "1.1");
    }

    /**
     * ディスク容量のしきい値が数値ではない場合例外がスローされること.
     * @throws Exception ディスク容量のしきい値が不正
     */
    @Test (expected = DcMxException.class)
    public void ディスク容量のしきい値が数値ではない場合例外がスローされること() throws Exception {
            new DiskUsageCreator(rootPath, "aaaaa");
    }

    /**
     * ルートパスがnullである場合例外がスローされること.
     * @throws Exception ルートパスが不正
     */
    @Test (expected = DcMxException.class)
    public void ルートパスがnullである場合例外がスローされること() throws Exception {
            new DiskUsageCreator(null, "0.9");
    }

    /**
     * ルートパスが空白文字列である場合例外がスローされること.
     * @throws Exception ルートパスが不正
     */
    @Test (expected = DcMxException.class)
    public void ルートパスが空白文字列である場合例外がスローされること() throws Exception {
            new DiskUsageCreator(" ", "0.9");
    }

}
