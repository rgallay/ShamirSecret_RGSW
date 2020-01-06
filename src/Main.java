import java.math.BigInteger;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        int nbbits;
        int nbminparts;
        int nbtotalparts;
        int qte;

        Scanner scanner = new Scanner(System.in);

        System.out.println("SHAMIR SECRET SHARING");
        System.out.println("");

        System.out.println("INFORMATIONS POUR LA GENERATION D'UN SECRET");
        System.out.print("Nombre de bits : ");
        nbbits = scanner.nextInt();

        System.out.print("Nombre de parts minimum pour reconstituer le secret : ");
        nbminparts = scanner.nextInt();

        System.out.print("Nombre total de parts : ");
        nbtotalparts = scanner.nextInt();

        System.out.println();
        System.out.println("-----------------------------------");
        System.out.println();

        Secret secret = new Secret(nbbits,nbminparts,nbtotalparts);

        BigInteger secretvalue = secret.generateSecret();
        PartSecret[] shares = secret.generateShares(secretvalue);

        System.out.println();
        System.out.println("-----------------------------------");
        System.out.println();
        System.out.println("RECONSTITUTION DU SECRET");

        BigInteger secretReconstitue = secret.getSecret(shares);

        System.out.println();
        System.out.println("-----------------------------------");
        System.out.println();
        System.out.println("AJOUT DE PARTS");
        System.out.println();
        System.out.print("Nombre de parts à ajouter : ");
        qte = scanner.nextInt();


        shares = secret.addShares(shares,qte);

        System.out.println();
        System.out.println("-----------------------------------");
        System.out.println();
        System.out.println("MODIFICATION DU SEUIL");
        System.out.println();
        System.out.print("Nouveau nombre de parts minimum : ");
        qte = scanner.nextInt();

        shares = secret.updateSharesThreshold(shares, qte);

        System.out.println();
        System.out.println("-----------------------------------");
        System.out.println();
        System.out.println("RECONSTITUTION DU SECRET AVEC LE NOUVEAU SEUIL / NOUVELLES PARTS");

        secretReconstitue = secret.getSecret(shares);



    }



}
