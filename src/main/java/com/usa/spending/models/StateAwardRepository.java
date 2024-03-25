package com.usa.spending.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateAwardRepository extends JpaRepository<State, String> {
    State findByListingFips(String fips);

    State findByListingCode(String code);

    State findByListingAmount(Double amount);
}
