package com.securevault.composite;

import java.util.Iterator;

/**
 * Interfaccia Component del Composite Pattern.
 * Definisce l'interfaccia comune per tutti gli elementi dell'albero (cartelle e credenziali).
 * Estende Iterable per supportare l'Iterator Pattern.
 */
public interface VaultComponent extends Iterable<VaultComponent> {
    
    /**
     * Operazione comune a tutti i componenti dell'albero.
     * @return nome del componente
     */
    String getName();
    
    /**
     * Stampa il componente con indentazione per visualizzazione gerarchica.
     * @param indent livello di indentazione
     */
    void print(int indent);

    /**
     * Operazione di default per aggiunta figli - appropriata solo per Composite.
     * Le foglie (Credential) lanciano UnsupportedOperationException.
     * @param component componente da aggiungere
     */
    default void add(VaultComponent component) {
        throw new UnsupportedOperationException("Non è un Composite");
    }

    /**
     * Operazione di default per rimozione figli - appropriata solo per Composite.
     * Le foglie (Credential) lanciano UnsupportedOperationException.
     * @param component componente da rimuovere
     */
    default void remove(VaultComponent component) {
        throw new UnsupportedOperationException("Non è un Composite");
    }

    /**
     * Iterator di default - appropriato solo per Composite.
     * Le foglie (Credential) devono override per restituire se stesse.
     * @return iterator sui figli
     */
    default Iterator<VaultComponent> iterator() {
        throw new UnsupportedOperationException("Non è un Composite");
    }
}