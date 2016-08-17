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
package com.fujitsu.dc.mx.tool;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fujitsu.dc.mx.DcMxConfig;
import com.fujitsu.dc.mx.DcMxException;

/**
 * memcachedにReadOnlyMode設定/解除のチェックのテスト.
 */
@RunWith(PowerMockRunner.class)
public class MemcachedReadDeleteModeControllerTest {


    /**
     * memcachedのポート番号にマイナス1が設定された場合例外をスローすること.
     * @throws Exception memcachedのポート番号が範囲外
     */
    @Test (expected = DcMxException.class)
    @PrepareForTest(DcMxConfig.class)
    public void memcachedのポート番号にマイナス1が設定された場合例外をスローすること() throws Exception {
        // テスト用のボリュームを使用するようにDcMxConfigをスパイ
        PowerMockito.spy(DcMxConfig.class);
        PowerMockito.when(DcMxConfig.class, "get", MemcachedReadDeleteModeController.class.getName() + ".port")
        .thenReturn("-1");

        new MemcachedReadDeleteModeController();
    }

    /**
     * memcachedのポート番号に0が設定された場合エラーとならないこと.
     * @throws Exception memcachedのポート番号が範囲外
     */
    @Test
    @PrepareForTest(DcMxConfig.class)
    public void memcachedのポート番号に0が設定された場合エラーとならないこと() throws Exception {
        // テスト用のボリュームを使用するようにDcMxConfigをスパイ
        PowerMockito.spy(DcMxConfig.class);
        PowerMockito.when(DcMxConfig.class, "get", MemcachedReadDeleteModeController.class.getName() + ".port")
        .thenReturn("0");

        MemcachedReadDeleteModeController controller = new MemcachedReadDeleteModeController();
        assertNotNull(controller);
    }

    /**
     * memcachedのポート番号に65535が設定された場合エラーとならないこと.
     * @throws Exception memcachedのポート番号が範囲外
     */
    @Test
    @PrepareForTest(DcMxConfig.class)
    public void memcachedのポート番号に65535が設定された場合エラーとならないこと() throws Exception {
        // テスト用のボリュームを使用するようにDcMxConfigをスパイ
        PowerMockito.spy(DcMxConfig.class);
        PowerMockito.when(DcMxConfig.class, "get", MemcachedReadDeleteModeController.class.getName() + ".port")
        .thenReturn("65535");

        MemcachedReadDeleteModeController controller = new MemcachedReadDeleteModeController();
        assertNotNull(controller);
    }


    /**
     * memcachedのポート番号に65536が設定された場合例外をスローすること.
     * @throws Exception memcachedのポート番号が範囲外
     */
    @Test (expected = DcMxException.class)
    @PrepareForTest(DcMxConfig.class)
    public void memcachedのポート番号に65536が設定された場合例外をスローすること() throws Exception {
        // テスト用のボリュームを使用するようにDcMxConfigをスパイ
        PowerMockito.spy(DcMxConfig.class);
        PowerMockito.when(DcMxConfig.class, "get", MemcachedReadDeleteModeController.class.getName() + ".port")
        .thenReturn("65536");

        new MemcachedReadDeleteModeController();
    }

}
