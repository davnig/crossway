package it.units.crossway.server.repository;

import it.units.crossway.server.model.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {
    Optional<Game> findByUuid(String uuid);

    boolean existsByUuid(String uuid);

    void deleteByUuid(String uuid);
}
