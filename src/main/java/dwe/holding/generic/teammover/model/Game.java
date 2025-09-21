package dwe.holding.generic.teammover.model;

import dwe.holding.generic.admin.model.base.MemberBaseBO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;


@Table(name = "TEAMMOVER_GAME")
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Game extends MemberBaseBO {
    @NotEmpty
    String whereIsTheGame;
    @NotNull
    LocalDateTime whenIsTheGame;
    boolean doWeNeedToDrive;
    int howManyPeople;
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Driver> drivers;

}