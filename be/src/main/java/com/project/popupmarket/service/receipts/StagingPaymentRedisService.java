package com.project.popupmarket.service.receipts;

import com.project.popupmarket.entity.StagingPayment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class StagingPaymentRedisService {
    private final RedisTemplate<String, Object> template;

    private String generateKey(String orderId) {
        return "staging:" + orderId;
    }

    public void save(String id, StagingPayment stagingPayment) {
        String key = generateKey(id);
        template.opsForValue().set(key, stagingPayment, 5L, TimeUnit.MINUTES);
    }

    public StagingPayment find(String id) {
        String key = generateKey(id);
        return (StagingPayment) template.opsForValue().get(key);
    }

    public void delete(String id) {
        String key = generateKey(id);
        template.delete(key);
    }
}
