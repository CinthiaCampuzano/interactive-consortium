package com.utn.interactiveconsortium.dto;

import com.utn.interactiveconsortium.enums.EPostReaction;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Builder
@Data
public class PostDto {

    private Long postId;

    private String title;

    private String content;

    private LocalDateTime creationPostDate;

    private ConsortiumDto consortium;

    private Map<EPostReaction, Integer> reactions = new HashMap<>();

    private EPostReaction userReaction;

}
