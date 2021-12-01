package pl.sdacademy.projektplus.quiz.database.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@NoArgsConstructor
@Getter
@ToString
@Entity(name = "players")
public class PlayerEntity {

    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String name;

    public PlayerEntity(String name) {
        this.name = name;
    }
}