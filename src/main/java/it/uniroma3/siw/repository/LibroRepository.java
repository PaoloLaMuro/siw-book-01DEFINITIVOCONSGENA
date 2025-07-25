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

    List<Libro> findByTitolo(String titolo);


    @Query("SELECT l FROM Libro l LEFT JOIN l.recensioni r GROUP BY l.id HAVING ROUND(AVG(r.voto),0) = :votoMedio")
    List<Libro> findByValutazione(Double votoMedio);

    
    //così faccio valere quelli senza recensione come voto 0
    @Query("SELECT l FROM Libro l LEFT JOIN l.recensioni r GROUP BY l.id HAVING COUNT(r) = 0")
    List<Libro> findAllWithoutRecensioni();




   

}
