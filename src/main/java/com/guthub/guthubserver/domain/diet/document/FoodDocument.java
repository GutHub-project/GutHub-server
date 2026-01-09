package com.guthub.guthubserver.domain.diet.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "foods")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String name;

    @Field(type = FieldType.Float)
    private Float calories;

    @Field(type = FieldType.Float)
    private Float dietaryFiber;

    @Field(type = FieldType.Float)
    private Float probiotics;

    @Field(type = FieldType.Float)
    private Float saturatedFat;

    @Field(type = FieldType.Float)
    private Float sugar;

    @Field(type = FieldType.Float)
    private Float refinedCarbs;

    @Field(type = FieldType.Boolean)
    private Boolean isFlourBased;

    @Field(type = FieldType.Float)
    private Float flour;
}
