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
package com.fujitsu.dc.mx.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujitsu.dc.mx.DcMxConfig;
import com.fujitsu.dc.mx.DcMxException;
import com.fujitsu.dc.mx.DcMxMessageId;
import com.fujitsu.dc.mx.control.diskusage.DiskUsageCreator;
import com.fujitsu.dc.mx.control.pcs.PcsModeControlManager;
import com.fujitsu.dc.mx.model.diskusage.DiskUsages;

/**
 * Disk使用量APIのメイン処理クラス.
 */
public class DiskUsageMainProcess {

    static Logger log = LoggerFactory.getLogger(DiskUsageMainProcess.class);

    private static final String UNKNOWN_ERROR_MESSAGE_ID = "MX500-SV-0999";

    /**
     * デフォルトコンストラクタ.
     */
    public DiskUsageMainProcess() {
        super();
    }

    /**
     * ディスク使用量を取得し情報を固める.
     * @return 各ディスクの使用量情報
     * @throws DcMxException 処理中にエラーが発生した場合
     */
    public DiskUsages collectDiskUsages() throws DcMxException {
        // 各ボリュームのディスク使用量を取得する
        log.info("Collecting disk usages.");
        DiskUsageCreator diskUsageCreator = new DiskUsageCreator(DcMxConfig.getMxVolumePath(),
                DcMxConfig.getMxVolumeThreshold());
        return diskUsageCreator.create();
    }

    /**
     * ディスク使用量情報を基に、PCSの ReadDeleteOnly modeを設定する.
     * @param diskUsages 全ディスクの使用量情報
     * @return 各ディスクの使用量情報（引数と同じものが返る)
     * @throws DcMxException 処理中にエラーが発生した場合
     */
    public DiskUsages handlePCSMode(DiskUsages diskUsages) throws DcMxException {
        // ReadDeleteOnlyモードの設定/解除
        log.info("Trying to handle ReadDeleteOnly mode.");
        PcsModeControlManager.setPcsMode(diskUsages);
        return diskUsages;
    }

    /**
     * cron等からの呼び出しポイント.
     * @param args 引数：不要
     * @throws DcMxException 処理中にエラーが発生した場合
     */
    public static void main(String[] args) throws DcMxException {
        log.info("DiskUsageMainProcess#main is called.");
        try {
            DiskUsageMainProcess processor = new DiskUsageMainProcess();
            DiskUsages usages = processor.collectDiskUsages();
            processor.handlePCSMode(usages);
            log.info("DiskUsageMainProcess#main is completed.");
        } catch (DcMxException e) {
            log.info(e.getMessage());
            throw e;
        } catch (Exception e) {
            DcMxException e2 = new DcMxException(new DcMxMessageId(UNKNOWN_ERROR_MESSAGE_ID), e);
            log.info(e2.getMessage());
            throw e2;
        }
    }
}
