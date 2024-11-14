package com.utn.interactiveconsortium.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Builder
@Data
public class PostDto {
    private Long postId;

    private String title;

    private String content;

    private LocalDateTime creationPostDate;

    private ConsortiumDto consortium;
}
