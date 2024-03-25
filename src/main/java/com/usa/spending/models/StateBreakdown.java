package com.usa.spending.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Embeddable
public class StateBreakdown {
    @Column(name = "award_type")
    private String type;
    @Column(name = "award_amount")
    private Double amount;

    private Integer count;
}
