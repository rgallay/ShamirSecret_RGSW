import java.math.BigInteger;
import java.security.SecureRandom;

public class Secret {

    private BigInteger nbpremier;
    private int nbbits;
    private int nbMinPart;
    private int nbTotalPart;
    private BigInteger[] coefficients;

    public Secret(int nbbits, int nbMinPart, int nbTotalPart){

        this.nbbits = nbbits;
        this.nbMinPart = nbMinPart;
        this.nbTotalPart = nbTotalPart;
    }

    public BigInteger generateSecret () {
        nbpremier = BigInteger.probablePrime(nbbits,new SecureRandom());

        BigInteger max = nbpremier;
        BigInteger base = new BigInteger("2");
        BigInteger min = base.pow(nbbits-1);

        BigInteger secret;

        do {
            secret = new BigInteger(max.bitLength(), new SecureRandom());
        } while (secret.compareTo(min)==-1 || secret.compareTo(max) >= 0);

        return secret;
    }

    public PartSecret[] generateShares(BigInteger secret) {

        BigInteger[] coefficients = new BigInteger[nbMinPart];
        coefficients[0] = secret;

        for(int i = 1; i<nbMinPart; i++) {
            BigInteger coefficient;
            do {
                coefficient = new BigInteger(nbpremier.bitLength(), new SecureRandom());
            }
            while(coefficient.compareTo(BigInteger.ZERO)==-1 || coefficient.compareTo(nbpremier) > 0);

            coefficients[i] = coefficient;
        }

        this.coefficients=coefficients;

        PartSecret[] shares = new PartSecret[nbTotalPart];

        for(int i = 1; i <= nbTotalPart; i++){
            BigInteger share = coefficients[0];


            for(int exposant = 1; exposant < nbMinPart; exposant++){
                share = share.add(coefficients[exposant].multiply(BigInteger.valueOf(i).pow(exposant)));
            }

            shares[i-1] = new PartSecret(i, share);
        }

        displayShares(shares);

        return shares;
    }


    public void displayShares(PartSecret[]shares) {
        System.out.println("Les parts générées sont : ");
        for(int i=0; i<shares.length; i++){
            System.out.println(shares[i]);
        }
    }


    public BigInteger getSecret(PartSecret[] shares)
    {
        BigInteger secret = BigInteger.ZERO;

        for(int i = 0; i < shares.length; i++)
        {
            BigInteger numerateur = BigInteger.ONE;
            BigInteger denominateur = BigInteger.ONE;

            for(int j = 0; j < shares.length; j++)
            {
                if(i == j)
                    continue;

                int startposition = shares[i].getX();
                int nextposition = shares[j].getX();

                numerateur = numerateur.multiply(BigInteger.valueOf(nextposition).negate()).mod(nbpremier);
                denominateur = denominateur.multiply(BigInteger.valueOf(startposition - nextposition)).mod(nbpremier);
            }
            BigInteger value = shares[i].getY();
            BigInteger tmp = value.multiply(numerateur) . multiply(moduloInverse(denominateur, nbpremier));
            secret = nbpremier.add(secret).add(tmp).mod(nbpremier);
        }

        System.out.println("Le secret: " + secret);

        return secret;
    }

    public BigInteger moduloInverse(BigInteger denominateur, BigInteger nbpremier)
    {
        BigInteger resultat;

        denominateur = denominateur.mod(nbpremier);

        if(denominateur.compareTo(BigInteger.ZERO) == -1){
            resultat = (gcd(nbpremier, denominateur.negate())[2]).negate();
        }
        else {
            resultat = gcd(nbpremier, denominateur)[2];
        }

        return nbpremier.add(resultat).mod(nbpremier);
    }

    public BigInteger[] gcd(BigInteger a, BigInteger b)
    {
        if (b.compareTo(BigInteger.ZERO) == 0)
            return new BigInteger[] {a, BigInteger.ONE, BigInteger.ZERO};
        else
        {
            BigInteger n = a.divide(b);
            BigInteger c = a.mod(b);
            BigInteger[] r = gcd(b, c);
            return new BigInteger[] {r[0], r[2], r[1].subtract(r[2].multiply(n))};
        }
    }

    public PartSecret[] addShares(PartSecret[] shares, int qte){

        this.nbTotalPart += qte;

        BigInteger secret = getSecret(shares);

        PartSecret[] newshares = new PartSecret[nbTotalPart];

        for(int i = 0; i<shares.length; i++){
            newshares[i] = shares[i];
        }

        for(int i = shares.length+1; i <=newshares.length ; i++){
            BigInteger share = secret;

            for(int exposant = 1; exposant < nbMinPart; exposant++){
                share = share.add(coefficients[exposant].multiply(BigInteger.valueOf(i).pow(exposant)));
            }

            newshares[i-1] = new PartSecret(i, share);
        }

        displayShares(newshares);

        return newshares;
    }

    public PartSecret[] updateSharesThreshold(PartSecret[] shares, int nbMinPart){
        this.nbMinPart = nbMinPart;

        BigInteger secret = getSecret(shares);
        return generateShares(secret);
    }

}
