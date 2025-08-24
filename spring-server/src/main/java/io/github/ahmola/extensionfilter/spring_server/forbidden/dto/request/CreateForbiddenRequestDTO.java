package io.github.ahmola.extensionfilter.spring_server.forbidden.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateForbiddenRequestDTO(
        @Schema(description = "추가할 확장자 이름 요청 DTO", example = "exe")
        @NotBlank @Size(min = 1, max = 20)
        @Pattern(regexp = "^[a-z0-9]{1,20}$")
        String extensionName
) {
}
