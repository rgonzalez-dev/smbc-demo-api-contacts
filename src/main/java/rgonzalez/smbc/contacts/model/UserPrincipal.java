package rgonzalez.smbc.contacts.model;

import java.io.Serializable;
import java.security.Principal;

public class UserPrincipal implements Serializable, Principal {

    private final Long id;
    private final Long networkId;
    private final String name;

    public UserPrincipal(Long id, Long networkId, String name) {
        this.id = id;
        this.networkId = networkId;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public Long getNetworkId() {
        return networkId;
    }

    @Override
    public String getName() {
        return name;
    }
}
