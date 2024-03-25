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
public class StateOverview {
    private String name;
    private String code;
    private String fips;
    private String type;
    private Long population;
    private Integer pop_year;
    private String pop_source;
    private Double median_household_income;
    private Integer mhi_year;
    private String mhi_source;
    private Long total_prime_amount;
    private Long total_prime_awards;
    private Long total_face_value_loan_amount;
    private Long total_face_value_loan_prime_awards;
    private Double award_amount_per_capita;
}
