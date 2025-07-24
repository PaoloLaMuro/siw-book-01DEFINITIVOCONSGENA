
package it.uniroma3.siw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Libro;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.LibroService;
import it.uniroma3.siw.service.RecensioneService;
import it.uniroma3.siw.service.UserService;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequestMapping("/recensioni")
public class RecensioneController {


    @Autowired
    private  LibroService libroService;

    @Autowired
    private  RecensioneService recensioneService;

    @Autowired
    private  UserService utenteService;

    @Autowired
    private CredentialsService credentialsService;


    @GetMapping("/nuova/{id}")
    public String formInserisciRecensione(@PathVariable("id") Long libroId, Model model) {
        Recensione r=new Recensione();
        model.addAttribute("recensione", r);
        model.addAttribute("libro", this.libroService.findById(libroId));
        return "nuovaRecensione";

    }

    @PostMapping("/inserisci/{id}")
    public String inserisciRecensione(@PathVariable("id") Long libroId, Recensione recensione, Model model, RedirectAttributes redirectAttributes) {
        recensione.setId(null);
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
        Libro l = libroService.findById(libroId);
        
        // Controllo se l'utente ha già rilasciato una recensione per questo libro
        List<Recensione> recensioniUtente = recensioneService.getRecensioniByAutore(credentials.getUser());
        boolean esisteRecensione = recensioniUtente.stream()
            .anyMatch(r -> r.getLibro().getId().equals(libroId));
        
        if (esisteRecensione) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hai già rilasciato una recensione per questo libro.");
            return "redirect:/libri/" + libroId;
        }
        
        recensione.setLibro(l);
        recensione.setAutore(credentials.getUser());
        recensioneService.salvaRecensione(recensione);
        redirectAttributes.addFlashAttribute("successMessage", "Recensione aggiunta con successo!");
        return "redirect:/libri/" + libroId;
    }


   
    


    @PostMapping("/admin/recensione/elimina/{id}")
    public String eliminaRecensione(@PathVariable("id") Long recensioneId, RedirectAttributes redirectAttributes) {
        Recensione recensione = recensioneService.findById(recensioneId);
        if (recensione != null) {
            Long libroId = recensione.getLibro().getId();
            recensioneService.eliminaRecensione(recensioneId);
            redirectAttributes.addFlashAttribute("successMessage", "Recensione eliminata con successo!");
            return "redirect:/libri/" + libroId;
        }
        redirectAttributes.addFlashAttribute("errorMessage", "Recensione non trovata.");
        return "redirect:/libri";
    }
}
