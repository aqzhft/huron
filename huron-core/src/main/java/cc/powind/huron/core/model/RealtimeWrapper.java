package cc.powind.huron.core.model;

import java.util.List;

public class RealtimeWrapper <T> {

    private String extractorId;

    private String extractorName;

    private String ipAddress;

    private List<T> realtimeList;

    public String getExtractorId() {
        return extractorId;
    }

    public void setExtractorId(String extractorId) {
        this.extractorId = extractorId;
    }

    public String getExtractorName() {
        return extractorName;
    }

    public void setExtractorName(String extractorName) {
        this.extractorName = extractorName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public List<T> getRealtimeList() {
        return realtimeList;
    }

    public void setRealtimeList(List<T> realtimeList) {
        this.realtimeList = realtimeList;
    }
}
