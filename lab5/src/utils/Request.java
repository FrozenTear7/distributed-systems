package utils;

import java.io.Serializable;

public class Request implements Serializable {
    private RequestType type;
    private String title;

    public Request(RequestType type, String line) {
        this.type = type;
        title = line.split(" ")[1];
    }

    public RequestType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }
}
