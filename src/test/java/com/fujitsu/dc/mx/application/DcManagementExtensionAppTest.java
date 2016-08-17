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
package com.fujitsu.dc.mx.application;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fujitsu.dc.mx.DcMxConfig;
import com.fujitsu.dc.mx.DcMxException;
import com.fujitsu.dc.mx.control.pcs.PcsModeControlManager;
import com.fujitsu.dc.mx.model.diskusage.DiskUsages;
import com.fujitsu.dc.mx.process.DiskUsageMainProcess;
import com.fujitsu.dc.mx.rs.DcManagementExtensionApp;

/**
 * DcManagementExtensionAppの起動時チェックのテスト.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({DcMxConfig.class, PcsModeControlManager.class })
public class DcManagementExtensionAppTest {
    String rootPath = ClassLoader.getSystemResource("").getPath() + File.separator + "diskSizeTest";
    String volumeName = "volume1";

    /**
     * テスト前に実行する処理.
     * @throws Exception エラー
     */
    @Before
    public void before() throws Exception {
        // テスト用ボリューム(ディレクトリ)作成
        File testDir = new File(rootPath + File.separator + volumeName);
        testDir.mkdirs();

        PowerMockito.spy(DcMxConfig.class);

        // テスト用のボリュームを使用するようにDcMxConfigをスパイ
        PowerMockito.when(DcMxConfig.class, "getMxVolumePath").thenReturn(rootPath);

        // テスト用のロックコントローラーを使用するようにDcMxConfigをスパイ
        PowerMockito.when(DcMxConfig.class, "getPcsModeController").thenReturn(
                "com.fujitsu.dc.mx.application.MockReadDeleteModeController");
    }

    /**
     * テスト後に実行する処理.
     */
    @After
    public void after() {
        File testDir = new File(rootPath + File.separator + volumeName);
        testDir.delete();
        testDir = new File(rootPath);
        testDir.delete();
    }

    /**
     * 正常な状態の場合に起動エラーが発生しないこと.
     * @throws DcMxException 起動エラー
     */
    @Test
    public void 正常な状態の場合に起動エラーが発生しないこと() throws DcMxException {
        DcManagementExtensionApp app = new DcManagementExtensionApp();
        app.initialize();

        assertFalse(MockReadDeleteModeController.getInstance().isLocked());
    }

    /**
     * 存在しないボリュームパスを指定した場合システムステータスがERRORとなること.
     * @throws Exception 起動エラー
     */
    @Test (expected = DcMxException.class)
    public void 存在しないボリュームパスを指定した場合システムステータスがERRORとなること() throws Exception {
        // 存在しないボリュームを返却するようにDcMxConfigをスパイ
        PowerMockito.when(DcMxConfig.class, "getMxVolumePath").thenReturn("/NonExistingDir");

        DcManagementExtensionApp app = new DcManagementExtensionApp();
        app.initialize();

        DiskUsageMainProcess processor = new DiskUsageMainProcess();
        DiskUsages diskUsages = processor.collectDiskUsages();
        processor.handlePCSMode(diskUsages);
    }

    /**
     * ディスクがFULL状態の場合起動時にmemcachedにフラグが設定されること.
     * @throws Exception 起動エラー
     */
    @Test
    public void ディスクがFULL状態の場合起動時にmemcachedにフラグが設定されること() throws Exception {
        // しきい値0を返却するようにDcMxConfigをスパイ
        PowerMockito.when(DcMxConfig.class, "getMxVolumeThreshold").thenReturn("0");

        MockReadDeleteModeController.getInstance().setMode(false);
        assertFalse(MockReadDeleteModeController.getInstance().isLocked());

        DcManagementExtensionApp app = new DcManagementExtensionApp();
        app.initialize();

        assertTrue(MockReadDeleteModeController.getInstance().isLocked());
    }

    /**
     * ロック状態が設定されている状態でディスクがFULLでない状態の場合起動時にmemcachedのフラグが解除されること.
     * @throws Exception 起動エラー
     */
    @Test
    public void ロック状態が設定されている状態でディスクがFULLでない状態の場合起動時にmemcachedのフラグが解除されること() throws Exception {
        // ロック状態を設定
        MockReadDeleteModeController.getInstance().setMode(true);
        assertTrue(MockReadDeleteModeController.getInstance().isLocked());

        DcManagementExtensionApp app = new DcManagementExtensionApp();
        app.initialize();

        assertFalse(MockReadDeleteModeController.getInstance().isLocked());
    }

}
