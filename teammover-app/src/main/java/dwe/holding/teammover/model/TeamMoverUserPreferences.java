package dwe.holding.teammover.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TeamMoverUserPreferences {
    int nrOfTeamMembers = 0;
    List<String> names = new ArrayList<>();
}