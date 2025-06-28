package com.securevault.util;

import com.securevault.exception.ExceptionHandler;
import com.securevault.exception.VaultException;
import com.securevault.security.AesStrategy;
import com.securevault.security.EncryptionStrategy;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class che combina Reflection con Strategy Pattern.
 * Carica dinamicamente le strategie di crittografia dal file di configurazione.
 * Permette di cambiare algoritmo senza ricompilare il codice.
 */
public class ReflectionUtil {
    private static final String CONFIG = "config.properties";           // File di configurazione
    private static final String PROP_KEY = "encryption.strategy";       // Chiave per la strategia

    /**
     * Carica dinamicamente una EncryptionStrategy dal file config.properties.
     * 
     * Processo:
     * 1. Carica il file config.properties dal classpath
     * 2. Legge il nome della classe della strategia
     * 3. Usa reflection per istanziare la classe
     * 4. Fallback ad AesStrategy se configurazione mancante
     * 
     * @param vaultKey chiave di crittografia da passare alla strategia
     * @return istanza della strategia configurata
     * @throws VaultException se il caricamento fallisce
     */
    public static EncryptionStrategy loadStrategy(SecretKey vaultKey) {
        Properties props = new Properties();

        // 1. CARICAMENTO CONFIGURAZIONE
        try (InputStream in = Thread.currentThread()
                                    .getContextClassLoader()
                                    .getResourceAsStream(CONFIG)) {
            if (in != null) {
                props.load(in);
            }
            // Se file non esiste, props rimane vuoto e si usa il default
        } catch (IOException e) {
            throw new VaultException(ExceptionHandler.handleException(e), e);
        }

        // 2. LETTURA NOME CLASSE (con fallback)
        String className = props.getProperty(PROP_KEY, AesStrategy.class.getName());

        try {
            // 3. CARICAMENTO CLASSE CON REFLECTION
            Class<?> clazz = Class.forName(className);
            
            // 4. ISTANZIAZIONE CON COSTRUTTORE APPROPRIATO
            try {
                // Prima prova: costruttore con SecretKey (es. AesStrategy)
                return (EncryptionStrategy) clazz.getConstructor(SecretKey.class)
                                                 .newInstance(vaultKey);
            } catch (NoSuchMethodException nsme) {
                // Seconda prova: costruttore vuoto (es. DummyStrategy)
                return (EncryptionStrategy) clazz.getDeclaredConstructor().newInstance();
            }
        } catch (Exception e) {
            // 5. EXCEPTION SHIELDING
            throw new VaultException(ExceptionHandler.handleException(e), e);
        }
    }
}