package com.utn.interactiveconsortium.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Table(name = "post")
@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long postId;

    private String title;

    private String content;

    private LocalDateTime creationPostDate;

    @ManyToOne
    @JoinColumn(name = "consortium_id")
    private ConsortiumEntity consortium;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostReactionEntity> postReactions;

}
