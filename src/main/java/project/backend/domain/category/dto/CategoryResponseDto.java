package project.backend.domain.category.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDto {
    public Long id;
    public String name;
    public String engName;
    public String basicImage;
    public String clickImage;
}
