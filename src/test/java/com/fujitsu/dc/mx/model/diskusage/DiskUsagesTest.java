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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.fujitsu.dc.mx.DcMxException;
import com.fujitsu.dc.mx.DcMxMessageId;

/**
 * テストケース.
 */
public class DiskUsagesTest {

    /**
     * 各ボリューム内のStatusが全てOKの場合にSystemStatusがOKとなること.
     */
    @Test
    public void 各ボリューム内のStatusが全てOKの場合にSystemStatusがOKとなること() {
        DiskUsages usages = new DiskUsages();

        DiskUsage usage = new DiskUsage("elasticsearch1", 10000000L, 7000000L, 0.9);
        usages.add(usage);
        usage = new DiskUsage("_ads", 10000000L, 7000000L, 0.9);
        usages.add(usage);
        usage = new DiskUsage("_dav", 10000000L, 7000000L, 0.9);
        usages.add(usage);

        assertEquals(DiskUsage.Status.OK, usages.getSystemStatus());
    }

    /**
     * 各ボリューム内のStatusがすべてERRORの場合にSystemStatusがERRORとなること.
     */
    @Test
    public void 各ボリューム内のStatusがすべてERRORの場合にSystemStatusがERRORとなること() {
        DiskUsages usages = new DiskUsages();

        DiskUsage usage = DiskUsage.getErrorInstance("elasticsearch1",
                new DcMxException(new DcMxMessageId("MX500-SV-0001")));
        usages.add(usage);
        usage = DiskUsage.getErrorInstance("_ads",
                new DcMxException(new DcMxMessageId("MX500-SV-0001")));
        usages.add(usage);
        usage = DiskUsage.getErrorInstance("_dav",
                new DcMxException(new DcMxMessageId("MX500-SV-0001")));
        usages.add(usage);

        assertEquals(DiskUsage.Status.ERROR, usages.getSystemStatus());
    }

    /**
     * 各ボリューム内のStatusがすべてFULLの場合にSystemStatusがFULLとなること.
     */
    @Test
    public void 各ボリューム内のStatusがすべてFULLの場合にSystemStatusがFULLとなること() {
        DiskUsages usages = new DiskUsages();

        DiskUsage usage = new DiskUsage("elasticsearch1", 10000000L, 9500000L, 0.9);
        usages.add(usage);
        usage = new DiskUsage("_ads", 10000000L, 95000000L, 0.9);
        usages.add(usage);
        usage = new DiskUsage("_dav", 10000000L, 9500000L, 0.9);
        usages.add(usage);

        assertEquals(DiskUsage.Status.FULL, usages.getSystemStatus());
    }

    /**
     * 各ボリューム内のStatusにFULLが存在する場合にSystemStatusがFULLとなること.
     */
    @Test
    public void 各ボリューム内のStatusにFULLが存在する場合にSystemStatusがFULLとなること() {
        DiskUsages usages = new DiskUsages();

        DiskUsage usage = new DiskUsage("elasticsearch1", 10000000L, 7000000L, 0.9);
        usages.add(usage);
        usage = new DiskUsage("_ads", 10000000L, 95000000L, 0.9);
        usages.add(usage);
        usage = new DiskUsage("_dav", 10000000L, 7000000L, 0.9);
        usages.add(usage);

        assertEquals(DiskUsage.Status.FULL, usages.getSystemStatus());
    }

    /**
     * 各ボリューム内のStatusにERRORが存在する場合にSystemStatusがERRORとなること.
     */
    @Test
    public void 各ボリューム内のStatusにERRORが存在する場合にSystemStatusがERRORとなること() {
        DiskUsages usages = new DiskUsages();

        DiskUsage usage = new DiskUsage("elasticsearch1", 10000000L, 7000000L, 0.9);
        usages.add(usage);
        usage = new DiskUsage("_ads", 10000000L, 70000000L, 0.9);
        usages.add(usage);
        usage = DiskUsage.getErrorInstance("_dav",
                new DcMxException(new DcMxMessageId("MX500-SV-0001")));
        usages.add(usage);

        assertEquals(DiskUsage.Status.ERROR, usages.getSystemStatus());
    }

    /**
     * 各ボリューム内のStatusにFULLとERRORが存在する場合にSystemStatusがERRORとなること.
     */
    @Test
    public void 各ボリューム内のStatusにFULLとERRORが存在する場合にSystemStatusがERRORとなること() {
        DiskUsages usages = new DiskUsages();

        DiskUsage usage = new DiskUsage("elasticsearch1", 10000000L, 7000000L, 0.9);
        usages.add(usage);
        usage = DiskUsage.getErrorInstance("_ads",
                new DcMxException(new DcMxMessageId("MX500-SV-0001")));
        usages.add(usage);
        usage = new DiskUsage("_dav", 10000000L, 9500000L, 0.9);
        usages.add(usage);

        assertEquals(DiskUsage.Status.ERROR, usages.getSystemStatus());
    }
}
