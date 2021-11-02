package it.units.crossway.server.repository;

import it.units.crossway.server.model.entity.Game;
import it.units.crossway.server.model.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Integer> {
    Optional<Player> findByNickname(String nickname);

    boolean existsByNickname(String nickname);

    void deleteByNickname(String nickname);
}
