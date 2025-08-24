// ============= ApplicationProperties.java =============
package com.expenseguard.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public class ApplicationProperties {

    private Cors cors = new Cors();
    private Notifications notifications = new Notifications();
    private Features features = new Features();
    private Security security = new Security();

    // Getters and setters
    public Cors getCors() { return cors; }
    public void setCors(Cors cors) { this.cors = cors; }
    
    public Notifications getNotifications() { return notifications; }
    public void setNotifications(Notifications notifications) { this.notifications = notifications; }
    
    public Features getFeatures() { return features; }
    public void setFeatures(Features features) { this.features = features; }
    
    public Security getSecurity() { return security; }
    public void setSecurity(Security security) { this.security = security; }

    public static class Cors {
        private String allowedOrigins = "http://localhost:3000";
        
        public String getAllowedOrigins() { return allowedOrigins; }
        public void setAllowedOrigins(String allowedOrigins) { this.allowedOrigins = allowedOrigins; }
    }

    public static class Notifications {
        private Email email = new Email();
        private Push push = new Push();
        
        public Email getEmail() { return email; }
        public void setEmail(Email email) { this.email = email; }
        
        public Push getPush() { return push; }
        public void setPush(Push push) { this.push = push; }

        public static class Email {
            private boolean enabled = true;
            private String from = "noreply@expenseguard.com";
            private Smtp smtp = new Smtp();
            
            public boolean isEnabled() { return enabled; }
            public void setEnabled(boolean enabled) { this.enabled = enabled; }
            
            public String getFrom() { return from; }
            public void setFrom(String from) { this.from = from; }
            
            public Smtp getSmtp() { return smtp; }
            public void setSmtp(Smtp smtp) { this.smtp = smtp; }

            public static class Smtp {
                private String host;
                private int port = 587;
                private String username;
                private String password;
                
                public String getHost() { return host; }
                public void setHost(String host) { this.host = host; }
                
                public int getPort() { return port; }
                public void setPort(int port) { this.port = port; }
                
                public String getUsername() { return username; }
                public void setUsername(String username) { this.username = username; }
                
                public String getPassword() { return password; }
                public void setPassword(String password) { this.password = password; }
            }
        }

        public static class Push {
            private boolean enabled = true;
            private String firebaseKey;
            
            public boolean isEnabled() { return enabled; }
            public void setEnabled(boolean enabled) { this.enabled = enabled; }
            
            public String getFirebaseKey() { return firebaseKey; }
            public void setFirebaseKey(String firebaseKey) { this.firebaseKey = firebaseKey; }
        }
    }

    public static class Features {
        private boolean bankIntegration = false;
        private boolean aiCategorization = true;
        
        public boolean isBankIntegration() { return bankIntegration; }
        public void setBankIntegration(boolean bankIntegration) { this.bankIntegration = bankIntegration; }
        
        public boolean isAiCategorization() { return aiCategorization; }
        public void setAiCategorization(boolean aiCategorization) { this.aiCategorization = aiCategorization; }
    }

    public static class Security {
        private Jwt jwt = new Jwt();
        
        public Jwt getJwt() { return jwt; }
        public void setJwt(Jwt jwt) { this.jwt = jwt; }

        public static class Jwt {
            private String secret = "expense-guard-secret-key-change-in-production";
            private long expiration = 86400000; // 24 hours
            
            public String getSecret() { return secret; }
            public void setSecret(String secret) { this.secret = secret; }
            
            public long getExpiration() { return expiration; }
            public void setExpiration(long expiration) { this.expiration = expiration; }
        }
    }
}