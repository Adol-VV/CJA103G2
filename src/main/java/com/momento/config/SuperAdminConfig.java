package com.momento.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "app.super-admin")
public class SuperAdminConfig {

    private List<String> accounts;

    public List<String> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<String> accounts){
        this.accounts = accounts;
    }


    public boolean isSuperAdmin(String account){
        return accounts != null && accounts.contains(account);
    }
}
