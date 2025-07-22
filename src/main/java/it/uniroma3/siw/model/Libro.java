package it.uniroma3.siw.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;

@Entity
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    private String titolo;

    @Column(nullable = true)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate annoPubblicazione;



    @OneToOne(fetch = FetchType.EAGER)
    private Immagine copertina;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "libro_autore",
               joinColumns = @JoinColumn(name = "libro_id"),
               inverseJoinColumns = @JoinColumn(name = "autore_id")
               )
    private Set<Autore> autori = new HashSet<>();


    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL)
    private List<Recensione> recensioni;


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getTitolo() {
        return titolo;
    }


    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }


    public LocalDate getAnnoPubblicazione() {
        return annoPubblicazione;
    }


    public void setAnnoPubblicazione(LocalDate annoPubblicazione) {
        this.annoPubblicazione = annoPubblicazione;
    }


    public Immagine getCopertina() {
        return copertina;
    }


    public void setCopertina(Immagine copertina) {
        this.copertina = copertina;
    }


    public Set<Autore> getAutori() {
        return autori;
    }


    public void setAutori(Set<Autore> autori) {
        this.autori = autori;
    }


    public List<Recensione> getRecensioni() {
        return recensioni;
    }


    public void setRecensioni(List<Recensione> recensioni) {
        this.recensioni = recensioni;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((titolo == null) ? 0 : titolo.hashCode());
        result = prime * result + ((annoPubblicazione == null) ? 0 : annoPubblicazione.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Libro other = (Libro) obj;
        if (titolo == null) {
            if (other.titolo != null)
                return false;
        } else if (!titolo.equals(other.titolo))
            return false;
        if (annoPubblicazione == null) {
            if (other.annoPubblicazione != null)
                return false;
        } else if (!annoPubblicazione.equals(other.annoPubblicazione))
            return false;
        return true;
    }


    
    

}
