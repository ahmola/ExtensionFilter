//package io.github.ahmola.extensionfilter.spring_server.forbidden.crud;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.github.ahmola.extensionfilter.spring_server.forbidden.Forbidden;
//import io.github.ahmola.extensionfilter.spring_server.forbidden.ForbiddenRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Map;
//
//// 스케쥴링을 활용한 write-back을 하려고 했으나, 사용자의 행위와 캐시 데이터 간의 미스가 flush로 인해 일어나서 일단은 deprecated
//@Deprecated
//@Slf4j
//@RequiredArgsConstructor
//@Service
//public class ForbiddenCacheService {
//    private final ForbiddenRepository repository;
//    private final RedisTemplate<String, Object> redisTemplate;
//    private final ObjectMapper objectMapper;
//
//    private static final String CACHE_KEY = "cache:forbidden:pending";
//
//    private void pushToCache(String op, String extensionName) {
//        Map<String, String> entry = Map.of(
//                "op", op,
//                "extensionName", extensionName
//        );
//        try {
//            log.info("Push Entry : {}", entry);
//            redisTemplate.opsForList().rightPush(CACHE_KEY, objectMapper.writeValueAsString(entry));
//        } catch (JsonProcessingException e) {
//            log.error("캐시 기록 실패", e);
//        }
//    }
//
//    // 1분마다 flush
//    @Scheduled(fixedRate = 30000)
//    @Transactional
//    public void flushCacheToDb() {
//        log.info("캐시 → DB FLUSH 시작");
//        while (true) {
//            Object raw = redisTemplate.opsForList().leftPop(CACHE_KEY);
//            if (raw == null) break;
//
//            try {
//                Map<String, String> opEntry = objectMapper.readValue(raw.toString(), new TypeReference<>() {});
//                String op = opEntry.get("op");
//                String ext = opEntry.get("extensionName");
//
//
//                if ("ADD".equals(op)) {
//                    if (!repository.existsByExtensionName(ext)) {
//                        repository.save(new Forbidden(null, ext));
//                        log.info("DB 저장: {}", ext);
//                    }
//                } else if ("DELETE".equals(op)) {
//                    repository.deleteByExtensionName(ext);
//                    log.info("DB 삭제: {}", ext);
//                }
//
//            } catch (Exception e) {
//                log.warn("캐시 엔트리 처리 실패: {}", raw, e);
//            }
//        }
//        log.info("캐시 → DB FLUSH 완료");
//    }
//
//    public void createForbiddenToCache(String extensionName) {
//        log.info("Create Forbidden to Cache : {}", extensionName );
//        pushToCache("ADD", extensionName);
//    }
//
//    public void deleteForbiddenFromCache(String extensionName) {
//        log.info("Delete Forbidden to Cache : {}", extensionName);
//        pushToCache("DELETE", extensionName);
//    }
//}
