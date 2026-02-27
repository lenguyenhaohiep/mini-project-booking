package com.example.pro.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "distributed_lock")
public class DistributedLock {
    @Id
    private String id;

    @Indexed(unique = true)
    @Field("resource")
    private String resource;

    @Indexed(expireAfter = "30s")
    @Field("created_at")
    private Instant createdAt;
}
