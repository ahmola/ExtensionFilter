package io.github.ahmola.extensionfilter.spring_server.forbidden.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateForbiddenResponseDTO(
        @Schema(description = "추가한 확장자 응답 DTO", example = "exe")
        Long id,
        String extensionName
) {
}
