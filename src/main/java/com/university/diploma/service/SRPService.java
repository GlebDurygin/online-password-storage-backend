package com.university.diploma.service;

import com.university.diploma.dto.UserSignInClientDto;
import com.university.diploma.dto.UserSignInDBDto;
import com.university.diploma.dto.UserSignInServerDto;
import com.university.diploma.dto.UserSignUpDto;
import com.university.diploma.form.SignInForm;
import com.university.diploma.form.SignUpForm;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Scope(value = "prototype")
@Service
public class SRPService {

    protected static final byte[] G_HEX = Hex.decode("02");
    protected static final byte[] N_HEX = Hex.decode("AC6BDB41324A9A9BF166DE5E1389582F" +
            "AF72B6651987EE07FC3192943DB56050" +
            "A37329CBB4A099ED8193E0757767A13D" +
            "D52312AB4B03310DCD7F48A9DA04FD50" +
            "E8083969EDB767B0CF6095179A163AB3" +
            "661A05FBD5FAAAE82918A9962F0B93B8" +
            "55F97993EC975EEAA80D740ADBF4FF74" +
            "7359D041D5C33EA71D281E446B14773B" +
            "CA97B43A23FB801676BD207A436C6481" +
            "F1D2B9078717461A5B9D32E688F87748" +
            "544523B524B0D57D5EA77A2775D2ECFA" +
            "032CFBDBF52FB3786160279004E57AE6" +
            "AF874E7303CE53299CCC041C7BC308D8" +
            "2A5698F3A8D0C38271AE35F8E9DBFBB6" +
            "94B5C803D89F7AE435DE236D525F5475" +
            "9B65E372FCD68EF20FA7111F9E4AFF73");

    protected static final BigInteger G = new BigInteger(1, G_HEX); // group generator
    protected static final BigInteger N = new BigInteger(1, N_HEX); // simple number

    protected BigInteger K; // multiplier parameter
    protected BigInteger emphaticKeyA;
    protected BigInteger emphaticKeyB;
    protected byte[] randomA;
    protected byte[] randomB;

    protected static final int SALT_LENGTH = 16;

    protected Digest digest; // hash function

    protected MessageDigest msgDigest;
    protected SecureRandom secureRandom;


    public SRPService() {
        // init Digest
        //digest = new SHA256Digest();
        //digest.update(NHex, 0, NHex.length);

        secureRandom = new SecureRandom();

        try {
            msgDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ignored) {
        }

        if (msgDigest != null) {
            msgDigest.update(N_HEX);

            byte[] paddedG = getLeftZeroPadded(G_HEX, N_HEX.length);
            byte[] kHex = msgDigest.digest(paddedG);

            K = new BigInteger(kHex);
        }
    }

    public UserSignUpDto signUp(SignUpForm form) {
        byte[] salt = secureRandom.generateSeed(SALT_LENGTH);

        String userNameWithPassword = form.getUsername() + " : " + form.getPassword();
        byte[] userNameWithPasswordDigest = msgDigest.digest(userNameWithPassword.getBytes());

        msgDigest.update(salt);
        msgDigest.update(userNameWithPasswordDigest);
        BigInteger x = new BigInteger(msgDigest.digest());

        BigInteger v = G.modPow(x, N); // verifier

        return new UserSignUpDto(form.getUsername(), new String(salt), v.toString(), form.getKeyword());
    }

    public UserSignInClientDto computeUsernameAndEmphaticKeyOnClient(String username) {
        randomA = new byte[32];
        secureRandom.nextBytes(randomA);
        emphaticKeyA = G.modPow(new BigInteger(randomA), N);

        return new UserSignInClientDto(username, emphaticKeyA.toString());
    }

    public UserSignInServerDto computeSaltAndEmphaticKeyOnServer(UserSignInDBDto dbDto) {
        randomB = new byte[32];
        secureRandom.nextBytes(randomB);
        BigInteger V = new BigInteger(dbDto.getVerifier());
        emphaticKeyB = K.multiply(V)
                .add(G.modPow(new BigInteger(randomB), N))
                .mod(N);

        return new UserSignInServerDto(dbDto.getSalt(), emphaticKeyB.toString());
    }

    public String computeClientSessionKey(UserSignInServerDto serverDto, SignInForm form) {
        msgDigest.update(emphaticKeyA.toString().getBytes());
        msgDigest.update(serverDto.getEmphaticKey().getBytes());
        BigInteger maskValue = new BigInteger(msgDigest.digest()); // u

        String userNameWithPassword = form.getUsername() + " : " + form.getPassword();
        byte[] userNameWithPasswordDigest = msgDigest.digest(userNameWithPassword.getBytes());

        msgDigest.update(serverDto.getSalt().getBytes());
        msgDigest.update(userNameWithPasswordDigest);
        BigInteger privateKey = new BigInteger(msgDigest.digest()); // x

        BigInteger supportPow = maskValue.multiply(privateKey)
                .add(new BigInteger(randomA)); // a + u*x
        BigInteger supportNumber = G.modPow(privateKey, N).multiply(K); // k*(g^x % N)
        BigInteger sessionKey = emphaticKeyB.subtract(supportNumber)
                .modPow(supportPow, N);

        return new String(msgDigest.digest(sessionKey.toString().getBytes()));
    }

    public String computeClientCheckValue(String username, String salt) {
        msgDigest.update(N.toString().getBytes());
        BigInteger n = new BigInteger(msgDigest.digest());

        msgDigest.update(G.toString().getBytes());
        BigInteger g = new BigInteger(msgDigest.digest());

        msgDigest.update(username.getBytes());
        BigInteger i = new BigInteger(msgDigest.digest());

        msgDigest.update(n.xor(g).toString().getBytes());
        msgDigest.update(i.toString().getBytes());
        msgDigest.update(salt.getBytes());
        msgDigest.update(emphaticKeyA.toString().getBytes());
        msgDigest.update(emphaticKeyB.toString().getBytes());
        msgDigest.update(K.toString().getBytes());

        return new String(msgDigest.digest());
    }

    public String computeServerSessionKey(UserSignInClientDto clientDto, UserSignInDBDto dbDto) {
        msgDigest.update(clientDto.getEmphaticKey().getBytes());
        msgDigest.update(emphaticKeyB.toString().getBytes());
        BigInteger maskValue = new BigInteger(msgDigest.digest()); // u

        BigInteger verifier = new BigInteger(dbDto.getVerifier());
        BigInteger supportPow = new BigInteger(randomB); // b
        BigInteger supportNumber = verifier.modPow(maskValue, N); // v ^ u % N
        BigInteger sessionKey = emphaticKeyA.multiply(supportNumber)
                .modPow(supportPow, N);

        return new String(msgDigest.digest(sessionKey.toString().getBytes()));
    }

    public String computeServerCheckValue(String clientCheckValue) {
        msgDigest.update(emphaticKeyA.toString().getBytes());
        msgDigest.update(clientCheckValue.getBytes());
        msgDigest.update(K.toString().getBytes());
        return new String(msgDigest.digest());
    }

    protected byte[] getLeftZeroPadded(byte[] input, int outputLength) {
        byte[] output = new byte[outputLength];
        if (input.length >= outputLength) {
            return input;
        } else {
            System.arraycopy(input, 0, output, outputLength - input.length,
                    input.length);
        }
        return output;
    }
}
