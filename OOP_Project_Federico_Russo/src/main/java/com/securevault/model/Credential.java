package com.securevault.model;

import com.securevault.composite.VaultComponent;
import com.securevault.factory.CredentialInterface;

import java.util.Iterator;
import java.util.Objects;

public class Credential implements VaultComponent, CredentialInterface {
    private final String service;
    private final String username;
    private final String password;

    public Credential(String service, String username, String password) {
        this.service = service;
        this.username = username;
        this.password = password;
    }

    public String getService() {
        return service;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getName() {
        return service + " - " + username;
    }

    @Override
    public void print(int indent) {
        String spaces = " ".repeat(indent);
        System.out.println(spaces + getName());
    }

    @Override
    public Iterator<VaultComponent> iterator() {
        return new Iterator<>() {
            private boolean hasNext = true;

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public VaultComponent next() {
                hasNext = false;
                return Credential.this;
            }
        };
    }

    public String serialize() {
        return service + "," + username + "," + password;
    }

    public static Credential deserialize(String line) {
        String[] parts = line.split(",", 3);
        if (parts.length != 3) throw new IllegalArgumentException("Formato non valido");
        return new Credential(parts[0], parts[1], parts[2]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Credential that)) return false;
        return Objects.equals(service, that.service) &&
               Objects.equals(username, that.username) &&
               Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(service, username, password);
    }
}