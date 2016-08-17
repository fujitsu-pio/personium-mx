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
package com.fujitsu.dc.mx.rs;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujitsu.dc.mx.DcMxConfig;
import com.fujitsu.dc.mx.model.diskusage.DiskUsages;
import com.fujitsu.dc.mx.process.DiskUsageMainProcess;
import com.fujitsu.dc.mx.rs.diskusage.DiskUsageResource;

/**
 * DC-MXのJAX-RSのApplication.
 */
public class DcManagementExtensionApp extends Application {

    private static Logger log = LoggerFactory.getLogger(DcManagementExtensionApp.class);

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        /* Disk使用量APIの JAX-RSリソース */
        classes.add(DiskUsageResource.class);
        return classes;
    }

    /**
     * サービス開始時に一度だけ呼び出される.
     * エラー時は、サービスの起動に失敗する。
     */
    @PostConstruct
    public void initialize() {
        // 1件プロパティを読み込む(初期化するため、すべてのプロパティが読み込まれる)
        DcMxConfig.getMxVersion();

        // Disk使用量チェックを呼出し、Read-Delete-Onlyモードを設定する.
        try {
            DiskUsageMainProcess processor = new DiskUsageMainProcess();
            // Disk使用量の取得
            DiskUsages diskUsages = processor.collectDiskUsages();
            // 上記で取得された Disk使用量情報を基に、PCS ReadDeleteOnlyモードを設定
            processor.handlePCSMode(diskUsages);
        } catch (Exception e) {
            log.error("Failed to initialize dc-mx application.", e);
            // 設定エラーがあった場合でもRESTサービスは継続させるため、例外は投げない
        }

    }
}
