package com.phantom.recipe.mappers;

import com.phantom.recipe.models.Recipe;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;

@SpringBootTest
class RecipeDTOMapperTest {

    private final RecipeDTOMapper recipeDTOMapper;

    private final WebClient.Builder webClientBuilder;

    @Autowired
    RecipeDTOMapperTest(RecipeDTOMapper recipeDTOMapper, WebClient.Builder webClientBuilder) {
        this.recipeDTOMapper = recipeDTOMapper;
        this.webClientBuilder = webClientBuilder;
    }


}