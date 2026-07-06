package com.ns.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class DownloadService {

    private static final Logger log = LoggerFactory.getLogger(DownloadService.class);

    private final Map<String, DownloadEntry> store = new ConcurrentHashMap<>();
    private final AtomicLong totalSize = new AtomicLong(0);

    private final long ttlMillis;
    private final long maxStoreSize;

    public DownloadService(
            @Value("${download.ttl.minutes:5}") long ttlMinutes,
            @Value("${download.max-store.mb:500}") long maxStoreMb) {
        this.ttlMillis = ttlMinutes * 60_000;
        this.maxStoreSize = maxStoreMb * 1024L * 1024L;
    }

    public String store(byte[] content) {
        if (content == null) {
            throw new IllegalArgumentException("Content must not be null");
        }
        if (totalSize.addAndGet(content.length) > maxStoreSize) {
            totalSize.addAndGet(-content.length);
            throw new IllegalStateException("Download store is full. Max size: " + maxStoreSize + " bytes");
        }
        String token = UUID.randomUUID().toString();
        store.put(token, new DownloadEntry(content, System.currentTimeMillis()));
        log.debug("Stored download token={}, size={} bytes, totalSize={}", token, content.length, totalSize.get());
        return token;
    }

    public byte[] retrieve(String token) {
        DownloadEntry entry = store.remove(token);
        if (entry == null) {
            return null;
        }
        totalSize.addAndGet(-entry.content.length);
        log.debug("Retrieved download token={}, size={} bytes", token, entry.content.length);
        return entry.content;
    }

    @Scheduled(fixedRateString = "${download.cleanup.rate.ms:60000}")
    public void cleanup() {
        long now = System.currentTimeMillis();
        long expiredBefore = now - ttlMillis;
        store.entrySet().removeIf(e -> {
            boolean expired = e.getValue().timestamp < expiredBefore;
            if (expired) {
                totalSize.addAndGet(-e.getValue().content.length);
                log.debug("Cleaned up expired token={}", e.getKey());
            }
            return expired;
        });
    }

    public int getActiveCount() {
        return store.size();
    }

    public long getTotalSize() {
        return totalSize.get();
    }

    private record DownloadEntry(byte[] content, long timestamp) {}
}
