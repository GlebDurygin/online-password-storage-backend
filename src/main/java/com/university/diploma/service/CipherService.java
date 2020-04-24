package com.university.diploma.service;

import com.university.diploma.dto.UserSignUpDto;
import com.university.diploma.form.SignUpForm;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope("prototype")
public class CipherService {

    protected BlockCipher aes256 = new AESEngine();

    protected AsymmetricBlockCipher rsa = new RSAEngine();

    protected static final String SERVER_MODULUS_STRING =
            "de869525d26eb7e9233ab280b53fac79ea978f49d067855b36767c5bb4947d925d24c0e145e72c7e" +
                    "0dace9b4060a3d77b855609393318f9210bdfccc89896856ea64f2739ee0c8902627dc8cda4e9860" +
                    "5f6d2433de7a015cc61eb00d88b2b46724ae1e267eb438ce167ec71f5a9add1d0530c768e816f55a" +
                    "05b28832d63eb225";
    protected static final String SERVER_PUBLIC_EXPONENT_STRING = "10001";

    protected static final String CLIENT_MODULUS_STRING =
            "8ee9e6679182136222c9ab8632307584a0e43d76accaa7e654a62981e659445e3f0f978dd9079bf3" +
                    "551d179cf822b42226c3b9c8cbe91ee3fb58a68b41a75e12a04b9e90cd08ab677ee8d8441053442e" +
                    "45a959ec5f56b3ee9554ae74de404f2daf79ff7599f710684b02c9c0e6b4938eb9293b0935500eed" +
                    "a8032a6b2ff79987";
    protected static final String CLIENT_PRIVATE_EXPONENT_STRING =
            "87f12e5dee279150944ec97e0957d731a5e9f61d611814c620573ebc75e50c07aa6ca67923acfd47" +
                    "65fb1a6f1209d0d77b904ab51a62522402febccd5252664453b0f56d2b38317e69ad09bbc551c708" +
                    "5f092333d701db061e7968d5eeeffcc28be294367c1d6a14986dab3406314bbc16dc393ebbce22c7" +
                    "0b69c6d1748d7221";

    public byte[] processBlockRSA(boolean forEncryption, byte[] text) {
        try {
            int blockLength;
            if (forEncryption) {
                blockLength = 127;
                BigInteger modulus = new BigInteger(SERVER_MODULUS_STRING, 16);
                BigInteger publicExponent = new BigInteger(SERVER_PUBLIC_EXPONENT_STRING, 16);
                RSAKeyParameters publicParameters = new RSAKeyParameters(false, modulus, publicExponent);
                rsa.init(true, publicParameters);

                List<byte[]> arrays = sliceArray(text, blockLength);
                byte[] result = new byte[(blockLength + 1) * arrays.size()];
                for (int i = 0; i < arrays.size(); i++) {
                    byte[] cipherText = rsa.processBlock(arrays.get(i), 0, arrays.get(i).length);
                    System.arraycopy(cipherText, 0, result, i * (blockLength + 1), cipherText.length);
                }

                return result;
            } else {
                blockLength = 128;
                BigInteger modulus = new BigInteger(CLIENT_MODULUS_STRING, 16);
                BigInteger privateExponent = new BigInteger(CLIENT_PRIVATE_EXPONENT_STRING, 16);
                RSAKeyParameters privateParameters = new RSAKeyParameters(true, modulus, privateExponent);
                rsa.init(false, privateParameters);

                List<byte[]> arrays = sliceArray(text, blockLength);
                List<byte[]> resultArrays = new ArrayList<>(arrays.size());
                for (byte[] bytes : arrays) {
                    byte[] cipherText = rsa.processBlock(bytes, 0, bytes.length);
                    resultArrays.add(cipherText);
                }

                long resultSize = resultArrays.stream()
                        .mapToInt(array -> array.length)
                        .sum();
                byte[] result = new byte[(int) resultSize];

                int index = 0;
                for (byte[] array : resultArrays) {
                    System.arraycopy(array, 0, result, index, array.length);
                    index += array.length;
                }

                return result;
            }
        } catch (InvalidCipherTextException e) {
            return null;
        }
    }

    public byte[] processBlockAES256(boolean forEncryption, String keyString, byte[] text) {
        byte[] key = new BigInteger(keyString, 16).toByteArray();
        if (forEncryption && Math.floorMod(text.length, 16) != 0) {
            text = addPadding(text, 16);
        }

        aes256.init(forEncryption, new KeyParameter(key));

        byte[] out = new byte[text.length];
        int index = 0;
        while (index < text.length) {
            aes256.processBlock(text, index, out, index);
            index += 16;
        }

        if (!forEncryption) {
            out = removePadding(out);
        }
        return out;
    }

    public UserSignUpDto decryptSignUpFormRSA(SignUpForm form) {
        String username = new String(processBlockRSA(false, form.getUsername()));
        String salt = new String(processBlockRSA(false, form.getSalt()));
        String verifier = new String(processBlockRSA(false, form.getVerifier()));
        return new UserSignUpDto(username, salt, verifier);
    }

    public Map<String, String> decryptBodyRSA(Map<String, byte[]> body) {
        Map<String, String> decryptedBody = new HashMap<>();
        for (Map.Entry<String, byte[]> entry : body.entrySet()) {
            String value = new String(processBlockRSA(false, entry.getValue()));
            decryptedBody.put(entry.getKey(), value);
        }

        return decryptedBody;
    }

    // ISO/IEC 7816-4
    protected byte[] addPadding(byte[] text, int maxPadding) {
        int length = text.length;

        int padding = maxPadding - Math.floorMod(length, maxPadding);
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

    protected List<byte[]> sliceArray(byte[] array, int blockLength) {
        int count = Math.floorDiv(array.length, blockLength);
        if (Math.floorMod(array.length, blockLength) != 0) {
            count++;
        }

        List<byte[]> arrays = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int arrayLength = blockLength;
            if (i == count - 1 && Math.floorMod(array.length, blockLength) != 0) {
                arrayLength = Math.floorMod(array.length, blockLength);
            }

            arrays.add(Arrays.copyOfRange(array, i * blockLength, i * blockLength + arrayLength));
        }

        return arrays;
    }
}
