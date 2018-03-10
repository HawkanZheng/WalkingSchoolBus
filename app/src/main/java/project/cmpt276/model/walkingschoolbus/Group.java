package project.cmpt276.model.walkingschoolbus;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by Hawkan Zheng on 3/9/2018.
 */

public class Group {
    private long id;
    private String href;

    @JsonIgnore
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @JsonIgnore
    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
