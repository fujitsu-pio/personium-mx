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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 設定情報を保持するクラス.
 * このクラスからクラスパス上にある dc-mx.propertiesの内容にアクセスできます。
 * クラスパス上に存在しない場合は、システムプロパティ com.fujitsu.dc.mx.configurationFile に
 * 設定されているパスのプロパティファイルを読み込みます。
 */
public class DcMxConfig {

    static Logger log = LoggerFactory.getLogger(DcMxConfig.class);
    private static final String CONFIGURATIN_ERROR_MESSAGE_ID = "MX500-SV-0001";
    /**
     * dc-mx.propertiesの設定ファイルパスキー.
     */
    static final String KEY_CONFIG_FILE = "com.fujitsu.dc.mx.configurationFile";

    private static final String[] MANDATORY_PROP_KEYS = {
        "com.fujitsu.dc.mx.version",
        "com.fujitsu.dc.mx.volume.path",
        "com.fujitsu.dc.mx.volume.threshold",
        "com.fujitsu.dc.mx.pcsModeController",
        "com.fujitsu.dc.mx.waitForMemcached"
    };

    /**
     * singleton.
     */
    private static DcMxConfig singleton = new DcMxConfig();

    /**
     * 設定値を格納するプロパティ実体.
     */
    private final Properties props;

    /**
     * 本アプリで使うプロパティキーのプレフィクス.
     */
    static final String KEY_ROOT = "com.fujitsu.dc.mx.";

    /**
     * MX version設定のキー.
     */
    public static final String MX_VERSION = KEY_ROOT + "version";

    /**
     * MX Disk容量APIで調査するディスク群がマウントされているディレクトリ.
     */
    public static final String MX_VOLUME_PATH = KEY_ROOT + "volume.path";
    /**
     * MX Disk容量APIが容量 FULLと判定すべきディスクの使用率. 文字列 "nn%"で指定されているものとする.
     */
    public static final String MX_VOLUME_THRESHOLD = KEY_ROOT + "volume.threshold";

    /**
     * PcsのReadDeleteModeを制御するためのControllerのクラス名.
     */
    public static final String PCS_MODE_CONTROLLER_CLASS = KEY_ROOT + "pcsModeController";

    /**
     * Memcachedの接続の待ち時間.
     */
    public static final String WAIT_FOR_MEMCACHED = KEY_ROOT + "waitForMemcached";

    /**
     * コンストラクタ.
     */
    private DcMxConfig() throws DcMxRuntimeException {
        props = getMxConfigProperties();
        Properties defProps = getMxDefaultConfigProperties();

        try {
            validateMandatoryProperties(props, defProps);
        } catch (DcMxException e) {
            log.error("Mandatory property is missing.");
            throw new DcMxRuntimeException(e);
        }
    }

    /**
     * MX Versionの値を取得します.
     * @return MX Versionの値
     */
    public static String getMxVersion() {
        return get(MX_VERSION);
    }

    /**
     * MX_VOLUME_PATHの値を取得する.
     * @return mx-properties内の com.fujitsu.dc.mx.volume.pathの値
     */
    public static String getMxVolumePath() {
        return get(MX_VOLUME_PATH);
    }

    /**
     * MX_VOLUME_THRESHOLDの値を取得する.
     * @return mx-properties内の com.fujitsu.dc.mx.volume.thresholdの値
     */
    public static String getMxVolumeThreshold() {
        return get(MX_VOLUME_THRESHOLD);
    }

    /**
     * PCS_MODE_CONTROLLER_CLASSの値を取得する.
     * @return mx-properties内の com.fujitsu.dc.mx.pcsModeControllerの値
     */
    public static String getPcsModeController() {
        return get(PCS_MODE_CONTROLLER_CLASS);
    }

    /**
     * WAIT_FOR_MEMCACHEDの値を取得する.
     * @return mx-properties内の com.fujitsu.dc.mx.waitForMemcachedの値
     */
    public static long getWaitForMemcached() {
        return Long.parseLong(get(WAIT_FOR_MEMCACHED));
    }

    /**
     * Key文字列を指定して設定情報を取得します.
     * @param key 設定キー
     * @return 設定値
     */
    public static String get(final String key) {
        return singleton.doGet(key);
    }

    /**
     * dc-mx.propertiesファイルを読み込む.
     * @return dc-mx.properties
     */
    protected Properties getMxConfigProperties() {
        Properties propertiesOverride = new Properties();
        String configFilePath = System.getProperty(KEY_CONFIG_FILE);
        InputStream is = getConfigFileInputStream(configFilePath);
        return loadProperties(propertiesOverride, is);
    }

    /**
     * クラスパスに定義されたdc-mx.propertiesファイルを読み込む.
     * @return
     */
    Properties getMxDefaultConfigProperties() {
        Properties properties = new Properties();
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("dc-mx-default.properties");
        return loadProperties(properties, is);
    }

    private Properties loadProperties(Properties properties, InputStream is) {
        try {
            if (is != null) {
                properties.load(is);
            } else {
                log.debug("[dc-mx.properties] file not found on the classpath.");
            }
        } catch (IOException e) {
            log.debug("IO Exception when loading [dc-mx.properties] file.");
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                log.debug("IO Exception when closing [dc-mx.properties] file.");
            }
        }
        return properties;
    }

    /**
     * dc-mx.propertiesをInputStream形式で取得する.
     * @param configFilePath 設定ファイルパス
     * @return dc-mx.propertiesのストリーム
     */
    protected InputStream getConfigFileInputStream(String configFilePath) {
        InputStream configFileInputStream = null;
        if (configFilePath == null) {
            configFileInputStream = DcMxConfig.class.getClassLoader().getResourceAsStream("dc-mx.properties");
            return configFileInputStream;
        }

        try {
            // 設定ファイルを指定されたパスから読み込む
            File configFile = new File(configFilePath);
            configFileInputStream = new FileInputStream(configFile);
            log.info("dc-mx.properties from system properties.");
        } catch (FileNotFoundException e) {
            log.info("dc-mx.properties from class path.");
            return null;
        }
        return configFileInputStream;
    }


    /**
     * 必須プロパティの存在/内容チェックを行う.
     * @param props プロパティ
     * @param defProperties デフォルトファイルに設定されたプロパティ
     * @throws DcMxException プロパティ内容にエラーが存在した場合
     */
    private void validateMandatoryProperties(Properties properties, Properties defProperties) throws DcMxException {
        // dc-mx.propertiesにキーが設定されていないまたは空の値が設定されている場合は、dc-mx-default.propertiesに設定した値を使用する
        for (String key : defProperties.stringPropertyNames()) {
            if (!properties.containsKey(key)) {
                log.info("property is missing in configuration: " + key);
                setDefaultPropertyValue(defProperties, key);
            }
            if (properties.getProperty(key) == null || properties.getProperty(key).isEmpty()) {
                log.info("Value is not specified for property: " + key);
                setDefaultPropertyValue(defProperties, key);
            }
        }

        // 必須キーが設定されていなければ、エラーにする
        for (String key : MANDATORY_PROP_KEYS) {
            if (!properties.containsKey(key)) {
                log.info("Mandatory property is missing in configuration: " + key);
                throw new DcMxException(new DcMxMessageId(CONFIGURATIN_ERROR_MESSAGE_ID));
            }
            if (properties.getProperty(key) == null || properties.getProperty(key).isEmpty()) {
                log.info("Value is not specified for mandatory property: " + key);
                throw new DcMxException(new DcMxMessageId(CONFIGURATIN_ERROR_MESSAGE_ID));
            }
        }
    }

    private void setDefaultPropertyValue(Properties defProperties, String key) {
        String value = defProperties.getProperty(key);
        log.info("Set default property. value=" + value);
        props.setProperty(key, value);
    }

    /**
     * プロパティを取得する.
     * @param key プロパティのキー
     * @return プロパティの値
     */
    protected String doGet(String key) {
        return (String) this.props.get(key);
    }
}
