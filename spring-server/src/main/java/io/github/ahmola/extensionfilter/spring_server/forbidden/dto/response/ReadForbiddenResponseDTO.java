package io.github.ahmola.extensionfilter.spring_server.forbidden.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReadForbiddenResponseDTO(
        @Schema(description = "확장자 조회 응답 DTO", example = "exe")
        Long id,
        String extensionName
) { }