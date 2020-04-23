package com.university.diploma.service;

import com.university.diploma.dto.UserSignUpDto;
import com.university.diploma.form.SignUpForm;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
@Scope("prototype")
public class CipherService {

    protected BlockCipher cipher = new AESEngine();

    public byte[] processBlock(boolean forEncryption, String keyString, byte[] text) {
        byte[] key = new BigInteger(keyString, 16).toByteArray();
        if (forEncryption && Math.floorMod(text.length, 16) != 0) {
            text = addPadding(text);
        }

        cipher.init(forEncryption, new KeyParameter(key));

        byte[] out = new byte[text.length];
        int index = 0;
        while (index < text.length) {
            cipher.processBlock(text, index, out, index);
            index += 16;
        }

        if (!forEncryption) {
            out = removePadding(out);
        }
        return out;
    }

    public UserSignUpDto decryptSignUpForm(SignUpForm form, String key) {
        String username = new String(processBlock(false, key, form.getUsername()));
        String salt = new String(processBlock(false, key, form.getSalt()));
        String verifier = new String(processBlock(false, key, form.getVerifier()));
        return new UserSignUpDto(username, salt, verifier);
    }

    public Map<String, String> decryptBody(Map<String, byte[]> body, String key) {
        Map<String, String> decryptedBody = new HashMap<>();
        for (Map.Entry<String, byte[]> entry : body.entrySet()) {
            String value = new String(processBlock(false, key, entry.getValue()));
            decryptedBody.put(entry.getKey(), value);
        }

        return decryptedBody;
    }

    // ISO/IEC 7816-4
    protected byte[] addPadding(byte[] text) {
        int length = text.length;

        int padding = 16 - Math.floorMod(length, 16);
        byte[] paddingArray = new byte[padding];
        paddingArray[0] = Byte.parseByte("80");
        if (padding > 1) {
            Arrays.fill(paddingArray, 1, padding - 1, Byte.parseByte("00"));
        }

        byte[] result = Arrays.copyOf(text, length + padding);
        System.arraycopy(paddingArray, 0, result, length, padding);
        return result;
    }

    // ISO/IEC 7816-4
    protected byte[] removePadding(byte[] text) {
        int padding = 0;
        int index = text.length - 1;
        while (text[index] == Byte.parseByte("00")) {
            padding++;
            index--;
        }

        if (text[index] == Byte.parseByte("80")) {
            padding++;
        } else {
            padding = 0;
        }

        return Arrays.copyOfRange(text, 0, text.length - padding);
    }
}
