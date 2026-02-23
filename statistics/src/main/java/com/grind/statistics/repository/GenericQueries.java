package com.grind.statistics.repository;

public class GenericQueries {
    public static final String Q_INGEST_EVENT = """
            INSERT
            INTO analytics.raw
            FORMAT JSONEachRow;
            """;
}
