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
public class StateListing {
    private String fips; // Federal Information Processing Standards (FIPS) code
    private String code; // State abbreviation (i.e. "AK")
    private String name; // State name (i.e. "Alaska")

    // TODO #7 -- convert to Enum based on types on:
    // https://github.com/fedspendingtransparency/usaspending-api/blob/master/usaspending_api/api_contracts/contracts/v2/recipient/state.md
    private String type;

    private Double amount; // Total dollar amount of awards
    private Integer count; // Total number of awards
}
