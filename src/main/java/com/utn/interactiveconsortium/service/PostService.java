package com.utn.interactiveconsortium.service;

import com.utn.interactiveconsortium.dto.PostDto;
import com.utn.interactiveconsortium.entity.ConsortiumEntity;
import com.utn.interactiveconsortium.entity.PostEntity;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.mapper.PostMapper;
import com.utn.interactiveconsortium.repository.ConsortiumRepository;
import com.utn.interactiveconsortium.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final ConsortiumRepository consortiumRepository;
    private final PostMapper postMapper;

    public Page<PostDto> getPosts(Long idConsortium, Pageable page) {
        return postMapper.toPage(postRepository.findByConsortiumConsortiumId(idConsortium, page));
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

}
