package com.example.planlekcji.ckziu_elektryk.client.replacments;

import java.util.Optional;

public interface ReplacementService {

    Optional<Replacement> getLatestReplacement();

    Optional<Replacement> getReplacement(String fileName);
}
