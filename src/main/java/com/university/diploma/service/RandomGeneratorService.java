package com.university.diploma.service;

import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.prng.DigestRandomGenerator;
import org.bouncycastle.crypto.prng.RandomGenerator;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Random generator on SHA-384 digest
 */
@Component
@Scope("singleton")
public class RandomGeneratorService {

    private RandomGenerator randomGenerator = new DigestRandomGenerator(new SHA384Digest());

    public void nextBytes(byte[] bytes) {
        randomGenerator.nextBytes(bytes);
    }
}
