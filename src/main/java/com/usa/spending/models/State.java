package com.usa.spending.models;

import lombok.*;

import java.util.List;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class State {
    private @Id @GeneratedValue Long id;

    @Embedded
    private StateListing listing;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "overview_name")),
            @AttributeOverride(name = "code", column = @Column(name = "overview_code")),
            @AttributeOverride(name = "fips", column = @Column(name = "overview_fips")),
            @AttributeOverride(name = "type", column = @Column(name = "overview_type"))
    })
    private StateOverview overview;

    @ElementCollection
    // @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "type", column = @Column(name = "award_type")),
            @AttributeOverride(name = "amount", column = @Column(name = "award_amount")),
            @AttributeOverride(name = "count", column = @Column(name = "award_count"))
    })
    private List<StateBreakdown> breakdown;

    public State(StateListing listing, StateOverview overview, List<StateBreakdown> breakdown) {
        this.listing = listing;
        this.overview = overview;
        this.breakdown = breakdown;
    }
}
