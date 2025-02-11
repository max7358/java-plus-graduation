package ewm.mappers;

import ewm.dto.category.CategoryDto;
import ewm.dto.category.NewCategoryDto;
import ewm.models.Category;

public class CategoryMapper {
    public static CategoryDto toDto(Category model) {
        return new CategoryDto(
                model.getId(),
                model.getName()
        );
    }

    public static Category toModel(CategoryDto dto) {
        return new Category(
                dto.getId(),
                dto.getName()
        );
    }

    public static Category toModel(NewCategoryDto dto) {
        Category model = new Category();
        model.setName(dto.getName());
        return model;
    }
}
