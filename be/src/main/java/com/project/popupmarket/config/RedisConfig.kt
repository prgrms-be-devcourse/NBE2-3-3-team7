package com.project.popupmarket.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@EnableCaching
@Configuration
class RedisConfig {
    @Value("\${spring.data.redis.host}")
    private val host: String? = null

    @Value("\${spring.data.redis.port}")
    private val port = 0

    @Value("\${spring.data.redis.password}")
    private val password: String? = null

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val config = RedisStandaloneConfiguration()
        config.hostName = host
        config.port = port
        config.setPassword(password)

        return LettuceConnectionFactory(config)
    }

    @Bean
    fun stringRedisTemplate(redisConnectionFactory: RedisConnectionFactory): StringRedisTemplate {
        return StringRedisTemplate(redisConnectionFactory)
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = redisConnectionFactory()

        // Jackson ObjectMapper 설정
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        objectMapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL
        )

        val serializer = GenericJackson2JsonRedisSerializer(objectMapper)

        template.defaultSerializer = serializer
        // 기본
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = serializer

        // 해시
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = serializer

        template.afterPropertiesSet()
        return template
    }

    // 캐시 설정
    @Bean
    fun redisCacheConfiguration(): RedisCacheConfiguration {
        return RedisCacheConfiguration
            .defaultCacheConfig()
            .entryTtl(Duration.ofDays(14))
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer()))
    }

    // 캐시 매니저
    @Bean
    fun cacheManager(redisConnectionFactory: RedisConnectionFactory): RedisCacheManager {
        return RedisCacheManager
            .builder(redisConnectionFactory)
            .cacheDefaults(redisCacheConfiguration())
            .build()
    }
}
