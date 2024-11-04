package com.example.planlekcji.ckziu_elektryk.client;

public interface ReplacementService {

    Replacement getLatestReplacement();

    Replacement getReplacement(String fileName);
}
