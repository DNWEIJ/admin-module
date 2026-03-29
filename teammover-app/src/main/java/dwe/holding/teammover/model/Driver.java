package dwe.holding.teammover.model;

import dwe.holding.admin.model.base.LocalAndMemberBaseBO;
import dwe.holding.teammover.model.type.DriveOptionEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Table(name = "TEAMMOVER_DRIVER")
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Driver extends LocalAndMemberBaseBO {
    String accountName;
    int nrOfTeamMembers;
    int nrOfEmptySpots;
    @Enumerated(EnumType.STRING)
    @NotNull
    DriveOptionEnum driveOption;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;
}