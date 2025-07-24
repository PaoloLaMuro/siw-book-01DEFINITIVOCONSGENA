package it.uniroma3.siw.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Libro;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.RecensioneRepository;

@Service
public class RecensioneService {

    @Autowired
    private RecensioneRepository recensioneRepository;

    public Iterable<Recensione> getRecensioniByLibro(Libro libro) {
        return this.recensioneRepository.findByLibro(libro);
    }

    public Recensione salvaRecensione(Recensione recensione) {
        return this.recensioneRepository.save(recensione);
    }
    
    public void eliminaRecensione(Long id) {
        this.recensioneRepository.deleteById(id);
    }

    public Optional<Recensione> getRecensioneById(Long id) {
        return this.recensioneRepository.findById(id);
    }

    public List<Recensione> getRecensioniByAutore(User user) {
        List<Recensione> recensioni = this.recensioneRepository.findByAutore(user);
        return recensioni;
    }

    public Recensione findById(Long recensioneId) {
        return this.recensioneRepository.findById(recensioneId).orElse(null);
    }

}
