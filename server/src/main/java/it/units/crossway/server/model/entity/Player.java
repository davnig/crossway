package it.units.crossway.server.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "player")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String nickname;

    public Player(String nickname) {
        this.nickname = nickname;
    }
}
