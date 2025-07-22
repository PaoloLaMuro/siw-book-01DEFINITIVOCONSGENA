package it.uniroma3.siw.repository;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Immagine;

public interface ImmagineRepository extends CrudRepository<Immagine, Long> {

    // Additional query methods can be defined here if needed

    // Example: public Optional<Immagine> findByName(String name);

}
