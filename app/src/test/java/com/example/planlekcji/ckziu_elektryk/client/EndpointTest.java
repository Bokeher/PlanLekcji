package com.example.planlekcji.ckziu_elektryk.client;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Map;

public class EndpointTest {

    @Test
    public void shouldCreateEndpointWithPlaceholders() {
        Endpoint endpoint = Endpoint.REPLACEMENTS_FROM_FILE
                .withPlaceholders(Map.of("{file_name}", "filename"));

        assertEquals("replacements/filename", endpoint.getName());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldCreateEndpointWithoutPlaceholders() {
        Endpoint.LATEST_REPLACEMENTS
                .withPlaceholders(Map.of("{file_name}", "filename"));
    }
}
