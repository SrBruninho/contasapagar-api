package com.srbruninho.contasapagar.infraestructure.projection;

import java.math.BigDecimal;

public interface PeriodProjection {
    BigDecimal getTotalValue();
    String getPeriod();
}
