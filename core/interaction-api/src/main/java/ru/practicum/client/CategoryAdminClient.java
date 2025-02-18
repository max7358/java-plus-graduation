package ru.practicum.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;

@FeignClient(name = "event-service", contextId = "category-admin", path = "/admin/categories")
public interface CategoryAdminClient {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CategoryDto createCategory(@RequestBody @Valid NewCategoryDto newCategoryDto);

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCategory(@PathVariable Long catId);

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    CategoryDto updateCategory(@PathVariable Long catId, @RequestBody @Valid CategoryDto categoryDto);
}
