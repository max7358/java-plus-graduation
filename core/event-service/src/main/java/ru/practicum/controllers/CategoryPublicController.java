package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.CategoryPublicClient;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.services.CategoryService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/categories")
public class CategoryPublicController implements CategoryPublicClient {
    private final CategoryService categoryService;

    public List<CategoryDto> getCategories(
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        return categoryService.getCategories(from, size);
    }

    public CategoryDto getCategoryById(@PathVariable Long catId) {
        return categoryService.getCategoryById(catId);
    }
}
