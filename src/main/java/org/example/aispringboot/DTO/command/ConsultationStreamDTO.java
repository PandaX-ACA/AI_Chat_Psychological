package org.example.aispringboot.DTO.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ConsultationStreamDTO {
    @NotBlank(message = "sessionId不能为空")
    private String sessionId;

    @NotBlank(message = "userMessage不能为空")
    @Size(max = 2000, message = "userMessage长度不能超过2000个字符")
    private String userMessage;

}
