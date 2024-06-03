package com.srbruninho.contasapagar.domain.repositories.projection;

import java.math.BigDecimal;

public interface TotalValuePaidPerPeriodProjection {
    BigDecimal getTotalValue();
    String getPeriod();
}
