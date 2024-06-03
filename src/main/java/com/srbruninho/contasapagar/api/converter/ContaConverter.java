package com.srbruninho.contasapagar.api.converter;

import com.srbruninho.contasapagar.api.dto.ContaDTO;
import com.srbruninho.contasapagar.domain.model.Conta;

public class ContaConverter {
    public static ContaDTO toDTO(Conta conta) {
        ContaDTO dto = new ContaDTO();
        dto.setId(conta.getId());
        dto.setDataVencimento(conta.getDataVencimento());
        dto.setDataPagamento(conta.getDataPagamento());
        dto.setValor(conta.getValor());
        dto.setDescricao(conta.getDescricao());
        dto.setSituacao(conta.getSituacao());
        return dto;
    }
}
