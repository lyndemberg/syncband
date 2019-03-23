package com.mesh.syncband.socket;

import org.json.JSONObject;

public interface ObserverConnectionData {
    void handlerData(JSONObject jsonObject);
}
