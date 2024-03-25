
package com.usa.spending.controllers;

import com.usa.spending.models.*;
import com.usa.spending.services.StateAwardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;

import org.springdoc.core.converters.models.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Slf4j
@RequestMapping("/api/v2/search")
public class SearchController {
    private final StateAwardService stateAwardService;
    private final StateAwardRepository repository;

    private static final String EXAMPLE_SEARCH = """
            {
                "subawards": false,
                "limit": 10,
                "page": 1,
                "filters": {
                    "award_type_codes": ["A", "B", "C"],
                    "time_period": [{"start_date": "2018-10-01", "end_date": "2019-09-30"}]
                },
                "fields": [
                    "Award ID",
                    "Recipient Name",
                    "Start Date",
                    "End Date",
                    "Award Amount",
                    "Awarding Agency",
                    "Awarding Sub Agency",
                    "Contract Award Type",
                    "Award Type",
                    "Funding Agency",
                    "Funding Sub Agency"
                ]
            }
                """;

    public SearchController(StateAwardService stateAwardService, StateAwardRepository repository) {
        this.stateAwardService = stateAwardService;
        this.repository = repository;
    }

    @PostMapping("/spending_by_award/")
    @Operation(summary = "Get detailed spending information with custom filters by award")
    public ResponseEntity<String> searchSpendingByAward(
            @Parameter(description = "Search with multiple filters across multiple fields", required = true, example = EXAMPLE_SEARCH) SearchRequest searchRequest) {
        String url = "https://api.usaspending.gov/api/v2/search/spending_by_award/";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // YourRequest requestData = new YourRequest();
        // populate requestData with your data

        HttpEntity<SearchRequest> request = new HttpEntity<>(searchRequest, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request,
                String.class);

        String jsonResponse = response.getBody();
        return response;
    }
}