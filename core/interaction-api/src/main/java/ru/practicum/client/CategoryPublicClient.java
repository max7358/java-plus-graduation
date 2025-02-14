package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;

import java.util.List;

@FeignClient(name = "event-service", contextId = "category-public", path = "/categories")
public interface CategoryPublicClient {
    @GetMapping
    List<CategoryDto> getCategories(
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    );

    @GetMapping("/{catId}")
    CategoryDto getCategoryById(@PathVariable Long catId);
}
