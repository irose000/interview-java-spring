package com.usa.spending.services;

import com.usa.spending.models.StateListing;
import com.usa.spending.models.StateOverview;
import com.usa.spending.models.StateBreakdown;
import com.usa.spending.models.State;
import com.usa.spending.models.StateAwardRepository;

import org.springdoc.core.converters.models.Sort;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.lang.reflect.Field;

@Service
@Slf4j
public class StateAwardService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String STATE_SEARCH = "https://api.usaspending.gov/api/v2/recipient/state/";
    private final String STATE_DETAIL = "https://api.usaspending.gov/api/v2/recipient/state/{0}/";
    private final String AWARDS_DETAIL = "https://api.usaspending.gov/api/v2/recipient/state/awards/{0}/";
    @Autowired
    private StateAwardRepository repository;

    public StateAwardService() {
    }

    private class MultiLevelSorter {
        private enum Order {
            asc,
            desc
        }

        private static class SortCriteria {
            String propertyName;
            Order order;

            public SortCriteria(String propertyName, Order order) {
                this.propertyName = propertyName;
                this.order = order;
            }
        }

        private static <T> Comparator<T> createComparator(List<String> criteriaStr, Class<T> classToSort) {
            //System.out.println("criteriaStr before splitting: " + criteriaStr.toString());
            List<String> resultList = new ArrayList<>();

            for (String item : criteriaStr) {
                String[] parts = item.trim().split(",");
                resultList.addAll(Arrays.asList(parts));
            }

            List<SortCriteria> criteria = new ArrayList<SortCriteria>();
            int i = 0;
            //System.out.println("criteriaStr: " + resultList.toString());
            while (i < resultList.size()) {
                try {
                    //System.out.printf("i: %d    .get(i): %s\n", i, resultList.get(i));
                    criteria.add(new SortCriteria(resultList.get(i), Order.valueOf(resultList.get(i + 1))));
                } catch (IllegalArgumentException e) {
                    criteria.add(new SortCriteria(resultList.get(i), Order.asc));
                }
                i += 2;
            }

            Comparator<T> comparator = (o1, o2) -> 0;

            for (SortCriteria c : criteria) {
                comparator = comparator.thenComparing(createSingleComparator(c, classToSort));
            }
            return comparator;
        }

        private static <T> Comparator<T> createSingleComparator(SortCriteria criteria, Class<T> classToSort) {
            return (o1, o2) -> {
                try {
                    Field field = classToSort.getDeclaredField(criteria.propertyName);
                    field.setAccessible(true);
                    Comparable v1 = (Comparable) field.get(o1);
                    Comparable v2 = (Comparable) field.get(o2);
                    return criteria.order == Order.asc ? v1.compareTo(v2) : v2.compareTo(v1);
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            };
        }
    }

    /**
     * Get all basic spending information for each state.
     * 
     * @return Array of StateListing objects
     */

    public StateListing[] getAllRemote() {
        log.info("Fetching all state spending overview from: URL={}", STATE_SEARCH);
        return this.restTemplate.getForObject(STATE_SEARCH, StateListing[].class);
    }

    public StateListing[] getAll() {
        List<StateListing> allStates = this.repository.findAll()
                .stream()
                .map(State::getListing)
                .collect(Collectors.toList());
        StateListing[] listing = allStates.toArray(new StateListing[0]);
        return listing;
    }

    // TODO #1 -- Get all basic spending information for each state, ordered by
    // "amount".
    public StateListing[] getAllSortedByAmount() {
        StateListing[] listings = this.getAll();
        Arrays.sort(listings, Comparator.comparing(state -> state.getAmount()));
        log.info("Fetching all state spending overview, ordered by amount, from: URL={}", STATE_SEARCH);
        return listings;
    }

    // TODO #2 -- Implement generic sorting by client-requested fields using "Sort"
    // object
    public StateListing[] getAllSorted(Sort sort) {
        // hint use sort.getSort(), where .get(0) is the field and .get(1) is the
        // direction (asc/desc)
        StateListing[] listings = this.getAll();
        // System.out.println(sort.getSort());
        Comparator<StateListing> comparator = MultiLevelSorter.createComparator(sort.getSort(), StateListing.class);
        Arrays.sort(listings, comparator);
        return listings;
    }

    public StateOverview getForStateByFipsRemote(String fips) {
        String url = MessageFormat.format(this.STATE_DETAIL, fips);
        log.info("Fetching state spending detail from: URL={}, fips={}", url, fips);
        return this.restTemplate.getForObject(url, StateOverview.class);
    }

    // TODO #3 -- Get detailed spending information for state by FIPS code
    public StateOverview getForStateByFips(String fips) {
        String url = MessageFormat.format(this.STATE_DETAIL, fips);
        log.info("Fetching state spending detail from: URL={}, fips={}", url, fips);

        // return this.restTemplate.getForObject(url, StateOverview.class);
        return this.repository.findByListingFips(fips).getOverview();
    }

    public StateBreakdown[] getAwardsForStateByFipsRemote(String fips) {
        String url = MessageFormat.format(this.AWARDS_DETAIL, fips);
        return this.restTemplate.getForObject(url, StateBreakdown[].class);
    }

    public StateBreakdown[] getAwardsForStateByFips(String fips) {
        // String url = MessageFormat.format(this.AWARDS_DETAIL, fips);
        // return this.restTemplate.getForObject(url, StateBreakdown[].class);
        return (this.repository.findByListingFips(fips).getBreakdown()).toArray(new StateBreakdown[0]);
    }

    // TODO #4 -- Get all awards for a specific state by abbreviation
    // Unfortunately, the public API does not have an endpoint for this directly
    public StateBreakdown[] getAwardsForStateCode(String code) {
        // StateListing[] states = this.getAll();
        List<StateListing> statesList = Arrays.asList(this.getAll());
        statesList = statesList.stream()
                .filter(state -> state.getCode().equals(code))
                .collect(Collectors.toList());

        return this.getAwardsForStateByFips(statesList.get(0).getFips());
    }

    public void preLoadDatabase() {
        StateListing[] listings = this.getAllRemote();
        log.info("Fetching all state info");

        for (StateListing listing : listings) {
            List<StateBreakdown> breakdown = new ArrayList<>(
                    Arrays.asList(this.getAwardsForStateByFipsRemote(listing.getFips())));

            repository.save(new State(listing, this.getForStateByFipsRemote(listing.getFips()), breakdown));
        }
        log.info("Successfully fetched {} listings and their corresponding overviews and breakdowns", listings.length);
    }
}
