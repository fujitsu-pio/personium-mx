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

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * DC-MX のメッセージバンドルクラス.
 */
public class DcMxMessageBundle {

    private static DcMxMessageBundle singleton = new DcMxMessageBundle();

    ResourceBundle messages = ResourceBundle.getBundle("dc-messages");

    /**
     * DC-MX のメッセージバンドルインスタンスを生成.
     * @return DC-MX のメッセージバンドルインスタンス
     */
    public static DcMxMessageBundle getInstance() {
        return singleton;
    }

    private DcMxMessageBundle() {
        super();
    }

    /**
     * IDに対応するメッセージを取得する.
     * @param messageId メッセージID
     * @return メッセージ
     */
    public String getMessage(String messageId) {
        try {
            if (messageId == null) {
                return null;
            }
            return messages.getString(messageId);
        } catch (MissingResourceException e) {
            return "Message not found for message ID: " + messageId;
        }
    }
}
