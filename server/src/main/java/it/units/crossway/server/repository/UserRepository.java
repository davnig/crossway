package it.units.crossway.server.repository;

import it.units.crossway.server.model.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Player, Integer> {
}
