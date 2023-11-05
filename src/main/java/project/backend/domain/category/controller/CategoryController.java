package project.backend.domain.category.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.backend.domain.category.mapper.CategoryMapper;
import project.backend.domain.category.service.CategoryService;

@RestController
@RequestMapping("/api/categorys")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;


    @ApiOperation(
            value = "카테고리 목록 조회",
            notes = "카테고리 목록을 조회한다.")
    @GetMapping
    public ResponseEntity getCategoryList() {
        return ResponseEntity.status(HttpStatus.OK).body(categoryMapper.categorysToCategoryResponseDtos(categoryService.getCategoryList()));
    }
}
