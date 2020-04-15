package com.university.diploma.service;

import com.university.diploma.dto.UserSignUpDto;
import com.university.diploma.form.SignUpForm;
import com.university.diploma.session.AuthorizationDetails;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Scope(value = "prototype")
@Service
public class SRPService {

    protected static final byte[] G_HEX = Hex.decode("02");
    protected static final byte[] N_HEX = Hex.decode(
            "AC6BDB41324A9A9BF166DE5E1389582F" +
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
    protected static final int SALT_LENGTH = 16;

    protected Digest digest; // hash function SHA-256

    @Autowired
    protected RandomGeneratorService randomGeneratorService;


    public SRPService() {
        // init Digest
        digest = new SHA256Digest();
        // init N
        byte[] nBytes = N.toString().getBytes();
        digest.update(nBytes, 0, nBytes.length);

        // init g
        byte[] gBytes = G.toString().getBytes();
        digest.update(gBytes, 0, gBytes.length);

        // init K - multiplier parameter
        byte[] kHex = new byte[digest.getDigestSize()];
        digest.doFinal(kHex, 0);

        K = new BigInteger(kHex);
    }

    public UserSignUpDto signUp(SignUpForm form) {
        byte[] salt = new byte[SALT_LENGTH];
        randomGeneratorService.nextBytes(salt);
        salt = new BigInteger(salt).toString(16).getBytes();

        String userNameWithPassword = form.getUsername() + " : " + form.getPassword();
        digest.update(userNameWithPassword.getBytes(), 0, userNameWithPassword.getBytes().length);

        byte[] userNameWithPasswordBytes = new byte[digest.getDigestSize()];
        digest.doFinal(userNameWithPasswordBytes, 0);

        userNameWithPasswordBytes = new BigInteger(userNameWithPasswordBytes).toString(16).getBytes();

        digest.update(salt, 0, salt.length);
        digest.update(userNameWithPasswordBytes, 0, userNameWithPasswordBytes.length);

        byte[] xBytes = new byte[digest.getDigestSize()];
        digest.doFinal(xBytes, 0);
        BigInteger privateKey = new BigInteger(xBytes);

        BigInteger v = G.modPow(privateKey, N); // verifier

        return new UserSignUpDto(form.getUsername(), new String(salt), v.toString(), form.getKeyword());
    }

    public void computeEmphaticKeyB(AuthorizationDetails details) {
        byte[] randomB = new byte[32];
        randomGeneratorService.nextBytes(randomB);
        details.setRandomB(new BigInteger(randomB).toString(16));

        BigInteger V = new BigInteger(details.getVerifier());
        BigInteger emphaticKeyB = K.multiply(V)
                .add(G.modPow(new BigInteger(details.getRandomB().getBytes()), N))
                .mod(N);
        details.setEmphaticKeyB(emphaticKeyB);
    }

    public String computeSessionKey(AuthorizationDetails details) {
        BigInteger emphaticKeyA = details.getEmphaticKeyA();
        byte[] emphaticKeyABytes = emphaticKeyA.toString().getBytes();
        digest.update(emphaticKeyABytes, 0, emphaticKeyABytes.length);

        byte[] emphaticKeyBBytes = details.getEmphaticKeyB().toString().getBytes();
        digest.update(emphaticKeyBBytes, 0, emphaticKeyBBytes.length);

        byte[] maskValueBytes = new byte[digest.getDigestSize()];
        digest.doFinal(maskValueBytes, 0);
        BigInteger maskValue = new BigInteger(maskValueBytes); // u = H(A,B)

        BigInteger verifierValue = new BigInteger(details.getVerifier());
        BigInteger supportPow = new BigInteger(details.getRandomB().getBytes()); // b
        BigInteger supportNumber = verifierValue.modPow(maskValue, N); // v ^ u % N
        BigInteger sessionKey = emphaticKeyA.multiply(supportNumber)
                .modPow(supportPow, N);

        byte[] sessionKeyBytes = sessionKey.toString().getBytes();
        digest.update(sessionKeyBytes, 0, sessionKeyBytes.length);

        byte[] sessionKeyDigest = new byte[digest.getDigestSize()];
        digest.doFinal(sessionKeyDigest, 0);
        return new BigInteger(sessionKeyDigest).toString(16);
    }

    public String computeClientCheckValue(AuthorizationDetails details, String username, String sessionKey) {
        byte[] nBytes = N.toString().getBytes();
        digest.update(nBytes, 0, nBytes.length);
        byte[] nDigest = new byte[digest.getDigestSize()];
        digest.doFinal(nDigest, 0);
        BigInteger n = new BigInteger(nDigest);

        byte[] gBytes = G.toString().getBytes();
        digest.update(gBytes, 0, gBytes.length);
        byte[] gDigest = new byte[digest.getDigestSize()];
        digest.doFinal(gDigest, 0);
        BigInteger g = new BigInteger(gDigest);

        byte[] usernameBytes = username.getBytes();
        digest.update(usernameBytes, 0, usernameBytes.length);
        byte[] usernameDigest = new byte[digest.getDigestSize()];
        digest.doFinal(usernameDigest, 0);
        BigInteger i = new BigInteger(usernameDigest);

        byte[] valueBytes = n.xor(g).toString().getBytes();
        digest.update(valueBytes, 0, valueBytes.length);
        digest.update(i.toString().getBytes(), 0, i.toString().getBytes().length);
        digest.update(details.getSalt().getBytes(), 0, details.getSalt().getBytes().length);

        byte[] emphaticKeyABytes = details.getEmphaticKeyA().toString().getBytes();
        digest.update(emphaticKeyABytes, 0, emphaticKeyABytes.length);

        byte[] emphaticKeyBBytes = details.getEmphaticKeyB().toString().getBytes();
        digest.update(emphaticKeyBBytes, 0, emphaticKeyBBytes.length);

        byte[] kBytes = sessionKey.getBytes();
        digest.update(kBytes, 0, kBytes.length);

        byte[] checkValueBytes = new byte[digest.getDigestSize()];
        digest.doFinal(checkValueBytes, 0);
        return new BigInteger(checkValueBytes).toString(16);
    }

    public String computeServerCheckValue(AuthorizationDetails details, String clientCheckValue, String sessionKey) {
        byte[] emphaticKeyABytes = details.getEmphaticKeyA().toString().getBytes();
        digest.update(emphaticKeyABytes, 0, emphaticKeyABytes.length);
        byte[] clientCheckValueBytes = clientCheckValue.getBytes();
        digest.update(clientCheckValueBytes, 0, clientCheckValueBytes.length);
        byte[] kBytes = sessionKey.getBytes();
        digest.update(kBytes, 0, kBytes.length);

        byte[] checkValueBytes = new byte[digest.getDigestSize()];
        digest.doFinal(checkValueBytes, 0);
        return new BigInteger(checkValueBytes).toString(16);
    }
}
