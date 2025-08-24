package io.github.ahmola.extensionfilter.spring_server.forbidden.crud;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ahmola.extensionfilter.spring_server.forbidden.Forbidden;
import io.github.ahmola.extensionfilter.spring_server.forbidden.ForbiddenRepository;
import io.github.ahmola.extensionfilter.spring_server.forbidden.dto.request.CreateForbiddenRequestDTO;
import io.github.ahmola.extensionfilter.spring_server.forbidden.dto.response.CreateForbiddenResponseDTO;
import io.github.ahmola.extensionfilter.spring_server.forbidden.dto.response.ReadForbiddenListResponseDTO;
import io.github.ahmola.extensionfilter.spring_server.forbidden.dto.response.ReadForbiddenResponseDTO;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class ForbiddenCrudService {
    private final ForbiddenRepository repository;

    @Cacheable(value = "cache:forbidden:list:v3", key = "'all'")
    @Transactional(readOnly = true)
    public ReadForbiddenListResponseDTO readForbiddenList() {
        return new ReadForbiddenListResponseDTO(
                repository.findAll().stream()
                        .map(f -> new ReadForbiddenResponseDTO(f.getId(), f.getExtensionName()))
                        .toList()
        );
    }

    // 생성 후 즉시 캐시 적재, 객체의 id를 저장
    @CacheEvict(value = "cache:forbidden:list:v3", allEntries = true)
    @Transactional
    public CreateForbiddenResponseDTO createForbidden(CreateForbiddenRequestDTO request){
        if (request == null || request.extensionName() == null || request.extensionName().isBlank())
            throw new IllegalArgumentException("extensionName must not be blank");
        log.info("Create Request : {}", request);

        if (repository.existsByExtensionName(request.extensionName()))
            throw new EntityExistsException("Already got " + request);

        Forbidden forbidden = repository.save(Forbidden.builder()
                .extensionName(request.extensionName())
                .build());
        return new CreateForbiddenResponseDTO(
                forbidden.getId(),
                forbidden.getExtensionName());
    }

    // 키 무효화
    @CacheEvict(value = "cache:forbidden:list:v3", allEntries = true)
    @Transactional
    public Boolean deleteForbidden(String extensionName){
        if (extensionName == null || extensionName.isBlank())
            throw new IllegalArgumentException("extensionName must not be blank");
        log.info("Delete Request : {}", extensionName);

        repository.deleteByExtensionName(extensionName);
        return true;
    }
}
