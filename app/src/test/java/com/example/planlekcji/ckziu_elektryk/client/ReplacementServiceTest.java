package com.example.planlekcji.ckziu_elektryk.client;


import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.planlekcji.ckziu_elektryk.client.replacments.Replacement;
import com.example.planlekcji.ckziu_elektryk.client.replacments.ReplacementService;
import com.example.planlekcji.ckziu_elektryk.client.stubs.TestConstants;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

public class ReplacementServiceTest {

    private ReplacementService replacementService;

    @Before
    public void init() {
        Config config = mock(Config.class);

        when(config.getAPIUrl()).thenReturn(TestConstants.URL);
        when(config.getToken()).thenReturn(TestConstants.TOKEN);

        CKZiUElektrykClient client = new CKZiUElektrykClient(config);

        replacementService = client.getReplacementService();
    }

    @Test
    public void shouldGetLatestReplacement() {
        Optional<Replacement> latestReplacement = replacementService.getLatestReplacement();

        if (latestReplacement.isPresent()) {
            Replacement replacement = latestReplacement.get();

            assertNotNull(replacement.content());
            assertNotNull(replacement.fileName());
        }
    }

    @Test
    public void shouldGetReplacementByFileName() {
        Optional<Replacement> latestReplacement = replacementService.getReplacement("GRAFIK-NIEOBECNOSCI-NAUCZYCIELI-25_v1-pazdziernika-2024.pdf");

        if (latestReplacement.isPresent()) {
            Replacement replacement = latestReplacement.get();

            assertNotNull(replacement.content());
            assertNotNull(replacement.fileName());
        }
    }
}
