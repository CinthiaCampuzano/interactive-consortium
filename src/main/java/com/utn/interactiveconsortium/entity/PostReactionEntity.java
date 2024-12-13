package com.utn.interactiveconsortium.entity;

import com.utn.interactiveconsortium.enums.EPostReaction;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Table(name = "post_reaction")
@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostReactionEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long postReactionId;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostEntity post;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private PersonEntity person;

    @Enumerated(EnumType.STRING)
    private EPostReaction reaction;

    private LocalDateTime reactionDate;

}
