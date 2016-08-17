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
package com.fujitsu.dc.mx.tool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import net.spy.memcached.ConnectionObserver;
import net.spy.memcached.internal.OperationFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujitsu.dc.mx.DcMxConfig;
import com.fujitsu.dc.mx.DcMxException;
import com.fujitsu.dc.mx.DcMxMessageId;
import com.fujitsu.dc.mx.control.pcs.ReadDeleteModeController;

/**
 * memcachedにReadDeleteModeを設定/解除するクラス.
 */
public class MemcachedReadDeleteModeController implements ReadDeleteModeController {

    private static final int PORT_MAX = 65535;
    private static final int PORT_MIN = 0;

    static Logger log = LoggerFactory.getLogger(MemcachedReadDeleteModeController.class);

    net.spy.memcached.MemcachedClient spyClient = null;
    private volatile boolean isConnected = false;

    /**
     * コンストラクタ.
     * @throws DcMxException インスタンスの生成に失敗.
     */
    public MemcachedReadDeleteModeController() throws DcMxException {
        try {
            String host = DcMxConfig.get(MemcachedReadDeleteModeController.class.getName() + ".host");
            String port = DcMxConfig.get(MemcachedReadDeleteModeController.class.getName() + ".port");
            int iPort = Integer.valueOf(port);
            if (iPort > PORT_MAX || iPort < PORT_MIN) {
                // memcachedのポート番号に対して、ポート番号の範囲外の値が指定された
                log.info("Memcached port number is out of range.");
                throw new DcMxException(new DcMxMessageId("MX500-SV-0001"));
            }
            this.spyClient = new net.spy.memcached.MemcachedClient(new InetSocketAddress(host, iPort));
            this.spyClient.addObserver(new ConnectionObserver() {

                @Override
                public void connectionLost(SocketAddress sa) {
                    isConnected = false;
                }

                @Override
                public void connectionEstablished(SocketAddress sa,
                        int reconnectCount) {
                    isConnected = true;
                }
            });
        } catch (NumberFormatException e) {
            log.info("Failed to get memcahced client as configuration is invalid.");
            throw new DcMxException(new DcMxMessageId("MX500-SV-0001"), e);
        } catch (IOException e) {
            log.info("Failed to get memcahced client with IOException.");
            throw new DcMxException(new DcMxMessageId("MX500-SV-0001"), e);
        }
    }

    @Override
    public synchronized void setReadDeleteMode(String key, Object data) throws DcMxException {
        // memcachedに接続出来るさサーバがない場合はエラーを返却する
        if (!isConnectedMemcached()) {
            throw new DcMxException(new DcMxMessageId("MX500-SV-0002"));
        }
        OperationFuture<Boolean> response = null;
        try {
            response = this.spyClient.set(key, 0, data);
        } catch (Exception e) {
            log.info("Failed to set ReadDelete mode to memcached.");
            throw new DcMxException(new DcMxMessageId("MX500-SV-0002"), e);
        }
        if (response != null && !response.getStatus().isSuccess()) {
            throw new DcMxException(new DcMxMessageId("MX500-SV-0002"));
        }
    }

    @Override
    public synchronized void removeReadDeleteMode(String key) throws DcMxException {
        // memcachedに接続出来るさサーバがない場合はエラーを返却する
        if (!isConnectedMemcached()) {
            throw new DcMxException(new DcMxMessageId("MX500-SV-0002"));
        }
        OperationFuture<Boolean> response = null;
        try {
            response = this.spyClient.delete(key);
        } catch (Exception e) {
            log.info("Failed to release ReadDelete mode from memcached.");
            throw new DcMxException(new DcMxMessageId("MX500-SV-0002"), e);
        }
        if (response != null && !response.getStatus().isSuccess()
                && !response.getStatus().getMessage().equals("NOT_FOUND")) {
            throw new DcMxException(new DcMxMessageId("MX500-SV-0002"));
        }
    }

    private boolean isConnectedMemcached() {
        if (isConnected) {
            return isConnected;
        }
        try {
            Thread.sleep(DcMxConfig.getWaitForMemcached());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return isConnected;
    }
}
