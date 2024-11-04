package com.example.planlekcji.ckziu_elektryk.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

public class ConfigTest {

    @Test
    public void shouldCreateConfig() {
        Config config = Config.getOrCreateConfig();

        assertNotNull(config);
    }

    @Test
    public void shouldReturnEmptyStringWhenValueIsCouldNotFindValue() {
        Config config = Config.getOrCreateConfig();

        String token = config.getToken();

        assertEquals("", token);
    }

    @Test
    public void shouldReturnToken() {
        Config config = mock(Config.class);
        when(config.getToken()).thenReturn("token");

        assertEquals("token", config.getToken());
    }
}
