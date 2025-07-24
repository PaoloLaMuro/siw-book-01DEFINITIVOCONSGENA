package it.uniroma3.siw.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
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
import it.uniroma3.siw.repository.RecensioneRepository;
import jakarta.transaction.Transactional;

@Service
public class LibroService {
    
    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private ImmagineRepository immagineRepository;

    @Autowired
    private AutoreRepository autoreRepository;

    @Autowired
    private RecensioneRepository recensioneRepository;

    public Iterable<Libro> getAllLibri() {
        return libroRepository.findAll();
    }

    @Transactional
    public Optional<Libro> getLibroById(Long id) {
        return libroRepository.findById(id);
    }

    public Libro salvaLibro(Libro libro) {
        return libroRepository.save(libro);
    }

    public void eliminaLibro(Libro libro) {
        libroRepository.delete(libro);
    }

    @Transactional
    public Libro findById(Long id) {
        Optional<Libro> libro = libroRepository.findById(id);
        return libro.orElse(null);
    }


    @Transactional
    public List<Libro> findAll() {
        return libroRepository.findAllWithAutori();
    }


    public Double calcolaMediaVoti(Long libroId){
        Double media = recensioneRepository.findMediaVotiByLibroId(libroId);
        return (media != null) ? media : 0.0;
    }


    @Transactional
    public void updateLibro(Libro libroDaAggiornare, List<Long> autoreIds, MultipartFile nuovaImmagine) throws IOException {
        Libro libro = libroRepository.findById(libroDaAggiornare.getId())
                .orElseThrow(() -> new IllegalArgumentException("Libro non trovato con ID: " + libroDaAggiornare.getId()));

        libro.setTitolo(libroDaAggiornare.getTitolo());
        libro.setAnnoPubblicazione(libroDaAggiornare.getAnnoPubblicazione());

        if (autoreIds != null) {
            Iterable<Autore> autoriIterable = autoreRepository.findAllById(autoreIds);
            Set<Autore> nuoviAutori = new HashSet<>();
            for (Autore autore : autoriIterable) {
                nuoviAutori.add(autore);
            }
            libro.setAutori(nuoviAutori);
        }

        if (nuovaImmagine != null && !nuovaImmagine.isEmpty()) {
            Immagine nuovaCopertina = new Immagine();
            nuovaCopertina.setImageData(nuovaImmagine.getBytes());
            Immagine copertinaSalvata = immagineRepository.save(nuovaCopertina);
            libro.setCopertina(copertinaSalvata);
        }

        libroRepository.save(libro);
    }

    public List<Libro> ricercaLibri(String titolo) {
        return libroRepository.findByTitolo(titolo);
    }



    public List<Libro> ricercaLibriPerVotoMedio(Double votoMedio) {
        if(votoMedio == 0.0) {
            return libroRepository.findAllWithoutRecensioni();
        }
    return libroRepository.findByValutazione(votoMedio);
}

    

}
