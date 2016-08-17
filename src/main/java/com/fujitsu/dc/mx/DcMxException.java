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

/**
 * DC-MX の基底例外クラス.
 */
public class DcMxException extends Exception {

    DcMxMessageId messageId = null;

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -4992995695239000240L;

    /**
     * デフォルトコンストラクタ.
     */
    public DcMxException() {
        super();
    }

    /**
     * コンストラクタ.
     * @param message メッセージ
     */
    public DcMxException(String message) {
        super(message);
    }

    /**
     * コンストラクタ.
     * @param msgId メッセージ
     */
    public DcMxException(DcMxMessageId msgId) {
        messageId = msgId;
    }

    /**
     * コンストラクタ.
     * @param msgId メッセージ
     * @param cause 根本例外
     */
    public DcMxException(DcMxMessageId msgId, Throwable cause) {
        super(cause);
        messageId = msgId;
    }

    /**
     * コンストラクタ.
     * @param cause 根本例外.
     */
    public DcMxException(Throwable cause) {
        super(cause);
    }

    /**
     * コンストラクタ.
     * @param message メッセージ.
     * @param cause 根本例外.
     */
    public DcMxException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * エラーメッセージを取得する.
     * @return エラーメッセージ
     */
    @Override
    public String getMessage() {
        if (messageId == null) {
            return super.getMessage();
        }
        String message = DcMxMessageBundle.getInstance().getMessage(messageId.getMessageId());
        if (message == null) {
            message = DcMxMessageBundle.getInstance().getMessage("MX500-SV-0998") + "messageId not specified";
        }
        return message;
    }

    /**
     * エラーコードを取得する.
     * @return エラーコード
     */
    public String getMessageId() {
        if (messageId == null) {
            return "MX500-SV-0999";
        }
        return this.messageId.getMessageId();
    }
}
