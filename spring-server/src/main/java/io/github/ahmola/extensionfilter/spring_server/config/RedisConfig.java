package io.github.ahmola.extensionfilter.spring_server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ahmola.extensionfilter.spring_server.forbidden.Forbidden;
import io.github.ahmola.extensionfilter.spring_server.forbidden.dto.response.ReadForbiddenListResponseDTO;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@EnableCaching
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, ReadForbiddenListResponseDTO> readListRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, ReadForbiddenListResponseDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setDefaultSerializer(new Jackson2JsonRedisSerializer<>(ReadForbiddenListResponseDTO.class));
        return template;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())
                )
                .entryTtl(Duration.ofSeconds(60));

        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
    }
}