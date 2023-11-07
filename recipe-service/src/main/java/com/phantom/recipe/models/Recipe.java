package com.phantom.recipe.models;

import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "receipt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int recipeId;

    @Column(name = "title")
    private String title;

    @ElementCollection (fetch = FetchType.EAGER)
    private Map<Integer,Integer> productIDsAndQuantities;//todo refactor table

    @Column(name = "actions")
    private String actions;

    @Override
    public String toString() {
        return "Recipe{" +
                "recipeId=" + recipeId +
                ", title='" + title + '\'' +
                ", productIDsAndQuantities=" + productIDsAndQuantities +
                ", actions='" + actions + '\'' +
                '}';
    }
}
