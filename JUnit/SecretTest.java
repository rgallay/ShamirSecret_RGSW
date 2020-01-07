import java.math.BigInteger;
import static org.junit.jupiter.api.Assertions.*;

class SecretTest {

    Secret secretObject = new Secret(8,3,5);
    PartSecret[] shares;
    BigInteger secret;


    @org.junit.jupiter.api.Test
    void generateShares() {

        secretObject.setNbpremier(BigInteger.valueOf(157));
        secret = BigInteger.valueOf(133);

        // Tester si le nombre de parts générées correspond au nombre total de parts souhaité
        assertEquals(secretObject.generateShares(secret).length,5);
        assertNotEquals(secretObject.generateShares(secret).length,3);
    }


    @org.junit.jupiter.api.Test
    void getSecret() {
        secretObject.setNbpremier(BigInteger.valueOf(157));
        secret = BigInteger.valueOf(133);
        secretObject.setSecret(secret);

        // Test avec 3 parts valides
        shares = new PartSecret[3];
        shares[0] = new PartSecret(1, BigInteger.valueOf(255));
        shares[1] = new PartSecret(3, BigInteger.valueOf(1105));
        shares[2] = new PartSecret(5, BigInteger.valueOf(2763));

        assertEquals(secret,secretObject.getSecret(shares));


        // Test avec 2 parts valides
        shares = new PartSecret[2];
        shares[0] = new PartSecret(1, BigInteger.valueOf(255));
        shares[1] = new PartSecret(3, BigInteger.valueOf(1105));

        assertNotEquals(secret,secretObject.getSecret(shares));

    }

    @org.junit.jupiter.api.Test
    void moduloInverse() {

        assertEquals(secretObject.moduloInverse(BigInteger.valueOf(154),BigInteger.valueOf(5)),BigInteger.valueOf(4));
        assertEquals(secretObject.moduloInverse(BigInteger.valueOf(180),BigInteger.valueOf(5)),BigInteger.valueOf(0));
    }

    @org.junit.jupiter.api.Test
    void gcd() {

        assertEquals(secretObject.gcd(BigInteger.valueOf(236000),BigInteger.valueOf(174000))[0],BigInteger.valueOf(2000));
        assertNotEquals(secretObject.gcd(BigInteger.valueOf(15),BigInteger.valueOf(5))[0],BigInteger.valueOf(7));
    }

}