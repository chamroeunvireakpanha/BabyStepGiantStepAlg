package Fin;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;

public class BSGS {

    // Computes the ceiling of the square root of a BigInteger.
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

    // Computes the order of a modulo p, assuming p is prime.
    // For a prime p, the multiplicative group order is p.
    public static BigInteger getOrder(BigInteger a, BigInteger p) {
        // Since p is prime, group order is simply p (the multiplicative group mod p).
        return p;
    }

    // Implements the Baby-Step Giant-Step algorithm for discrete logarithm
    // and returns a list of all solutions x (in the range 0 <= x < p) satisfying a^x ≡ b (mod p).
    public static ArrayList<BigInteger> babyStepGiantStepAllSolutions(BigInteger a, BigInteger b, BigInteger p) {
        ArrayList<BigInteger> solutions = new ArrayList<>();

        // Ensure a and b are in the group of units modulo p.
        if (!a.gcd(p).equals(BigInteger.ONE) || !b.gcd(p).equals(BigInteger.ONE)) {
            System.out.println("Error: 'a' and 'b' must be coprime with p.");
            return solutions;
        }

        // Since p is prime, the group order is simply p.
        BigInteger groupOrder = p;

        // n = ceil(sqrt(groupOrder))
        BigInteger n = bigIntSqRootCeil(groupOrder);

        // Baby-step: compute a^j mod p for j = 0 to n-1 and store in a hash map.
        HashMap<BigInteger, BigInteger> babySteps = new HashMap<>();
        BigInteger current = BigInteger.ONE;
        for (BigInteger j = BigInteger.ZERO; j.compareTo(n) < 0; j = j.add(BigInteger.ONE)) {
            babySteps.put(current, j);
            current = current.multiply(a).mod(p);
        }

        // Giant-step: compute a^-n mod p.
        BigInteger aToN = a.modPow(n, p);
        BigInteger factor;
        try {
            factor = aToN.modInverse(p);
        } catch (ArithmeticException e) {
            System.out.println("Error: Unable to compute modular inverse. Check that 'a' is in the group of units.");
            return solutions;
        }

        // Find the minimal solution x0 such that a^x0 ≡ b (mod p).
        BigInteger gamma = b;
        BigInteger x0 = null;
        for (BigInteger i = BigInteger.ZERO; i.compareTo(n) < 0; i = i.add(BigInteger.ONE)) {
            if (babySteps.containsKey(gamma)) {
                BigInteger j = babySteps.get(gamma);
                x0 = i.multiply(n).add(j);  // x0 = i*n + j
                break;
            }
            gamma = gamma.multiply(factor).mod(p);
        }

        if (x0 == null) {
            // No solution found.
            return solutions;
        }

        // Compute the order d of a modulo p (which is p because p is prime).
        BigInteger d = getOrder(a, p);

        // All solutions are x = x0 + k*d.
        // Here we list all solutions in the range [0, groupOrder).
        for (BigInteger k = BigInteger.ZERO; x0.add(k.multiply(d)).compareTo(groupOrder) < 0; k = k.add(BigInteger.ONE)) {
            solutions.add(x0.add(k.multiply(d)));
        }

        return solutions;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // Ask the user for input values.
            System.out.print("Enter the base (a): ");
            BigInteger a = new BigInteger(scanner.nextLine());

            System.out.print("Enter the target value (b): ");
            BigInteger b = new BigInteger(scanner.nextLine());

            System.out.print("Enter the modulus (p): ");
            BigInteger p = new BigInteger(scanner.nextLine());

            // Solve the discrete logarithm problem and get all solutions.
            ArrayList<BigInteger> solutions = babyStepGiantStepAllSolutions(a, b, p);
            if (!solutions.isEmpty()) {
                System.out.println("Solutions found:");
                for (BigInteger sol : solutions) {
                    System.out.println("x = " + sol + " such that " + a + "^" + sol + " ≡ " + b + " (mod " + p + ")");
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
