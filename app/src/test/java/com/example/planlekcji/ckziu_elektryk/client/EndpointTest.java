package com.example.planlekcji.ckziu_elektryk.client;

import static org.junit.Assert.assertEquals;

import com.example.planlekcji.ckziu_elektryk.client.common.Endpoint;

import org.junit.Test;

import java.util.Map;

public class EndpointTest {

    @Test
    public void shouldCreateEndpointWithPlaceholders() {
        Endpoint endpoint = Endpoint.REPLACEMENTS_BY_FILE_NAME
                .withPlaceholders(Map.of("{file_name}", "filename"));

        assertEquals("replacements/filename", endpoint.getName());
    }

    @Test
    public void shouldCreateEndpointWithPlaceholdersWithReplacingTwice() {
        Endpoint.REPLACEMENTS_BY_FILE_NAME
                .withPlaceholders(Map.of("{file_name}", "filename"));

        Endpoint endpoint2 = Endpoint.REPLACEMENTS_BY_FILE_NAME
                .withPlaceholders(Map.of("{file_name}", "filename2"));

        assertEquals("replacements/filename2", endpoint2.getName());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldCreateEndpointWithoutPlaceholders() {
        Endpoint.LATEST_REPLACEMENTS
                .withPlaceholders(Map.of("{file_name}", "filename"));
    }
}
