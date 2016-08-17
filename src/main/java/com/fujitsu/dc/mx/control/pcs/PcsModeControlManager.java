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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujitsu.dc.mx.DcMxConfig;
import com.fujitsu.dc.mx.DcMxException;
import com.fujitsu.dc.mx.DcMxMessageId;
import com.fujitsu.dc.mx.model.diskusage.DiskUsage;
import com.fujitsu.dc.mx.model.diskusage.DiskUsages;

/**
 * PCSの動作モードを変更するクラス.
 */
public class PcsModeControlManager {

    static Logger log = LoggerFactory.getLogger(PcsModeControlManager.class);

    private ReadDeleteModeController lockController = null;
    private static final String LOCK_KEY = "PcsReadDeleteMode";
    private static PcsModeControlManager singleton = null;

    private void init() throws DcMxException {
        Class<?> clazz;
        try {
            clazz = Class.forName(DcMxConfig.getPcsModeController());
            lockController = (ReadDeleteModeController) clazz.newInstance();
        } catch (ClassNotFoundException e) {
            throw new DcMxException(new DcMxMessageId("MX500-SV-0001"), e);
        } catch (ClassCastException e) {
            throw new DcMxException(new DcMxMessageId("MX500-SV-0001"), e);
        } catch (InstantiationException e) {
            throw new DcMxException(new DcMxMessageId("MX500-SV-0001"), e);
        } catch (IllegalAccessException e) {
            throw new DcMxException(new DcMxMessageId("MX500-SV-0001"), e);
        }
    }

    private PcsModeControlManager() throws DcMxException {
        init();
    }

    /**
     * PCSの動作モードを変更する.
     * @param diskUsages 各ボリュームのディスク使用量のリスト
     * @throws DcMxException DcMxException
     */
    public static void setPcsMode(DiskUsages diskUsages) throws DcMxException {
        if (null == singleton) {
            try {
                singleton = new PcsModeControlManager();
            } catch (DcMxException e) {
                log.error(e.getMessage(), e);
                diskUsages.setError(e);
                return;
            }
        }
        singleton.setPcsModeInternal(diskUsages);
    }

    /**
     * PCSの動作モードを変更する.
     * @param diskUsages 各ボリュームのディスク使用量のリスト
     * @throws DcMxException DcMxException
     */
    private void setPcsModeInternal(DiskUsages diskUsages) throws DcMxException {
        boolean allDisksOk = true;
        for (DiskUsage usage : diskUsages) {
            switch (usage.getStatus()) {
            case FULL:
                // ReadDeleteOnlyモードに移行する
                log.info("Try to set ReadDeleteOnly mode to PCS.");
                try {
                lockController.setReadDeleteMode(LOCK_KEY, diskUsages.toJson().toJSONString());
                log.info("Set ReadDeleteOnly.");
                } catch (DcMxException e) {
                    // memcachedに書き込みが失敗した場合、ボリューム全体のStatusをERRORに設定する
                    log.error(e.getMessage(), e);
                    diskUsages.setError(e);
                }
                log.info(diskUsages.toJson().toJSONString());
                return;
            case ERROR:
                allDisksOk = false;
                break;
            case OK:
                break;
            default:
                break;
            }
        }
        // ReadDeleteOnlyモードを削除する
        if (allDisksOk) {
            log.info("Try to release ReadDeleteOnly mode from PCS.");
            try {
                lockController.removeReadDeleteMode(LOCK_KEY);
                log.info("Unset ReadDeleteOnly.");
            } catch (DcMxException e) {
                // memcachedに書き込みが失敗した場合、ボリューム全体のStatusをERRORに設定する
                log.error(e.getMessage(), e);
                diskUsages.setError(e);
            }
            log.info(diskUsages.toJson().toJSONString());
        }
    }
}
