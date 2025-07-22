package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Autore;
import it.uniroma3.siw.model.Immagine;
import it.uniroma3.siw.model.Libro;
import it.uniroma3.siw.service.AutoreService;
import jakarta.transaction.Transactional;

@Controller
public class AutoreController {

    @Autowired
    AutoreService autoreService;

    @GetMapping("/autori")
    public String getAutori(Model model) {
        model.addAttribute("autori", autoreService.getAllAutori());
        return "autori";
    }








    // Mostra form di modifica autore
    @GetMapping("/admin/autore/modifica/{id}")
    public String formModificaAutore(@PathVariable Long id, Model model) {
        Autore autore = autoreService.getAutoreById(id)
                .orElseThrow(() -> new IllegalArgumentException("Autore non trovato"));
        model.addAttribute("autore", autore);
        return "admin/modificaAutore";
    }


    @GetMapping("/admin/autore/nuovo")
    public String AggiungiAutore(Model model) {
        model.addAttribute("autore", new Autore());
        return "admin/addAutore";
    }

    @PostMapping("/admin/autore/inserito")
    @Transactional
    public String SalvaAutore(@ModelAttribute("autore") Autore autore,
                              @RequestParam(value="fileImmagine", required = false) MultipartFile fileImmagine) throws IOException {

        if ( fileImmagine != null && !fileImmagine.isEmpty()) {
            Immagine immagine = new Immagine();
            immagine.setImageData(fileImmagine.getBytes());
            autore.setImmagineAutore(immagine);
        }
        autoreService.salvaAutore(autore);
        return "redirect:/autori";
    }

    @PostMapping("/admin/autore/modifica/{id}")
    public String salvaModificaAutore(@PathVariable Long id,
                                      @ModelAttribute Autore autoreModificato,
                                      @RequestParam(value="fileImmagine", required = false) MultipartFile immagine) throws IOException {
        autoreService.aggiornaAutore(id, autoreModificato, immagine);
        return "redirect:/autori";
    }


    // Rimuovi autore
    @PostMapping("/admin/autore/rimuovi/{id}")
    public String rimuoviAutore(@PathVariable Long id) {
        Optional<Autore> optAutore =autoreService.getAutoreById(id);
        if (optAutore.isPresent()) {
            Autore autore = optAutore.get();
            for (Libro libro: autore.getLibri()) {
                libro.getAutori().remove(autore);
            }
            autore.getLibri().clear();
            autoreService.salvaAutore(autore);
            autoreService.eliminaAutore(autore.getId());
        }
        return "redirect:/autori";
    }

    



}
