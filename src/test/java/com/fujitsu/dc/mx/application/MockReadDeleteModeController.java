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
package com.fujitsu.dc.mx.application;

import com.fujitsu.dc.mx.DcMxException;
import com.fujitsu.dc.mx.control.pcs.ReadDeleteModeController;

/**
 * ReadDeleteModeControllerのモッククラス.
 */
public class MockReadDeleteModeController implements ReadDeleteModeController {

    private static MockReadDeleteModeController singleton = null;
    private boolean locked = false;

    /**
     * コンストラクタ.
     */
    public MockReadDeleteModeController() {
        if (singleton == null) {
            singleton = this;
        }
    }

    /**
     * モックのインスタンスを返却するメソッド.
     * @return MockReadDeleteModeController
     */
    public static MockReadDeleteModeController getInstance() {
        if (singleton == null) {
            singleton = new MockReadDeleteModeController();
        }
        return singleton;
    }

    @Override
    public void setReadDeleteMode(String key, Object data) throws DcMxException {
        setMode(true);
    }

    @Override
    public void removeReadDeleteMode(String key) throws DcMxException {
        setMode(false);
    }

    /**
     * モックでのロック状態を返却するメソッド.
     * @return true: ロック中 false:ロック中ではない
     */
    public boolean isLocked() {
        return singleton.locked;
    }

    /**
     * モックにロック状態をセットするメソッド.
     * @param mode ロック状態
     */
    public void setMode(boolean mode) {
        singleton.locked = mode;
    }

}
