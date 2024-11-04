package com.example.planlekcji.ckziu_elektryk.client;


import org.junit.Before;
import org.junit.Test;

public class ReplacementServiceTest {

    private ReplacementService replacementService;

    @Before
    public void init() {
        CKZiUElektrykClient client = new CKZiUElektrykClient("token");

        replacementService = client.getReplacementService();
    }

    @Test
    public void shouldGetLatestReplacement() {
        Replacement latestReplacement = replacementService.getLatestReplacement();

        
    }
}
