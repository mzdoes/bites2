package com.example.mzdoes.bites2;

import java.io.Serializable;

/**
 * Created by zeucudatcapua2 on 3/22/18.
 */

public class ShortSource implements Serializable {

    private String id, name;

    public ShortSource() {
        id = "youtube";
        name = "YouTube";
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
