package com.virjar.hermes.hermesagent.host;

import android.content.Context;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import com.virjar.hermes.hermesagent.bean.CommonRes;
import com.virjar.hermes.hermesagent.host.manager.StartAppTask;
import com.virjar.hermes.hermesagent.util.CommonUtils;
import com.virjar.hermes.hermesagent.util.Constant;
import com.virjar.hermes.hermesagent.util.HttpClientUtils;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by virjar on 2018/8/23.
 */

public class HttpServer {

    private static AsyncHttpServer server = null;
    private static AsyncServer mAsyncServer = null;

    public void startServer(Context context) {
        if (ping()) {
            return;
        }
        if (server != null) {
            server.stop();
            mAsyncServer.stop();
        }

        server = new AsyncHttpServer();
        mAsyncServer = new AsyncServer();

        bindPingCommand(server);
        bindStartAppCommand(server, context);

    }

    private void bindStartAppCommand(AsyncHttpServer server, final Context context) {
        server.get(Constant.startAppPath, new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                String app = request.getQuery().getString("app");
                if (StringUtils.isBlank(app)) {
                    CommonUtils.sendJSON(response, CommonRes.failed("the param {app} must exist"));
                    return;
                }
                new StartAppTask(app, context, response).execute();
            }
        });
    }


    private void bindPingCommand(AsyncHttpServer server) {
        server.get(Constant.httpServerPingPath, new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                response.send("true");
            }
        });
    }

    private boolean ping() {
        String url = "http://" + CommonUtils.getLocalIp() + "：" + Constant.httpServerPort + Constant.httpServerPingPath;
        return StringUtils.equalsIgnoreCase(HttpClientUtils.getRequest(url), "true");
    }
}