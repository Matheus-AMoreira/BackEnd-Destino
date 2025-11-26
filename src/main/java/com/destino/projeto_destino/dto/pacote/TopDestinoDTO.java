package com.destino.projeto_destino.dto.pacote;

public record TopDestinoDTO(
        Long id,          // ID da Cidade (usado como chave)
        String cidade,    // Nome da Cidade
        String estado,    // Sigla do Estado
        Long vendas,      // Quantidade de compras
        String imagem     // Uma imagem de exemplo (pega do pacote)
) {
}
