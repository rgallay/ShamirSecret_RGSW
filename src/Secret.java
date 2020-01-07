import java.math.BigInteger;
import java.security.SecureRandom;

public class Secret {

    private BigInteger nbpremier;
    private int nbbits;
    private int nbMinPart;
    private int nbTotalPart;
    private BigInteger[] coefficients;
    private BigInteger secret;

    /**
     * Constructeur de l'objet secret avec les paramètres nécessaires
     * @param nbbits
     * @param nbMinPart
     * @param nbTotalPart
     */
    public Secret(int nbbits, int nbMinPart, int nbTotalPart){
        this.nbbits = nbbits;
        this.nbMinPart = nbMinPart;
        this.nbTotalPart = nbTotalPart;
    }

    /**
     * Cette méthode génère un secret aléatoire.
     * Un nombre premier aléatoire est généré sur la base du nombre de bits donné.
     * Le secret est généré aléatoirement entre 2^nbbits-1 et le nombre premier
     * @return
     */
    public BigInteger generateSecret () {
        nbpremier = BigInteger.probablePrime(nbbits,new SecureRandom());

        BigInteger max = nbpremier;
        BigInteger base = new BigInteger("2");
        BigInteger min = base.pow(nbbits-1);

        BigInteger secret;

        do {
            secret = new BigInteger(max.bitLength(), new SecureRandom());
        } while (secret.compareTo(min)==-1 || secret.compareTo(max) >= 0);

        this.secret = secret;
        return secret;
    }

    /**
     * Cette méthode génère les parts permettant de recronstruire le secret rentré en paramètre.
     * Un coefficient est calculé par rapport au nombre minimum de part permettant de reconstruire le secret.
     * Le coefficient (degré de la courbe) est utilisé pour le calcul des parts.
     * @param secret
     * @return
     */
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

    /**
     * Cette méthode permet l'affichage des coordonnées des parts permettant de retrouveer le secret
     * @param shares
     */
    public void displayShares(PartSecret[]shares) {
        System.out.println("Les parts générées sont : ");
        for(int i=0; i<shares.length; i++){
            System.out.println(shares[i]);
        }
    }

    /**
     * Cette méthode gère la reconstruction du secret en se basant sur l'interpolation de Lagrange.
     * Elle se base sur l'algorithme d'Euclide étendu et l'inverse multiplicatif.
     * Si le secret ne peut pas être reconstruit, une exception est levée et le programme s'arrête.
     *
     * @param shares
     * @return
     */
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

        try{
            if(secret.compareTo(this.secret) != 0)
                throw new Exception();
        } catch (Exception e) {
            System.out.println("Le secret ne peut pas être reconstruit avec ces parts.");
            System.exit(1);
        }

        System.out.println("Le secret: " + secret);

        return secret;
    }


    /**
     * Cette méthode permet de calculer l'inverse multiplicatif entre deux BigInteger.
     * Cette méthode est utilisée pour la reconstruction du secret.
     * Elle utilise le reste obtenu par le calcul du plus grand diviseur commun (Euclide) (3e case du tableau)
     * @param denominateur
     * @param nbpremier
     * @return
     */
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

    /**
     * Cette méthode cherche le plus grand diviseur commun entre deux BitInteger.
     * Elle utilise la récursivité jusqu'à obtenir un reste égal à zéro.
     * @param a
     * @param b
     * @return
     */
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

    /**
     * Cette méthode permet de créer des parts supplémentaires sur la base du secret existant.
     * La méthode commence par reconstruire le secret sur la base des parts existantes.
     * Le nombre de parts à créer est passé en paramètre.
     *
     * @param shares
     * @param qte
     * @return
     */
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

    /**
     * Cette méthode permet de modifier le nombre minimum de parts nécessaires pour la reconstruction du secret.
     * Le secret n'est pas modifié. De nouvelles parts sont créées car le coefficient change (basé sur le nombre minimum de parts).
     * @param shares
     * @param nbMinPart
     * @return
     */
    public PartSecret[] updateSharesThreshold(PartSecret[] shares, int nbMinPart){
        this.nbMinPart = nbMinPart;

        BigInteger secret = getSecret(shares);
        return generateShares(secret);
    }

    public void setNbpremier (BigInteger nbpremier){
        this.nbpremier = nbpremier;
    }

    public void setSecret (BigInteger secret){
        this.secret = secret;
    }

}
