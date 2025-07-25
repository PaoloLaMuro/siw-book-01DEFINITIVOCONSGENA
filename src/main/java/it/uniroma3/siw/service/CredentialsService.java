package it.uniroma3.siw.service;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.repository.CredentialsRepository;
import jakarta.transaction.Transactional;

@Service
public class CredentialsService {

    @Autowired
    protected PasswordEncoder passwordEncoder;
    
    @Autowired
    protected CredentialsRepository credentialsRepository;


    @Transactional
    public Credentials getCredentials(Long id) {
        Optional<Credentials> result = this.credentialsRepository.findById(id);
        return result.orElse(null);
    }

    @Transactional
    public Credentials getCredentials(String username) {
        Optional<Credentials> result = this.credentialsRepository.findByUsername(username);
        return result.orElse(null);
    }

    @Transactional
    public Credentials saveCredentials(Credentials credentials) {
        credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
        credentials.setRole(Credentials.DEFAULT_ROLE);
        return this.credentialsRepository.save(credentials);
    }



}
