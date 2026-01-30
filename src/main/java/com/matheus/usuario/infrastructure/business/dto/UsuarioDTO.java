package com.matheus.usuario.infrastructure.business.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioDTO {

    private String nome;
    private String senha;
    private String email;
    private List<EnderecoDTO> enderecos;
    private List<TelefoneDTO> telefones;
}
