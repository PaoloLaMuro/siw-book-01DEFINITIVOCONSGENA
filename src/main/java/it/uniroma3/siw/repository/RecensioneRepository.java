package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Libro;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.User;

public interface RecensioneRepository extends CrudRepository<Recensione, Long> {

    // Additional query methods can be defined here if needed

    // Example: public Optional<Recensione> findByLibro(Libro libro);

    // Note: You may want to add specific query methods related to Recensione if necessary

    Iterable<Recensione> findByLibro(Libro libro);


    @Query("SELECT AVG(r.voto) FROM Recensione r WHERE r.libro.id = :libroId")
    Double findMediaVotiByLibroId(@Param("libroId") Long libroId);


    List<Recensione> findByAutore(User user);
    


}
