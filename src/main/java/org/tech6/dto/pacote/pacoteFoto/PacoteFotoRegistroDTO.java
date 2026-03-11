package org.tech6.dto.pacote.pacoteFoto;

import java.util.List;

public record PacoteFotoRegistroDTO(
        String nome,
        String url,
        List<FotoDTO> fotosAdicionais
) {
}
