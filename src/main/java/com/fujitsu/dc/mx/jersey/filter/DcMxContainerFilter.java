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
package com.fujitsu.dc.mx.jersey.filter;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujitsu.dc.mx.DcMxConfig;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

/**
 * 本アプリのリクエスト及びレスポンスに対してかけるフィルター.
 */
public class DcMxContainerFilter implements ContainerRequestFilter, ContainerResponseFilter {


    static Logger log = LoggerFactory.getLogger(DcMxContainerFilter.class);

    @Context
    private HttpServletRequest httpServletRequest;

    @Override
    public ContainerRequest filter(ContainerRequest req) {
        // リクエスト受付時のログを出力する。
        requestLog(req);

        // リクエストの時間を記録する
        long requestTime = System.currentTimeMillis();
        // リクエストの時間をセッションに保存する
        this.httpServletRequest.setAttribute("requestTime", requestTime);
        return req;
    }

    @Override
    public ContainerResponse filter(ContainerRequest req, ContainerResponse resp) {
        responseLog(resp);
        return resp;
    }

    /**
     * リクエストログ出力.
     * @param request
     * @param response
     */
    private void requestLog(final ContainerRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("[" + DcMxConfig.getMxVersion() + "] " + "Started. ");
        sb.append(request.getMethod());
        sb.append(" ");
        sb.append(request.getRequestUri().toString());
        sb.append(" ");
        sb.append(this.httpServletRequest.getRemoteAddr());
        log.info(sb.toString());
    }

    /**
     * レスポンスログ出力.
     * @param response
     */
    private void responseLog(final ContainerResponse response) {
        StringBuilder sb = new StringBuilder();
        sb.append("[" + DcMxConfig.getMxVersion() + "] " + "Completed. ");
        sb.append(response.getStatus());
        sb.append(" ");

        // レスポンスの時間を記録する
        long responseTime = System.currentTimeMillis();
        // セッションからリクエストの時間を取り出す
        long requestTime = (Long) this.httpServletRequest.getAttribute("requestTime");
        // レスポンスとリクエストの時間差を出力する
        sb.append((responseTime - requestTime) + "ms");
        log.info(sb.toString());
    }

}
