package io.github.ahmola.extensionfilter.spring_server.forbidden.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ReadForbiddenListResponseDTO(
        @Schema(description = "차단된 모든 확장자 리스트 응답 DTO", example = "exe")
        List<ReadForbiddenResponseDTO> extensions) {
}
