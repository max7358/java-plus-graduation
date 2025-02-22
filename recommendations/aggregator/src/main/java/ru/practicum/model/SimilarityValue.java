package ru.practicum.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SimilarityValue {
    private double similarity;
    private double eventMinSum;

    public void updateSimilarity(double eventWeightSumRootA, double eventWeightSumRootB) {
        this.similarity = eventMinSum / (eventWeightSumRootA * eventWeightSumRootB);
    }
}