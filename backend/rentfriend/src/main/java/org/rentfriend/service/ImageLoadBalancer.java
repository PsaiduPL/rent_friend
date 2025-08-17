package org.rentfriend.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.rentfriend.exception.ImageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@Component
public class ImageLoadBalancer {
    ConcurrentMap<Integer, Integer> buckets;
    AtomicInteger atomicInteger = new AtomicInteger(0);
    @Value("${image.storage.buckets}")
    Integer maxBuckets;
    final Path baseLocation;
    final ObjectMapper objectMapper;

    public ImageLoadBalancer(ObjectMapper objectMapper, String location, ConcurrentMap<Integer, Integer> buckets) {
        this.objectMapper = objectMapper;
        this.baseLocation = Path.of(location);
        this.buckets = buckets;
        System.out.println(buckets instanceof ConcurrentHashMap);
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(baseLocation);
            for (int i = 0; i < maxBuckets; i++) {
                Files.createDirectories(baseLocation.resolve(Integer.toString(i)));
            }

        } catch (IOException e) {
            throw new ImageException("Could not initialize storage", e);
        }
        loadState();

    }

    @PreDestroy
    public void preDestroy() {
        saveState();
    }

    void loadState() {
        String config = "config.json";
        if (Path.of(config).toFile().exists()) {
            try {

                Map<String, Object> obj = objectMapper.readValue(Path.of(config).toFile(), new TypeReference<Map<String, Object>>() {
                });
                atomicInteger.set((Integer) obj.get("last_index"));
                Map<String, Object> tmp = (Map<String, Object>) obj.get("buckets");
                ConcurrentMap<Integer, Integer> tmp2 = tmp.entrySet().stream().collect(Collectors.toConcurrentMap(e -> Integer.parseInt(e.getKey()),
                    e -> (Integer) e.getValue()));

                buckets = new ConcurrentSkipListMap<Integer, Integer>(tmp2);
                log.info(buckets);
                Integer max = buckets.keySet().stream().max(Integer::compareTo).get();
                if (!max.equals(maxBuckets - 1)) {
                    for (int i = max + 1; i < maxBuckets; i++) {
                        buckets.put((Integer) i, 0);
                    }
                }
                log.info("state of balancer loaded");
            } catch (Exception e) {
                log.warn("failed to load balancer state IOError ", e);
            }


        } else {
            log.warn("failed to load balancer state file doesnt exists new init");
            buckets = new ConcurrentSkipListMap<>(
                Stream.iterate(0, (i) -> i + 1).limit(maxBuckets).collect(Collectors.toConcurrentMap(i -> i, i -> 0)));
            log.info("buckets {}", buckets);
        }

    }

    void saveState() {
        String config = "config.json";
        try {
            objectMapper.writeValue(Path.of(config).toFile(), Map.of("name", "image_load_balancer", "last_index", atomicInteger.get(),
                "buckets", buckets));
            log.info("state of balancer saved");
        } catch (IOException e) {
            log.warn("failed to save load balancer state");
        }
    }

    Integer nextBucket() {
        Integer minValue = buckets.values().stream().min(Integer::compareTo).get();
        Integer key = buckets.entrySet().stream().filter(entry -> entry.getValue().equals(minValue)).findFirst().get().getKey();
        return key;
    }

    void updateBucket(Integer index) {
        Integer bucketSize = this.buckets.get(index);
        bucketSize++;
        buckets.put(index, bucketSize);
    }
}
