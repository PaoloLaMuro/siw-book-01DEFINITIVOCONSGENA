package it.uniroma3.siw.service;

import java.io.IOException;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Autore;
import it.uniroma3.siw.model.Immagine;
import it.uniroma3.siw.model.Libro;
import it.uniroma3.siw.repository.AutoreRepository;
import it.uniroma3.siw.repository.ImmagineRepository;
import it.uniroma3.siw.repository.LibroRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AutoreService {

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private AutoreRepository autoreRepository;

    @Autowired
    private ImmagineRepository immagineRepository;


    public Iterable<Autore> getAllAutori() {
        return this.autoreRepository.findAll();
    }

    public Optional<Autore> getAutoreById(Long id) {
        return this.autoreRepository.findById(id);
    }

    public Autore salvaAutore(Autore autore) {
        return this.autoreRepository.save(autore);
    }

    //controllare se non è meglio passare direttamente l'autore o un altro campo
    public void eliminaAutore(Long id) {
        this.autoreRepository.deleteById(id);
    }

    public Set<Autore> getAutoriByLibroId(Long libroId) {
        Optional<Libro> libroOpt = this.libroRepository.findById(libroId);
        if(libroOpt.isPresent()) {
            return libroOpt.get().getAutori();
        }
        else
            return Collections.emptySet();  //aggiungere eccezione 
    }
    

     public Autore aggiornaAutore(Long id, Autore autoreAggiornato, MultipartFile immagine) throws IOException {
        Optional<Autore> autoreOpt = autoreRepository.findById(id);
        if (autoreOpt.isPresent()) {
            Autore autore = autoreOpt.get();
            autore.setNome(autoreAggiornato.getNome());
            autore.setCognome(autoreAggiornato.getCognome());
            autore.setDataMorte(autoreAggiornato.getDataMorte());
            autore.setDataNascita(autoreAggiornato.getDataNascita());
            autore.setNazionalita(autoreAggiornato.getNazionalita());

            if(immagine != null && !immagine.isEmpty()) {
                Immagine nuovaImmagine = new Immagine();
                nuovaImmagine.setImageData(immagine.getBytes());
                Immagine immagineSalvata =  immagineRepository.save(nuovaImmagine);
                autore.setImmagineAutore(immagineSalvata);
            }
            return autoreRepository.save(autore);
        }else {
            throw new EntityNotFoundException("Autore con id" + id + " non trovato");
        }
   

    }
}