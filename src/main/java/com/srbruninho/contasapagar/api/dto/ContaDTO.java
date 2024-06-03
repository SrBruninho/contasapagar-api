package com.srbruninho.contasapagar.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.srbruninho.contasapagar.domain.model.Situacao;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ContaDTO {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataVencimento;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataPagamento;

    private BigDecimal valor;

    private String descricao;

    private Situacao situacao;
}
