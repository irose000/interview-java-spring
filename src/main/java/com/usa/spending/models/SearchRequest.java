package com.usa.spending.models;

import java.util.List;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class SearchRequest {
    private boolean subawards;
    private int limit;
    private int page;
    private Filters filters;
    private List<String> fields;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class Filters {
        private List<String> award_type_codes;
        private List<TimePeriod> time_period;

        @Getter
        @Setter
        @AllArgsConstructor
        @NoArgsConstructor
        @ToString
        public static class TimePeriod {
            private String start_date;
            private String end_date;

        }
    }
}
