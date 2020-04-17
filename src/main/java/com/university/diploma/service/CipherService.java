package com.university.diploma.service;

import com.university.diploma.dto.UserSignUpDto;
import com.university.diploma.form.SignUpForm;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.RC4Engine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
@Scope("prototype")
public class CipherService {
    protected StreamCipher cipher = new RC4Engine();

    public byte[] processBytes(boolean forEncryption, byte[] key, byte[] text) {
        cipher.init(forEncryption, new KeyParameter(key));
        byte[] out = new byte[text.length];
        cipher.processBytes(text, 0, text.length, out, 0);
        return out;
    }

    public UserSignUpDto decryptSignUpForm(SignUpForm form, byte[] key) {
        String username = new String(processBytes(key, form.getUsername()));
        String salt = new String(processBytes(key, form.getSalt()));
        String verifier = new String(processBytes(key, form.getVerifier()));
        return new UserSignUpDto(username, salt, verifier);
    }

    public Map<String, String> decryptBody(Map<String, byte[]> body, byte[] key) {
        Map<String, String> decryptedBody = new HashMap<>();
        for (Map.Entry<String, byte[]> entry : body.entrySet()) {
            String value = new String(processBytes(key, entry.getValue()));
            decryptedBody.put(entry.getKey(), value);
        }

        return decryptedBody;
    }

    public byte[] processBytes(byte[] key, byte[] text) {
        if (key.length > 64) {
            key = Arrays.copyOfRange(key, 1, key.length);
        }
        if (key.length < 1 || key.length > 256) {
            throw new IllegalArgumentException(
                    "key must be between 1 and 256 bytes");
        } else {
            final byte[] S = new byte[256];
            final byte[] T = new byte[256];
            int keylen = key.length;
            byte x;
            for (int i = 0; i < 256; i++) {
                S[i] = (byte) i;
                T[i] = key[i % keylen];
            }
            int j = 0;
            for (int i = 0; i < 256; i++) {
                j = (j + S[i] + T[i]) & 0xFF;
                S[i] ^= S[j];
                S[j] ^= S[i];
                S[i] ^= S[j];
            }

            final byte[] ciphertext = new byte[text.length];
            int i = 0, k, t;
            j = 0;
            for (int counter = 0; counter < text.length; counter++) {
                i = (i + 1) & 0xFF;
                j = (j + S[i]) & 0xFF;
                S[i] ^= S[j];
                S[j] ^= S[i];
                S[i] ^= S[j];
                t = (S[i] + S[j]) & 0xFF;
                k = S[t];
                ciphertext[counter] = (byte) (text[counter] ^ k);
            }
            return ciphertext;
        }
    }
}
