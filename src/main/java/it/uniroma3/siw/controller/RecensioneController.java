package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String inserisciRecensione(@PathVariable("id")  Long libroId, Recensione recensione, Model model) {
        recensione.setId(null);
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
        Libro l= libroService.findById(libroId);


        recensione.setLibro(l);
        recensione.setAutore(credentials.getUser());
        recensione.setAutore(credentials.getUser());
        recensioneService.salvaRecensione(recensione);


        return "redirect:/libri/" + libroId;
    }

}
