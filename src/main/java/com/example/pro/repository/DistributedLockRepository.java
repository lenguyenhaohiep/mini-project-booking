package com.example.pro.repository;

import com.example.pro.entity.DistributedLock;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistributedLockRepository extends MongoRepository<DistributedLock, String> {
    void deleteByResource(String resource);
}
