package cc.powind.huron.core.model;

import java.util.List;

public class RealtimeWrapper <T> {

    private String extractorId;

    private List<T> realtimeList;

    public String getExtractorId() {
        return extractorId;
    }

    public void setExtractorId(String extractorId) {
        this.extractorId = extractorId;
    }

    public List<T> getRealtimeList() {
        return realtimeList;
    }

    public void setRealtimeList(List<T> realtimeList) {
        this.realtimeList = realtimeList;
    }
}
