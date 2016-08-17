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
public class DcMxRuntimeException extends RuntimeException {

    /**
     * SerialVersion UID.
     */
    private static final long serialVersionUID = 1641003555111231350L;

    /**
     * デフォルトコンストラクタ.
     */
    public DcMxRuntimeException() {
        // TODO Auto-generated constructor stub
    }

    /**
     * コンストラクタ.
     * @param message メッセージ
     */
    public DcMxRuntimeException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * コンストラクタ.
     * @param cause 根本例外.
     */
    public DcMxRuntimeException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * コンストラクタ.
     * @param message メッセージ.
     * @param cause 根本例外.
     */
    public DcMxRuntimeException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

}
