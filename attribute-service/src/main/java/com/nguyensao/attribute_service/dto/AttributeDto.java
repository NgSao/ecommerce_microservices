package com.nguyensao.attribute_service.dto;

import lombok.Data;

@Data
public class AttributeDto {
    private Long id;
    private String name;
    private Long groupId;
    private String groupName;
}