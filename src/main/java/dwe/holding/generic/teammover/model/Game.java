package dwe.holding.generic.teammover.model;

import dwe.holding.generic.admin.model.base.TenantBaseBO;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Game extends TenantBaseBO {
    // https://waterpolo.knzb.nl/waterpolo/verenigingen/poule/11200/8507/DHS138S/
    @NotEmpty
    String whereIsTheGame;
    @NotNull
    LocalDateTime whenIsTheGame;
    String nameOfTheTeam;
    boolean doWeNeedToDrive;
    int howManyPeople;
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Driver> drivers;
}