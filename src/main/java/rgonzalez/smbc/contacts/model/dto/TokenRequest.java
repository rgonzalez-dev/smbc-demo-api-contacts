package rgonzalez.smbc.contacts.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request DTO for obtaining a JWT token.
 */
public class TokenRequest {

    @JsonProperty("networkId")
    private Long networkId;

    @JsonProperty("userId")
    private String userId;

    public TokenRequest() {
    }

    public TokenRequest(Long networkId) {
        this.networkId = networkId;
    }

    public TokenRequest(Long networkId, String userId) {
        this.networkId = networkId;
        this.userId = userId;
    }

    public Long getNetworkId() {
        return networkId;
    }

    public void setNetworkId(Long networkId) {
        this.networkId = networkId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
