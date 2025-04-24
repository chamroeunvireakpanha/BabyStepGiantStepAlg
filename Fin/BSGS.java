package Fin;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;

public class BSGS {

    public static BigInteger bigIntSqRootCeil(BigInteger x) {
        BigInteger left = BigInteger.ZERO;
        BigInteger right = x;
        BigInteger two = BigInteger.valueOf(2);

        while (left.compareTo(right) <= 0) {
            BigInteger mid = left.add(right).divide(two);
            BigInteger midSq = mid.multiply(mid);
            int cmp = midSq.compareTo(x);
            if (cmp < 0) {
                left = mid.add(BigInteger.ONE);
            } else if (cmp > 0) {
                right = mid.subtract(BigInteger.ONE);
            } else {
                return mid;
            }
        }
        return left;
    }

    public static BigInteger getOrder(BigInteger a, BigInteger p) {
        if (a.mod(p).equals(BigInteger.ZERO)) {
            throw new IllegalArgumentException("a must be coprime with p");
        }
        BigInteger m = p.subtract(BigInteger.ONE);
        if (m.equals(BigInteger.ZERO)) {
            return BigInteger.ONE;
        }

        ArrayList<BigInteger> divisors = new ArrayList<>();
        BigInteger sqrtM = bigIntSqRootCeil(m);
        for (BigInteger i = BigInteger.ONE; i.compareTo(sqrtM) <= 0; i = i.add(BigInteger.ONE)) {
            if (m.mod(i).equals(BigInteger.ZERO)) {
                divisors.add(i);
                BigInteger counterpart = m.divide(i);
                if (!counterpart.equals(i)) {
                    divisors.add(counterpart);
                }
            }
        }
        divisors.sort(BigInteger::compareTo);
        for (BigInteger d : divisors) {
            if (a.modPow(d, p).equals(BigInteger.ONE)) {
                return d;
            }
        }
        return m;
    }

    public static ArrayList<BigInteger> babyStepGiantStepAllSolutions(BigInteger a, BigInteger b, BigInteger p) {
        ArrayList<BigInteger> solutions = new ArrayList<>();

        if (!a.gcd(p).equals(BigInteger.ONE) || !b.gcd(p).equals(BigInteger.ONE)) {
            System.out.println("Error: 'a' and 'b' must be coprime with p.");
            return solutions;
        }

        BigInteger groupOrder = p.subtract(BigInteger.ONE);
        BigInteger n = bigIntSqRootCeil(groupOrder);

        HashMap<BigInteger, BigInteger> babySteps = new HashMap<>();
        BigInteger current = BigInteger.ONE;
        for (BigInteger j = BigInteger.ZERO; j.compareTo(n) < 0; j = j.add(BigInteger.ONE)) {
            if (!babySteps.containsKey(current)) {
                babySteps.put(current, j);
            }
            current = current.multiply(a).mod(p);
        }

        BigInteger aToN = a.modPow(n, p);
        BigInteger factor;
        try {
            factor = aToN.modInverse(p);
        } catch (ArithmeticException e) {
            System.out.println("Error: Unable to compute modular inverse. Check that 'a' is in the group of units.");
            return solutions;
        }

        BigInteger gamma = b;
        BigInteger x0 = null;
        for (BigInteger i = BigInteger.ZERO; i.compareTo(n) < 0; i = i.add(BigInteger.ONE)) {
            if (babySteps.containsKey(gamma)) {
                BigInteger j = babySteps.get(gamma);
                x0 = i.multiply(n).add(j);
                break;
            }
            gamma = gamma.multiply(factor).mod(p);
        }

        if (x0 == null) {
            return solutions;
        }

        BigInteger d = getOrder(a, p);

        for (BigInteger k = BigInteger.ZERO; x0.add(k.multiply(d)).compareTo(groupOrder) < 0; k = k.add(BigInteger.ONE)) {
            solutions.add(x0.add(k.multiply(d)));
        }

        return solutions;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter the base (a): ");
            BigInteger a = new BigInteger(scanner.nextLine());

            System.out.print("Enter the target value (b): ");
            BigInteger b = new BigInteger(scanner.nextLine());

            System.out.print("Enter the modulus (p): ");
            BigInteger p = new BigInteger(scanner.nextLine());

            ArrayList<BigInteger> solutions = babyStepGiantStepAllSolutions(a, b, p);
            if (!solutions.isEmpty()) {
                System.out.println("Solutions found:");
                for (BigInteger sol : solutions) {
                    System.out.println("we find x = " + sol + " such that " + a + "^" + sol + " ≡ " + b + " (mod " + p + ")");
                }
            } else {
                System.out.println("No solution found.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}