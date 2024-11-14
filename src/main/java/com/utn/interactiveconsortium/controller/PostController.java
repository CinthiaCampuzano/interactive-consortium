package com.utn.interactiveconsortium.controller;

import com.utn.interactiveconsortium.dto.PostDto;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

//    Administrador obtiene todos los posts de un consorcio
//    Resiente y propietario obtienen todos los posts de un consorcio

    @GetMapping
    public Page<PostDto> getPosts(@RequestParam Long idConsortium, Pageable page) {
        return postService.getPosts(idConsortium, page);
    }

//    Administrador obtiene un post por su titulo
//    Resiente y propietario obtienen un post por su titulo
    @GetMapping(value = "byTitle")
    public Page<PostDto> getPostsByTitle(@RequestParam Long idConsortium, @RequestParam(required = false) String title, Pageable page) {
        return postService.getPostsByTitle(idConsortium, title, page);
    }

//    Administrador crea un post
   @PostMapping
    public PostDto createPost(@RequestBody PostDto postDto) throws EntityNotFoundException {
        return postService.createPost(postDto);
    }

//    Administrador actualiza un post
    @PutMapping
    public void updatePost(@RequestBody PostDto postUpdateDto) throws EntityNotFoundException {
        postService.updatePost(postUpdateDto);
    }

//    Administrador elimina un post
    @DeleteMapping(value = "{postId}")
    public void deletePost(@PathVariable Long postId) throws EntityNotFoundException {
        postService.deletePost(postId);
    }



}
