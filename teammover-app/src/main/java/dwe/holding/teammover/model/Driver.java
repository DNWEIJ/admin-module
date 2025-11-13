package dwe.holding.teammover.model;

import dwe.holding.admin.model.base.TenantBaseBO;
import dwe.holding.admin.model.type.DriveOptionEnum;
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
public class Driver extends TenantBaseBO {
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