package com.srbruninho.contasapagar.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "A data de vencimento não pode ser nula")
    private LocalDate dataVencimento;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataPagamento;

    @NotNull(message = "O valor não pode ser nulo")
    @Positive(message = "O valor deve ser maior que zero")
    private BigDecimal valor;

    @NotNull(message = "A descrição não pode ser nula")
    private String descricao;

    @Enumerated(EnumType.STRING)
    private Situacao situacao;

    public void defineSituacao() {
        if (this.dataPagamento != null) {
            this.situacao = Situacao.PAGA;
        } else {
            LocalDate today = LocalDate.now();
            if (this.dataVencimento.isEqual(today) || this.dataVencimento.isAfter(today)) {
                this.situacao = Situacao.PENDENTE;
            } else {
                this.situacao = Situacao.ATRASADA;
            }
        }
    }

    public void confirmPayment(){
        this.dataPagamento = LocalDate.now();
        this.situacao = Situacao.PAGA;
    }
}
