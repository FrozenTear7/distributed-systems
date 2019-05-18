package utils;

import java.io.Serializable;

public class Request implements Serializable {
    private RequestType type;
    private String title;
    private String actor;

    public Request(RequestType type, String line) {
        this.type = type;
        title = line.split(" ")[1];
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public RequestType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getActor() {
        return actor;
    }
}
