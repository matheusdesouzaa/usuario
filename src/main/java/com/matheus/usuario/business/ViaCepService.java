package com.matheus.usuario.business;

import com.matheus.usuario.infrastructure.clients.ViaCepClient;
import com.matheus.usuario.infrastructure.clients.ViaCepDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ViaCepService {

    private final ViaCepClient viaCepClient;

    public ViaCepDTO buscarDadosEndereco(String cep){
        return viaCepClient.buscaDadosEndereco(processarCep(cep));
    }

    private String processarCep(String cep){
        String cepFormatado = cep.replace(" ","").
                replace("-","");

        if (!cepFormatado.matches("\\d+") || !Objects.equals(cepFormatado.length(), 8)){
            throw new IllegalArgumentException("O cep contém caracteres inválidos,favor verificar");
        }
        return cepFormatado;
    }
}
