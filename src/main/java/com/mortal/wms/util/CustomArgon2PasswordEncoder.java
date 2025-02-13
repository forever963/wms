package com.mortal.wms.util;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

public class CustomArgon2PasswordEncoder extends Argon2PasswordEncoder {

    public CustomArgon2PasswordEncoder(int saltLength, int hashLength, int parallelism, int memory, int iterations) {
        super(saltLength, hashLength, parallelism, memory, iterations);
    }
}