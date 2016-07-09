package com.byids.hy.byidstest.controlUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hy on 2016/7/9.
 */
public class LightControl {
    private int flag = 0;
    private String controlProtocol;
    private String machineName;

    private String controlSence;
    private String houseDBName;

    public LightControl(String houseDBName, String controlProtocol, String machineName, String controlData, String controlSence) {
        this.houseDBName = houseDBName;
        this.controlProtocol = controlProtocol;
        this.machineName = machineName;

        this.controlSence = controlSence;
    }

    public void toContorl(){
        JSONObject CommandData = new JSONObject();
        JSONObject controlData=new JSONObject();
        try {
            CommandData.put("controlProtocol",controlProtocol);
            CommandData.put("machineName",machineName);
            CommandData.put("controlData",controlData);
            CommandData.put("controlSence",controlSence);
            CommandData.put("houseDBName",houseDBName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
