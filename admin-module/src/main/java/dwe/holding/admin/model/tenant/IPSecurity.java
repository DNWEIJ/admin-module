package dwe.holding.admin.model.tenant;


import dwe.holding.admin.model.base.MemberBaseBO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.rmi.server.UID;


/**
 * Restricted IP Numbers. When no IP Numbers are available, then the user is granted access from all locations.
 */

@Table(name = "ADMIN_IPSECURITY"
)
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class IPSecurity extends MemberBaseBO {

    @Column(nullable = false)
    private UID mid;

    @Column(nullable = false)
    private   Long userId;

    @NotEmpty
    @Column(nullable = false)
    private String ipnumber;

    public Object getIpnumber() {
        return ipnumber;
    }
}