package com.usa.spending.controllers;

import com.usa.spending.models.*;
import com.usa.spending.services.StateAwardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.converters.models.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v2/recipient/state")
public class StateAwardController {
    private final StateAwardService stateAwardService;
    private final StateAwardRepository repository;

    private static final String EXAMPLE_SORT = """
              {
              "sort": [
                "amount,asc"
              ]
            }
            """;

    public StateAwardController(StateAwardService stateAwardService, StateAwardRepository repository) {
        this.stateAwardService = stateAwardService;
        this.repository = repository;
    }

    // Called after initialization to fetch data from usa spending API and store it
    // in a local database
    @PostConstruct
    private void postConstruct() {
        this.stateAwardService.preLoadDatabase();
    }

    @GetMapping("/all")
    @Operation(summary = "Get all basic spending information for each state")
    public ResponseEntity<StateListing[]> getAll(
            @Parameter(description = "Sort by a field", required = false, example = EXAMPLE_SORT) Sort sort) {
        if (sort != null)
            return new ResponseEntity<>(this.stateAwardService.getAllSorted(sort), HttpStatus.OK);
        else
            return new ResponseEntity<>(this.stateAwardService.getAll(), HttpStatus.OK);
    }

    // TODO #3 -- Get detailed spending information for state by FIPS code
    @GetMapping("/{fips}")
    @Operation(summary = "Get detailed spending information for specific state by FIPS code")
    public ResponseEntity<StateOverview> getDetail(@PathVariable String fips) {
        return new ResponseEntity<>(this.stateAwardService.getForStateByFips(fips), HttpStatus.OK);
    }

    // TODO #5 -- Get all awards for a specific state by state code
    @GetMapping("/awards/{fips}")
    @Operation(summary = "Get details on all awards for a specific state by FIPS code")
    public ResponseEntity<StateBreakdown[]> getAwardsDetail(@PathVariable String fips) {
        return new ResponseEntity<>(this.stateAwardService.getAwardsForStateByFips(fips), HttpStatus.OK);
    }

    // TODO #6 -- Implement a POST request to perform advanced querying/filtering of
    // data according to the public api search schema
    // -- consult documentation (https://api.usaspending.gov/docs/endpoints) for
    // information on the /api/v2/search/... functionality
    // @PostMapping("")

    // SEE "SearchController.java" for implementation
}
