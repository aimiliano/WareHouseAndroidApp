package mystore.stormeco.gr.appmystore;

import java.util.HashMap;

public class EventBusMessage {

    protected String id;
    protected HashMap<String,Object> data = new HashMap<>();

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
