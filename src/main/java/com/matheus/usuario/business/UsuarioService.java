package com.matheus.usuario.business;

import com.matheus.usuario.business.converter.UsuarioConverter;
import com.matheus.usuario.business.dto.EnderecoDTO;
import com.matheus.usuario.business.dto.TelefoneDTO;
import com.matheus.usuario.business.dto.UsuarioDTO;
import com.matheus.usuario.infrastructure.entity.Endereco;
import com.matheus.usuario.infrastructure.entity.Telefone;
import com.matheus.usuario.infrastructure.entity.Usuario;
import com.matheus.usuario.infrastructure.exceptions.ConflictException;
import com.matheus.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.matheus.usuario.infrastructure.repository.EnderecoRepository;
import com.matheus.usuario.infrastructure.repository.TelefoneRepository;
import com.matheus.usuario.infrastructure.repository.UsuarioRepository;
import com.matheus.usuario.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EnderecoRepository enderecoRepository;
    private final TelefoneRepository telefoneRepository;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO){
        emailExists(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));
    }

    public void emailExists(String email){
        try {
            boolean existe = verificaEmailExistente(email);
            if (existe){
                throw new ConflictException("email ja cadastrado " + email);
            }
        } catch (ConflictException e) {
            throw new ConflictException("email ja cadastrado " , e.getCause());
        }
    }

    public boolean verificaEmailExistente(String email){
        return usuarioRepository.existsByEmail(email);
    }

    public UsuarioDTO buscarUsuarioPorEmail(String email){
        try {
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Email nao encontrado " + email)));
        }catch(ResourceNotFoundException e){
            throw new RuntimeException("email nao encontrado " + email);
        }
    }

    public void deletaPorEmail(String email){
        usuarioRepository.deleteByEmail(email);
    }

    public UsuarioDTO atualizaDadosUsuario(String token,UsuarioDTO usuarioDTO){
        String email = jwtUtil.extractUsername(token.substring(7));
        usuarioDTO.setSenha(usuarioDTO.getSenha() != null ? passwordEncoder.encode(usuarioDTO.getSenha()) : null );
        Usuario usuarioEntity = usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("email nao encontrado"));
        Usuario usuario = usuarioConverter.updateUsuario(usuarioDTO, usuarioEntity);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));

    }

    public EnderecoDTO atualizaEndereco(Long idEndereco, EnderecoDTO enderecoDTO){
        Endereco entity = enderecoRepository.findById(idEndereco).orElseThrow(() ->
                new ResourceNotFoundException("id nao encontrado " +idEndereco));
        Endereco endereco = usuarioConverter.updateEndereco(enderecoDTO,entity);

        return usuarioConverter.paraEnderecoDTO(enderecoRepository.save(endereco));
    }

    public TelefoneDTO atualizaTelefone(Long idTelefone, TelefoneDTO telefoneDTO){
        Telefone entity = telefoneRepository.findById(idTelefone).orElseThrow(() ->
                new ResourceNotFoundException("id nao encontrado " + idTelefone));
        Telefone telefone = usuarioConverter.updateTelefone(telefoneDTO,entity);
        return usuarioConverter.paraTelefoneDTO(telefoneRepository.save(telefone));
    }

}
