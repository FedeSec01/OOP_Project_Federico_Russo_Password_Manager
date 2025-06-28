package com.securevault.composite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Classe Composite del Composite Pattern.
 * Rappresenta una cartella che può contenere altre cartelle o credenziali.
 * Gestisce una collezione di VaultComponent figli.
 */
public class FolderComposite implements VaultComponent {
    private final String name;                              // Nome della cartella
    private final List<VaultComponent> children = new ArrayList<>();  // Lista dei componenti figli

    /**
     * Costruttore della cartella.
     * @param name nome della cartella
     */
    public FolderComposite(String name) {
        this.name = name;
    }

    /**
     * Restituisce il nome della cartella.
     * @return nome della cartella
     */
    @Override
    public String getName() { 
        return name; 
    }

    /**
     * Override del metodo di default - aggiunge un figlio alla cartella.
     * Questa è l'operazione principale dei Composite.
     * @param component componente da aggiungere (cartella o credenziale)
     */
    @Override
    public void add(VaultComponent component) {
        children.add(component);
    }

    /**
     * Override del metodo di default - rimuove un figlio dalla cartella.
     * @param component componente da rimuovere
     */
    @Override
    public void remove(VaultComponent component) {
        children.remove(component);
    }

    /**
     * Restituisce iterator sui figli diretti (non ricorsivo).
     * Per attraversamento ricorsivo si usa CompositeIterator.
     * @return iterator sui figli diretti
     */
    @Override
    public Iterator<VaultComponent> iterator() {
        return children.iterator();
    }

    /**
     * Stampa la cartella e tutti i suoi figli con indentazione gerarchica.
     * Implementa la ricorsione tipica del Composite Pattern.
     * @param indent livello di indentazione corrente
     */
    @Override
    public void print(int indent) {
        // Stampa il nome della cartella con simbolo "+"
        System.out.printf("%s+ %s%n", " ".repeat(indent * 2), name);
        
        // Ricorsivamente stampa tutti i figli con indentazione incrementata
        for (VaultComponent child : children) {
            child.print(indent + 1);
        }
    }
}