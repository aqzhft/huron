package cc.powind.huron.core.collect;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ExtractInfo {

    private String extractorId;

    private String extractorName;

    private String ipAddress;

    private final Map<String, RealtimeInfo> detailMap = new ConcurrentHashMap<>(2 << 8);

    public ExtractInfo(String extractorId, String extractorName) {
        this.extractorId = extractorId;
        this.extractorName = extractorName;
    }

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

    public Instant getLastTime() {
        return detailMap.values().stream().map(RealtimeInfo::getLastTime).max(Instant::compareTo).orElse(null);
    }

    public Long getAmount() {
        return detailMap.values().stream().mapToLong(info -> info.getAmount().longValue()).count();
    }

    public void flush(String extractorName, String ipAddress, String alias, long size) {
        this.extractorName = extractorName;
        this.ipAddress = ipAddress;
        RealtimeInfo realtimeInfo = detailMap.computeIfAbsent(alias, key -> new RealtimeInfo(alias));
        realtimeInfo.plush(size);
    }

    private class RealtimeInfo {

        private String alias;

        private Instant lastTime;

        private AtomicLong amount;

        public RealtimeInfo(String alias) {
            this.alias = alias;
            this.lastTime = Instant.now();
            this.amount = new AtomicLong(0);
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public Instant getLastTime() {
            return lastTime;
        }

        public void setLastTime(Instant lastTime) {
            this.lastTime = lastTime;
        }

        public AtomicLong getAmount() {
            return amount;
        }

        public void setAmount(AtomicLong amount) {
            this.amount = amount;
        }

        public void plush(long size) {
            this.amount.addAndGet(size);
            this.lastTime = Instant.now();
        }
    }
}
