package com.base.infra.security;

import java.util.Date;

public record TokenInfo(
        String username,
        String userId,
        String role,
        String issuer,
        String audience,
        Date issuedAt,
        Date expiresAt
) {
    public static TokenInfoBuilder builder() {
        return new TokenInfoBuilder();
    }

    public static class TokenInfoBuilder {
        private String username;
        private String userId;
        private String role;
        private String issuer;
        private String audience;
        private Date issuedAt;
        private Date expiresAt;

        public TokenInfoBuilder username(String username) {
            this.username = username;
            return this;
        }

        public TokenInfoBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public TokenInfoBuilder role(String role) {
            this.role = role;
            return this;
        }

        public TokenInfoBuilder issuer(String issuer) {
            this.issuer = issuer;
            return this;
        }

        public TokenInfoBuilder audience(String audience) {
            this.audience = audience;
            return this;
        }

        public TokenInfoBuilder issuedAt(Date issuedAt) {
            this.issuedAt = issuedAt;
            return this;
        }

        public TokenInfoBuilder expiresAt(Date expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public TokenInfo build() {
            return new TokenInfo(username, userId, role, issuer, audience, issuedAt, expiresAt);
        }
    }

    public boolean isExpired() {
        return expiresAt != null && expiresAt.before(new Date());
    }

    public long getExpiresInSeconds() {
        if (expiresAt == null) return 0;
        return (expiresAt.getTime() - System.currentTimeMillis()) / 1000;
    }
}
