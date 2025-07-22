package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Libro;

public interface LibroRepository extends CrudRepository<Libro, Long> {

    // Additional query methods can be defined here if needed

    // Example: public Optional<Libro> findByTitle(String title);

    @Query("SELECT DISTINCT l FROM Libro l LEFT JOIN FETCH l.autori")
    List<Libro> findAllWithAutori();

}
