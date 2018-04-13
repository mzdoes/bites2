package com.example.mzdoes.bites2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zeucudatcapua2 on 3/22/18.
 */

public class SourceList implements Serializable {
    private String status;
    private List<NewsSource> sources;

    public SourceList() {
        status = "ok";
        sources = new ArrayList<>();
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public List<NewsSource> getSourceList() {
        return sources;
    }
    public void setSourceList(List<NewsSource> sourceList) {
        this.sources = sourceList;
    }

    @Override
    public String toString() {
        return "SourceList{" +
                "status='" + status + '\'' +
                ", sourceList=" + sources +
                '}';
    }
}
