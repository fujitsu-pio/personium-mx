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
package com.fujitsu.dc.mx.control.pcs;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fujitsu.dc.mx.DcMxConfig;
import com.fujitsu.dc.mx.model.diskusage.DiskUsage;
import com.fujitsu.dc.mx.model.diskusage.DiskUsages;
import com.fujitsu.dc.mx.tool.MemcachedReadDeleteModeController;

/**
 * memcachedにReadOnlyMode設定/解除のチェックのテスト.
 */
@RunWith(PowerMockRunner.class)
public class PcsModeControlManagerTest {

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
     * memcachedに停止時にReadOnlyModeを設定しようとした場合にボリューム全体のStatusがERRORとなること.
     * @throws Exception エラー
     */
    @Test
    @PrepareForTest({DcMxConfig.class, PcsModeControlManager.class })
    public void memcachedに停止時にReadOnlyModeを設定しようとした場合にボリューム全体のStatusがERRORとなること() throws Exception {
        // テスト用のボリュームを使用するようにDcMxConfigをスパイ
        PowerMockito.spy(DcMxConfig.class);
        PowerMockito.when(DcMxConfig.class, "get", MemcachedReadDeleteModeController.class.getName() + ".host")
        .thenReturn("hoge");

        DiskUsages usages = new DiskUsages();
        DiskUsage usage = new DiskUsage("elasticsearch1", 10000000L, 7000000L, 0.9);
        usages.add(usage);
        usage = new DiskUsage("_ads", 10000000L, 95000000L, 0.9);
        usages.add(usage);
        usage = new DiskUsage("_dav", 10000000L, 7000000L, 0.9);
        usages.add(usage);

        // memcachedにReadOnlyModeを設定する
        PcsModeControlManager.setPcsMode(usages);
        assertEquals(DiskUsage.Status.ERROR, usages.getSystemStatus());
    }

    /**
     * memcachedに停止時にReadOnlyModeを解除しようとした場合にボリューム全体のStatusがERRORとなること.
     * @throws Exception エラー
     */
    @Test
    @PrepareForTest({DcMxConfig.class, PcsModeControlManager.class })
    public void memcachedに停止時にReadOnlyModeを解除しようとした場合にボリューム全体のStatusがERRORとなること() throws Exception {
        // テスト用のボリュームを使用するようにDcMxConfigをスパイ
        PowerMockito.spy(DcMxConfig.class);
        PowerMockito.when(DcMxConfig.class, "get", MemcachedReadDeleteModeController.class.getName() + ".host")
        .thenReturn("hoge");

        DiskUsages usages = new DiskUsages();
        DiskUsage usage = new DiskUsage("elasticsearch1", 10000000L, 7000000L, 0.9);
        usages.add(usage);
        usage = new DiskUsage("_ads", 10000000L, 7000000L, 0.9);
        usages.add(usage);
        usage = new DiskUsage("_dav", 10000000L, 7000000L, 0.9);
        usages.add(usage);

        // memcachedにReadOnlyModeを解除する
        PcsModeControlManager.setPcsMode(usages);
        assertEquals(DiskUsage.Status.ERROR, usages.getSystemStatus());
    }

    /**
     * memcachedにReadOnlyModeが設定されている場合にReadOnlyModeを解除しようとした場合にボリューム全体のStatusがOKとなること.
     * PowerMockitoで接続できるホストと接続できないホストを共存出来ないため、テストをIgnore
     * このテストを実施する時は一時的に@Ignoreを外すこと
     * @throws Exception エラー
     */
    @Test
    @PrepareForTest({DcMxConfig.class, PcsModeControlManager.class })
    @Ignore
    public void memcachedにReadOnlyModeが設定されている場合にReadOnlyModeを解除しようとした場合にボリューム全体のStatusがOKとなること() throws Exception {
        // テスト用のボリュームを使用するようにDcMxConfigをスパイ
        PowerMockito.spy(DcMxConfig.class);
        PowerMockito.when(DcMxConfig.class, "get", MemcachedReadDeleteModeController.class.getName() + ".host")
        .thenReturn("10.123.120.21");

        DiskUsages usagesSet = new DiskUsages();
        DiskUsage usage = new DiskUsage("elasticsearch1", 10000000L, 7000000L, 0.9);
        usagesSet.add(usage);
        usage = new DiskUsage("_ads", 10000000L, 95000000L, 0.9);
        usagesSet.add(usage);
        usage = new DiskUsage("_dav", 10000000L, 7000000L, 0.9);
        usagesSet.add(usage);
        // memcachedにReadOnlyModeを設定する
        PcsModeControlManager.setPcsMode(usagesSet);

        DiskUsages usageRemove = new DiskUsages();
        usage = new DiskUsage("elasticsearch1", 10000000L, 7000000L, 0.9);
        usageRemove.add(usage);
        usage = new DiskUsage("_ads", 10000000L, 7000000L, 0.9);
        usageRemove.add(usage);
        usage = new DiskUsage("_dav", 10000000L, 7000000L, 0.9);
        usageRemove.add(usage);
        // memcachedにReadOnlyModeを解除する
        PcsModeControlManager.setPcsMode(usageRemove);
        assertEquals(DiskUsage.Status.OK, usageRemove.getSystemStatus());
    }

    /**
     * memcachedにReadOnlyModeが設定されていない場合にReadOnlyModeを解除しようとした場合にボリューム全体のStatusがOKとなること.
     * PowerMockitoで接続できるホストと接続できないホストを共存出来ないため、テストをIgnore
     * このテストを実施する時は一時的に@Ignoreを外すこと
     * @throws Exception エラー
     */
    @Test
    @PrepareForTest({DcMxConfig.class, PcsModeControlManager.class })
    @Ignore
    public void memcachedにReadOnlyModeが設定されていない場合にReadOnlyModeを解除しようとした場合にボリューム全体のStatusがOKとなること() throws Exception {
        // テスト用のボリュームを使用するようにDcMxConfigをスパイ
        PowerMockito.spy(DcMxConfig.class);
        PowerMockito.when(DcMxConfig.class, "get", MemcachedReadDeleteModeController.class.getName() + ".host")
        .thenReturn("10.123.120.21");

        DiskUsage usage = new DiskUsage("elasticsearch1", 10000000L, 7000000L, 0.9);
        DiskUsages usages = new DiskUsages();
        usage = new DiskUsage("elasticsearch1", 10000000L, 7000000L, 0.9);
        usages.add(usage);
        usage = new DiskUsage("_ads", 10000000L, 7000000L, 0.9);
        usages.add(usage);
        usage = new DiskUsage("_dav", 10000000L, 7000000L, 0.9);
        usages.add(usage);
        // memcachedにReadOnlyModeを解除する
        PcsModeControlManager.setPcsMode(usages);
        assertEquals(DiskUsage.Status.OK, usages.getSystemStatus());
    }
}
