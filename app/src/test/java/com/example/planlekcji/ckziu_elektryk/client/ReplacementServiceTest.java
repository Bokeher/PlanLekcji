package com.example.planlekcji.ckziu_elektryk.client;


import static org.junit.Assert.assertNotNull;

import com.example.planlekcji.ckziu_elektryk.client.replacements.Replacement;
import com.example.planlekcji.ckziu_elektryk.client.replacements.ReplacementRequest;
import com.example.planlekcji.ckziu_elektryk.client.replacements.ReplacementService;
import com.example.planlekcji.ckziu_elektryk.client.replacements.ReplacementType;
import com.example.planlekcji.ckziu_elektryk.client.stubs.CKZiUElektrykClientStub;
import com.example.planlekcji.ckziu_elektryk.client.stubs.ConfigStub;
import com.example.planlekcji.ckziu_elektryk.client.stubs.TestConstants;
import com.example.planlekcji.ckziu_elektryk.client.utils.DateUtil;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class ReplacementServiceTest {

    private ReplacementService replacementService;
    private Config config;

    @Before
    public void init() throws IOException {
        config = new ConfigStub();
        CKZiUElektrykClient client = new CKZiUElektrykClientStub(config);

        replacementService = client.getReplacementService();
    }

    @Test
    public void shouldGetLatestReplacement() {
        List<Replacement> replacements = replacementService.getLatestReplacements();

        assertNotNull(replacements);
    }

    @Test
    public void shouldGetLatestReplacementWithModeClasses() {
        List<Replacement> replacements = replacementService.getLatestReplacements(ReplacementType.CLASSES);

        assertNotNull(replacements);
    }

    @Test
    public void shouldGetLatestReplacementWithModeClassesAndDate() {
        List<Replacement> replacements = replacementService.getReplacements(ReplacementType.CLASSES, DateUtil.parseDate(ReplacementRequest.REPLACEMENT_DATE_PATTERN, "2025-09-09"));

        assertNotNull(replacements);
    }

    @Test
    public void shouldGetReplacementsByPeriod() {
        Date startDate = DateUtil.parseDate(ReplacementRequest.REPLACEMENT_DATE_PATTERN, "2026-09-07");
        Date endDate = DateUtil.parseDate(ReplacementRequest.REPLACEMENT_DATE_PATTERN, "2026-09-11");

        config = Mockito.mock(Config.class);

        try(MockWebServer server = new MockWebServer()) {
            Mockito.when(config.getAPIUrl()).thenReturn(server.url("/").url().toString());
            Mockito.when(config.getToken()).thenReturn("token");
            server.enqueue(new MockResponse().
                    setResponseCode(200)
                    .setBody(TestConstants.RESPONSE_REPLACEMENTS_PERIOD));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<Date, List<Replacement>> replacements = replacementService.getReplacements(ReplacementType.TEACHERS, startDate, endDate);

        assertNotNull(replacements);
    }
}
