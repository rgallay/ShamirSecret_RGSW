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
        System.out.println();
        System.out.println("INFORMATIONS POUR LA GENERATION D'UN SECRET");
        System.out.print("Nombre de bits : ");

            while (true) {
                try {
                    nbbits = scanner.nextInt();
                    if(nbbits<=0)
                        throw new NonPositiveInteger();
                    break;
                } catch (java.util.InputMismatchException e) {
                    System.out.print("Indiquer un nombre de bits correct : ");
                    scanner.nextLine();
                } catch(NonPositiveInteger e){
                    System.out.print(e.getMessage());
                    scanner.nextLine();
                }
            }



        System.out.print("Nombre de parts minimum pour reconstituer le secret : ");

        while(true) {
            try {
                nbminparts = scanner.nextInt();
                if(nbminparts<=0)
                    throw new NonPositiveInteger();
                break;
            } catch (java.util.InputMismatchException e) {
                System.out.print("Indiquer un nombre de parts minimum correct : ");
                scanner.nextLine();
            } catch (NonPositiveInteger e){
                System.out.print(e.getMessage());
                scanner.nextLine();
            }
        }

            System.out.print("Nombre total de parts : ");
            while (true) {
                try {
                    nbtotalparts = scanner.nextInt();
                    if(nbtotalparts<nbminparts)
                        throw new Exception();
                    break;
                } catch (java.util.InputMismatchException e) {
                    System.out.print("Indiquer un nombre total de parts correct : ");
                    scanner.nextLine();
                } catch(Exception e){
                    System.out.print("Indiquer un nombre supérieur au nombre minimum de part : ");
                }
            }

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

        while(true) {
            try {
                qte = scanner.nextInt();
                break;
            } catch (java.util.InputMismatchException e) {

                System.out.print("Indiquer un nombre de part correct : ");
                scanner.nextLine();
            }
        }


        shares = secret.addShares(shares,qte);

        System.out.println();
        System.out.println("-----------------------------------");
        System.out.println();
        System.out.println("MODIFICATION DU SEUIL");
        System.out.println();
        System.out.print("Nouveau nombre de parts minimum : ");

        while(true) {
            try {
                qte = scanner.nextInt();
                break;
            } catch (java.util.InputMismatchException e) {

                System.out.print("Indiquer un nombre de part correct : ");
                scanner.nextLine();
            }
        }

        shares = secret.updateSharesThreshold(shares, qte);

        System.out.println();
        System.out.println("-----------------------------------");
        System.out.println();
        System.out.println("RECONSTITUTION DU SECRET AVEC LE NOUVEAU SEUIL / NOUVELLES PARTS");

        secretReconstitue = secret.getSecret(shares);



    }



}
