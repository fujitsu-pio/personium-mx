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
package com.fujitsu.dc.mx;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


/**
 * DC-MX Disk使用量APIのリソースクラス.
 */
public class DcMxExceptionTest {

    /**
     * 想定外のエラーが発生した場合にメッセージを取得できること.
     */
    @Test
    public void 想定外のエラーが発生した場合にメッセージを取得できること() {
        DcMxException dcMxException = new DcMxException("Unknown error.");
        String message = dcMxException.getMessage();
        assertEquals("Unknown error.", message);
    }

    /**
     * 想定内のエラーが発生した場合にメッセージを取得できること.
     */
    @Test
    public void 想定内のエラーが発生した場合にメッセージを取得できること() {
        DcMxException dcMxException = new DcMxException("Failed to read disk status.");
        String message = dcMxException.getMessage();
        assertEquals("Failed to read disk status.", message);
    }

    /**
     * 発生したExceptionに対応したメッセージがない場合にMX500_SV_0998が返却されること.
     */
    @Test
    public void 発生したExceptionに対応したメッセージがない場合にMX500_SV_0998が返却されること() {
        DcMxMessageId messageId = new DcMxMessageId(null);
        DcMxException dcMxException = new DcMxException(messageId);
        String message = dcMxException.getMessage();
        assertEquals("Message not found for error :messageId not specified", message);
    }
}
