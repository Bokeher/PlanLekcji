package com.example.planlekcji.ckziu_elektryk.client.replacements;


import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ReplacementService {
    List<Replacement> getReplacements(ReplacementType replacementType, Date date);

    Map<Date, List<Replacement>> getReplacements(ReplacementType replacementType, Date startDate, Date endDate);

    default List<Replacement> getLatestReplacements(ReplacementType replacementType) {
        return this.getReplacements(replacementType, new Date());
    }

    default List<Replacement> getLatestReplacements() {
        return this.getLatestReplacements(ReplacementType.TEACHERS);
    }
}
