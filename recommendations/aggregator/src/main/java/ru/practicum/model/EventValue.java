package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventValue {
    private double eventWeightSum;
    private double eventWeightSumSquare;

    public void updateSumSquareWeight() {
        this.eventWeightSumSquare = Math.sqrt(this.eventWeightSum);
    }
}