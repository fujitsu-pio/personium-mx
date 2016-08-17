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

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujitsu.dc.mx.DcMxException;
import com.fujitsu.dc.mx.DcMxMessageId;
import com.fujitsu.dc.mx.model.diskusage.DiskUsage;
import com.fujitsu.dc.mx.model.diskusage.DiskUsages;

/**
 * ディスク使用量の情報を採取するクラス.
 */
public class DiskUsageCreator {

    private static final String CONFIGURATION_INVALID_CODE = "MX500-SV-0001";
    private static final String FAILED_TO_READ_DISK_STATUS_CODE = "MX500-SV-0003";

    static Logger log = LoggerFactory.getLogger(DiskUsageCreator.class);

    private static final String UNMOUNTED_CHECK_FILE = ".unmounted";
    private String volumePath;
    private double threshold;

    private File volumes;

    /**
     * コンストラクタ.
     * @param volumePath ディスク使用量採取対象のパス
     * @param threshold しきい値（小数値 例：0.9）
     * @throws DcMxException しきい値の数値が異常な場合
     */
    public DiskUsageCreator(String volumePath, String threshold) throws DcMxException {
        this.volumePath = volumePath;
        if (null == this.volumePath || this.volumePath.trim().isEmpty()) {
            throw new DcMxException(new DcMxMessageId(CONFIGURATION_INVALID_CODE));
        }
        try {
            this.threshold = Double.valueOf(threshold);
        } catch (NumberFormatException e) {
            throw new DcMxException(new DcMxMessageId(CONFIGURATION_INVALID_CODE));
        }
        if (this.threshold > 1.0d || this.threshold < 0.0d) {
            throw new DcMxException(new DcMxMessageId(CONFIGURATION_INVALID_CODE));
        }
    }

    /**
     * ディスク使用量の情報を採取する.
     * @return ディスク使用量の情報
     * @throws DcMxException ディスク使用量の採取に失敗
     */
    public DiskUsages create() throws DcMxException {

        volumes = new File(volumePath);
        if (!volumes.exists()) {
            // ルートボリュームが存在しない場合
            log.info(String.format("[%s]: Volume does not exist. [%s]", FAILED_TO_READ_DISK_STATUS_CODE, volumePath));
            throw new DcMxException(new DcMxMessageId(FAILED_TO_READ_DISK_STATUS_CODE));
        }
        if (volumes.isFile()) {
            // ルートボリュームがファイルの場合
            log.info(String.format(
                    "[%s]: Volume is not directory. [%s]", FAILED_TO_READ_DISK_STATUS_CODE, volumePath));
            throw new DcMxException(new DcMxMessageId(FAILED_TO_READ_DISK_STATUS_CODE));
        }

        // ボリューム配下のディスク使用量情報を取得する
        DiskUsages diskUsages = getVolumes();

        return diskUsages;
    }

    /**
     * ボリューム配下のディスク使用量情報を取得する.
     * @return ディスク使用量情報
     */
    private DiskUsages getVolumes() {
        DiskUsages diskUsages = new DiskUsages();
        // ボリューム毎にコンテキスト作成
        for (File volume : volumes.listFiles()) {

            // ファイルの場合は無視する
            if (volume.isFile()) {
                continue;
            }

            // ボリュームがマウントされていない場合はエラーをセットする
            File unmounted = new File(volume.getPath() + File.separator + UNMOUNTED_CHECK_FILE);
            if (unmounted.exists()) {
                log.info(String.format(
                        "[%s]: Volume is not mounted. [%s]", FAILED_TO_READ_DISK_STATUS_CODE,
                        volume.getPath()));
                DiskUsage diskUsage = DiskUsage.getErrorInstance(volume.getName(),
                        new DcMxException(new DcMxMessageId(FAILED_TO_READ_DISK_STATUS_CODE)));
                diskUsages.add(diskUsage);
                continue;
            }

            DiskUsage diskUsage = getDiskUsage(volume);
            diskUsages.add(diskUsage);
        }

        return diskUsages;
    }

    private DiskUsage getDiskUsage(File volume) {
        String volumeName = volume.getName();
        long volumeDiskSize = volume.getTotalSpace();
        long usedDiskSize = volumeDiskSize - volume.getUsableSpace();

        DiskUsage diskUsage = new DiskUsage(volumeName, volumeDiskSize, usedDiskSize, threshold);
        return diskUsage;
    }
}
