package com.srbruninho.contasapagar.domain.repositories;

import com.srbruninho.contasapagar.domain.model.Conta;
import com.srbruninho.contasapagar.infraestructure.projection.PeriodProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

    Page<Conta> findByDataVencimentoBetweenAndDescricaoAndDataPagamentoIsNull(LocalDate startDate, LocalDate endDate, String description, Pageable pageable);

    @Query(value = "SELECT SUM(valor) AS totalValue,to_char(data_pagamento,'MM/yyyy') AS period FROM conta\n" +
            "WHERE data_pagamento IS NOT NULL\n" +
            "GROUP BY to_char(data_pagamento,'MM/yyyy')" ,nativeQuery = true)
    Page<PeriodProjection> getTotalValuePaidPerPeriod(LocalDate startDate, LocalDate endDate, Pageable pageable);
}
