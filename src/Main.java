import java.math.BigInteger;

public class Main {

    public static void main(String[] args) {

        Secret secret = new Secret(5,2,8);

        BigInteger secretvalue = secret.generateSecret();
        PartSecret[] shares = secret.generateShares(secretvalue);
        BigInteger secretReconstitue = secret.getSecret(shares);
        shares = secret.addShares(shares,2);


        if(secretReconstitue.compareTo(secretvalue) == 0)
            System.out.println("C'est une réussite pour l'humanité.");

    }



}
