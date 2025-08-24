package io.github.ahmola.extensionfilter.spring_server.forbidden.crud;

import io.github.ahmola.extensionfilter.spring_server.forbidden.dto.request.CreateForbiddenRequestDTO;
import io.github.ahmola.extensionfilter.spring_server.forbidden.dto.response.CreateForbiddenResponseDTO;
import io.github.ahmola.extensionfilter.spring_server.forbidden.dto.response.ReadForbiddenListResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/forbidden/crud")
@Tag(name = "Forbidden Extension", description = "차단 확장자 관리 API")
@RestController
public class ForbiddenCrudController {

    private final ForbiddenCrudService crudService;

    @Operation(summary = "확장자 전체 조회", description = "차단된 확장자 목록을 조회합니다")
    @GetMapping
    public ResponseEntity<ReadForbiddenListResponseDTO> getForbiddenList(){
        log.info("Get Request");
        ReadForbiddenListResponseDTO response = crudService.readForbiddenList();

        log.info("Send Response : {}", response );
        return ResponseEntity.ok(
                response
        );
    }

    @Operation(summary = "확장자 추가", description = "확장자를 추가하며, 즉시 Redis에 적재합니다.")
    @PostMapping
    public ResponseEntity<CreateForbiddenResponseDTO> postForbidden(
            @Parameter(description = "추가할 확장자 DTO")
            @Valid @RequestBody CreateForbiddenRequestDTO request){
        log.info("Create Request : {}", request);
        return new ResponseEntity<>(
                crudService.createForbidden(request)
                , HttpStatus.CREATED);
    }

    @Operation(summary = "확장자 삭제", description = "확장자를 삭제합니다.")
    @DeleteMapping
    public ResponseEntity<Boolean> deleteForbidden(@RequestParam String extensionName){
        log.info("Delete Request : {}", extensionName);
        return ResponseEntity.ok(
                crudService.deleteForbidden(extensionName)
        );
    }
}
