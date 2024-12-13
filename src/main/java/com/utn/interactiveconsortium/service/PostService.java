package com.utn.interactiveconsortium.service;

import com.utn.interactiveconsortium.dto.PostDto;
import com.utn.interactiveconsortium.entity.ConsortiumEntity;
import com.utn.interactiveconsortium.entity.PersonEntity;
import com.utn.interactiveconsortium.entity.PostEntity;
import com.utn.interactiveconsortium.entity.PostReactionEntity;
import com.utn.interactiveconsortium.enums.EPostReaction;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.mapper.PostMapper;
import com.utn.interactiveconsortium.repository.ConsortiumRepository;
import com.utn.interactiveconsortium.repository.PostReactionRepository;
import com.utn.interactiveconsortium.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final PostReactionRepository postReactionRepository;

    private final ConsortiumRepository consortiumRepository;

    private final PostMapper postMapper;

    private final LoggedUserService loggedUserService;

    public Page<PostDto> getPosts(Long idConsortium, Pageable page) {
        Page<PostEntity> postEntities = postRepository.findByConsortiumConsortiumId(idConsortium, page);
        Page<PostDto> postDtoPage = postMapper.toPage(postEntities);

        postDtoPage.getContent().forEach(postDto -> {
            addReactionToPost(postDto, postEntities.stream()
                    .filter(postEntity -> postEntity.getPostId().equals(postDto.getPostId()))
                    .findFirst()
                    .orElseThrow()
            );
        });

        return postDtoPage;
    }

    public Page<PostDto> getPostsByTitle(Long idConsortium, String title, Pageable page) {
        return postMapper.toPage(postRepository.findByTitleContainingAndConsortiumId(idConsortium, title, page));
    }

    public PostDto createPost(PostDto postDto) throws EntityNotFoundException {

        PostEntity newPostEntity = postMapper.convertDtoToEntity(postDto);

        ConsortiumEntity consortium = consortiumRepository.findById(postDto.getConsortium().getConsortiumId())
                .orElseThrow(() -> new EntityNotFoundException("Consorcio no encontrado"));

        newPostEntity.setConsortium(consortium);
        newPostEntity.setCreationPostDate(LocalDateTime.now());

        postRepository.save(newPostEntity);

        return postMapper.convertEntityToDto(newPostEntity);
    }

    public void updatePost(PostDto postUpdateDto) throws EntityNotFoundException {

        boolean postExists = postRepository.existsById(postUpdateDto.getPostId());

        if(!postExists){
            throw new EntityNotFoundException("Publicación no encontrada");
        }

        PostEntity postToUpdateEntity = postRepository.findById(postUpdateDto.getPostId()).get();

        boolean isTitleChanged = !postToUpdateEntity.getTitle().equals(postUpdateDto.getTitle());
        boolean isContentChanged = !postToUpdateEntity.getContent().equals(postUpdateDto.getContent());

        if (isTitleChanged) {
            postToUpdateEntity.setTitle(postUpdateDto.getTitle());
        }

        if (isContentChanged) {
            postToUpdateEntity.setContent(postUpdateDto.getContent());
        }

        if (isTitleChanged || isContentChanged) {
            postToUpdateEntity.setCreationPostDate(LocalDateTime.now());
        }

        postRepository.save(postToUpdateEntity);
    }

    public void deletePost(Long postId) throws EntityNotFoundException {
        boolean postExists = postRepository.existsById(postId);

        if(!postExists){
            throw new EntityNotFoundException("Publiación no encontrada");
        }
        postRepository.deleteById(postId);
    }

    public PostDto reactToPost(Long postId, EPostReaction reaction) throws EntityNotFoundException {

        PostEntity postToUpdateEntity = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Publicación no encontrada"));
        PersonEntity loggedPerson = loggedUserService.getLoggedPerson();

        Optional<PostReactionEntity> postReactionOptional = postReactionRepository.findByPostPostIdAndPersonPersonId(postId, loggedPerson.getPersonId());

        PostReactionEntity postReaction;
        if (postReactionOptional.isPresent()) {
            postReaction = postReactionOptional.get();
            postReaction.setReaction(reaction);
            postReaction.setReactionDate(LocalDateTime.now());
        } else {
            postReaction = PostReactionEntity.builder()
                    .post(postToUpdateEntity)
                    .person(loggedPerson)
                    .reaction(reaction)
                    .reactionDate(LocalDateTime.now())
                    .build();
        }
        postReactionRepository.save(postReaction);

        PostDto postDto = postMapper.convertEntityToDto(postToUpdateEntity);
        addReactionToPost(postDto, postToUpdateEntity);
        postDto.setUserReaction(postReaction.getReaction());
        return postDto;
    }

    private void addReactionToPost(PostDto postDto, PostEntity postEntity) {
        Map<EPostReaction, Integer> reactions = postEntity.getPostReactions().stream()
                .collect(
                        () -> new HashMap<>(Map.of(
                                EPostReaction.THUMBS_UP, 0,
                                EPostReaction.THUMBS_DOWN, 0,
                                EPostReaction.CLAPS, 0,
                                EPostReaction.SAD_FACE, 0
                        )),
                        (map, postReaction) -> map.put(postReaction.getReaction(), map.get(postReaction.getReaction()) + 1),
                        HashMap::putAll
                );
        postDto.setReactions(reactions);
    }
}
