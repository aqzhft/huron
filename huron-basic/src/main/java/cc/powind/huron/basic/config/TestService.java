package cc.powind.huron.basic.config;

import cc.powind.huron.core.collect.CollectService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TestService {

    @Autowired
    private CollectService collectService;

    @Autowired
    private ObjectMapper mapper;

    private final static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

    @PostConstruct
    public void init() throws JsonProcessingException {

        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        com.sun.management.OperatingSystemMXBean xx = (com.sun.management.OperatingSystemMXBean) operatingSystemMXBean;

        File[] files = File.listRoots();
        for (File file : files) {
            System.out.println(file.getAbsolutePath());
        }

        executorService.scheduleAtFixedRate(() -> {

            UsageRealtime realtime = new UsageRealtime();
            realtime.setEquipmentId("00");
            realtime.setIndicatorId("01");
            realtime.setTime(Instant.now());
            realtime.setTotal((double) xx.getTotalPhysicalMemorySize());
            realtime.setUsed((double) (xx.getTotalPhysicalMemorySize() - xx.getFreePhysicalMemorySize()));

            collectService.collect(realtime);

        }, 0, 1, TimeUnit.SECONDS);
    }
}
