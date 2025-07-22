package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Autore;
import it.uniroma3.siw.model.Immagine;
import it.uniroma3.siw.model.Libro;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.repository.ImmagineRepository;
import it.uniroma3.siw.repository.LibroRepository;
import it.uniroma3.siw.service.AutoreService;
import it.uniroma3.siw.service.LibroService;
import it.uniroma3.siw.service.RecensioneService;
import jakarta.transaction.Transactional;

@Controller 
public class LibroController {


    @Autowired
    private LibroService libroService;

    @Autowired
    private AutoreService autoreService;

    @Autowired
    private RecensioneService recensioneService;

    @Autowired
    private ImmagineRepository immagineRepository;

    @Autowired
    private LibroRepository libroRepository;




    @GetMapping("/libri")
    @Transactional
    public String getLibri(Model model) {
        List<Libro> libri = libroService.findAll();
        model.addAttribute("libri", libri);
        Set<Autore> autori= new HashSet<>();
        Map<Long,Double> mediaVotiMap = new HashMap<>();

        for(Libro l : libri){
            autori.addAll(l.getAutori());
            double media= libroService.calcolaMediaVoti(l.getId());
            mediaVotiMap.put(l.getId(), media);
        }

        model.addAttribute("autori", autori);
        model.addAttribute("libri", libri);
        model.addAttribute("mediaVotiMap", mediaVotiMap);
        return "libri";
    }


    @GetMapping("/libri/{id}")
    @Transactional
    public String dettaglioLibro(@PathVariable("id") Long id, Model model) {
        Optional<Libro> libroOpt = libroService.getLibroById(id);
        if (libroOpt.isPresent()) {
            Libro libro = libroOpt.get();
            Iterable<Recensione> recensioni = recensioneService.getRecensioniByLibro(libro);
            model.addAttribute("libro", libro);
            model.addAttribute("recensioni", recensioni);
            return "dettagliLibro";
        }
        return "dettagliLibro";
    }



    //admin


    @GetMapping("/admin/libro/nuovo")
    public String mostraFormLibro(Model model) {
        model.addAttribute("libro", new Libro());
        model.addAttribute("autori", autoreService.getAllAutori());
        return "admin/addLibro";
    }


    @PostMapping("/admin/libro/inserito")
    @Transactional
    public String salvaLibro(
            @ModelAttribute("libro") Libro libro,
            BindingResult bindingResult,
            @RequestParam(value = "autori", required = false) Set<Long> idAutori,
            @RequestParam(value = "copertinaFile", required = false) MultipartFile copertinaFile
    ) throws IOException {

       
        if (bindingResult.hasErrors()) {
            System.out.println("ERRORI DI BINDING:");
            bindingResult.getAllErrors().forEach(error -> {
                System.out.println(" - " + error.getObjectName() + ": " + error.getDefaultMessage());
            });
        } else {
            System.out.println("Nessun errore di binding.");
        }

        
        System.out.println("Dati del libro ricevuto:");
        System.out.println(" - Titolo: " + libro.getTitolo());
        System.out.println(" - Anno pubblicazione: " + libro.getAnnoPubblicazione());

        
        if (idAutori != null) {
            System.out.println("ID autori selezionati: " + idAutori);
        } else {
            System.out.println(" Nessun autore selezionato.");
        }

        
        if (copertinaFile != null && !copertinaFile.isEmpty()) {
            System.out.println(" File copertina ricevuto: " + copertinaFile.getOriginalFilename() + " (" + copertinaFile.getSize() + " bytes)");
        } else {
            System.out.println(" Nessun file copertina ricevuto.");
        }

       
        if (idAutori != null) {
            for (Long id : idAutori) {
                Autore autore = autoreService.getAutoreById(id).orElse(null);
                if (autore != null) {
                    libro.getAutori().add(autore);
                    if (autore.getLibri() == null) {
                        autore.setLibri(new ArrayList<>());
                    }
                    if (!autore.getLibri().contains(libro)) {
                        autore.getLibri().add(libro);
                    }
                }
            }
        }

        // ▶ Gestione immagine
        if (copertinaFile != null && !copertinaFile.isEmpty()) {
            Immagine immagine = new Immagine();
            immagine.setImageData(copertinaFile.getBytes());
            immagineRepository.save(immagine);
            libro.setCopertina(immagine);
        }

        // ▶ Salvataggio libro
        libroRepository.save(libro);

        System.out.println(" Libro salvato con successo (ID: " + libro.getId() + ")");
        return "redirect:/libri";
    }



    @GetMapping("/admin/libro/modifica/{id}")
    public String formModificaLibro(@PathVariable("id") Long id, Model model) {
        Libro libro = libroRepository.findById(id).orElse(null);
        model.addAttribute("libro", libro);
        model.addAttribute("autori", autoreService.getAllAutori());
        return "admin/modificaLibro";
    }

    @PostMapping("/admin/libro/modifica/{id}")
    public String updateLibro(
            @PathVariable Long id,
            @ModelAttribute Libro libro,
            @RequestParam(value = "autori", required = false) List<Long> autoreIds,
            @RequestParam(value = "copertinaFile", required = false) MultipartFile copertinaFile) throws IOException {


        libroService.updateLibro(libro ,autoreIds, copertinaFile);

        return "redirect:/libri";  // Reindirizza alla lista libri dopo l'aggiornamento
    }


    @Transactional
    @PostMapping("/admin/libro/elimina/{id}")
    public String deleteLibro(@PathVariable Long id) {
        Libro libro = libroRepository.findById(id).orElse(null);

        // Rompe i collegamenti con gli autori (ManyToMany)
        for (Autore autore : libro.getAutori()) {
            autore.getLibri().remove(libro);
        }
        libro.getAutori().clear();


        libro.setCopertina(null);

        // Recensioni: se orphanRemoval = true, bastano
        libro.getRecensioni().clear(); // oppure non serve se configurato correttamente

        libroRepository.delete(libro);

        return "redirect:/libri";
    }




}
