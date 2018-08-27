package com.virjar.hermes.hermesagent.host.manager;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.virjar.hermes.hermesagent.host.service.FontService;
import com.virjar.hermes.hermesagent.plugin.AgentCallback;
import com.virjar.hermes.hermesagent.plugin.AgentRegister;
import com.virjar.hermes.hermesagent.util.CommonUtils;
import com.virjar.hermes.hermesagent.util.Constant;

import org.apache.commons.lang3.StringUtils;

import java.util.TimerTask;

/**
 * Created by virjar on 2018/8/24.<br>
 * 保持和server的心跳，发现server挂掉之后，拉起server
 */

public class AgentDaemonTask extends TimerTask {
    private static final String TAG = "AgentDaemonTask";
    private Context context;
    private AgentCallback agentCallback;

    public AgentDaemonTask(Context context, AgentCallback agentCallback) {
        this.context = context;
        this.agentCallback = agentCallback;
    }

    @Override
    public void run() {
        String pingResponse = CommonUtils.pingServer(agentCallback.targetPackageName());
        if (StringUtils.equalsIgnoreCase(pingResponse, Constant.rebind)) {
            Log.i(TAG, "rebind service");
            AgentRegister.registerToServer(agentCallback, context);
            return;
        }
        if (StringUtils.equalsIgnoreCase(pingResponse, "true")) {
            return;
        }

        pingResponse = CommonUtils.pingServer(agentCallback.targetPackageName());
        if (StringUtils.equalsIgnoreCase(pingResponse, Constant.unknown)) {
            Intent service = new Intent(context, FontService.class);
            context.startService(service);
        }

    }
}
