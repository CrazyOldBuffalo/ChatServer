package sample;

import org.json.simple.*;

public class Message {
    private String msg;
    private String clientName;
    private String date;

    public Message(String sclientName, String smessage, String sdate) {
        this.clientName = sclientName;
        this.msg = smessage;
        this.date = sdate;
    }

    public String getMessage() {
        return msg;
    }

    public String getDate() {
        return date;
    }

    public String getClientName() {
        return clientName;
    }

    @SuppressWarnings("unchecked")
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("clientName", clientName);
        json.put("Message", msg);
        json.put("Date", date);
        return json;
    }
}
