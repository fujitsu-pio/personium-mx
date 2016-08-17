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
import static org.junit.Assert.assertNotNull;

import org.junit.Test;


/**
 * DC-MX Disk使用量APIのリソースクラス.
 */
public class DcMxMessageBundleTest {

    /**
     * メッセージファイルに存在するメッセージが取得できること.
     */
    @Test
    public void メッセージファイルに存在するメッセージが取得できること() {
        assertNotNull(DcMxMessageBundle.getInstance().getMessage("MX500-SV-0001"));
    }

    /**
     * メッセージファイルに存在しないメッセージはnullで返却されること.
     */
    @Test
    public void メッセージファイルに存在しないメッセージはnullで返却されること() {
        assertEquals("Message not found for message ID: MX500-SV",
                DcMxMessageBundle.getInstance().getMessage("MX500-SV"));
    }

}
