package com.example.pro.service;

import com.example.pro.entity.DistributedLock;
import com.example.pro.repository.DistributedLockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class DistributedLockService {
    private static final int MAX_ATTEMPTS = 10;
    private static final long RETRY_DELAY_MS = 50;

    private final DistributedLockRepository lockRepository;

    /**
     * Acquires a distributed lock for the given resource. Retries on contention,
     * waiting for the current holder to release. The lock document carries a TTL
     * so it is automatically removed by MongoDB if the holder crashes.
     *
     * @throws DuplicateKeyException if the lock cannot be acquired after all retries
     */
    public void acquire(String resource) {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            try {
                lockRepository.save(
                    DistributedLock.builder()
                        .resource(resource)
                        .createdAt(Instant.now())
                        .build()
                );
                return;
            } catch (DuplicateKeyException e) {
                log.debug("Lock held for resource '{}', attempt {}/{}", resource, attempt + 1, MAX_ATTEMPTS);
                if (attempt == MAX_ATTEMPTS - 1) throw e;
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(ie);
                }
            }
        }
    }

    public void release(String resource) {
        lockRepository.deleteByResource(resource);
    }
}
