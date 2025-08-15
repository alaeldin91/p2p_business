import java.math.BigDecimal;

public class TestP2PBusiness {
    public static void main(String[] args) {
        Exceris1P2PBusiness business = new Exceris1P2PBusiness();

        System.out.println("=== Initial Balances ===");
        business.printBalances();

        // Test checkFromUserNameIsExist
        System.out.println("\n=== Testing User Existence ===");
        System.out.println("Omer exists: " + business.checkFromUserNameIsExist("Omer"));
        System.out.println("Ahmed exists: " + business.checkFromUserNameIsExist("Ahmed"));

        // Test transfer
        System.out.println("\n=== Testing Transfer ===");
        business.transfer("TXN001", "Omer", "Sara", new BigDecimal("100.00"));

        // Test withdrawal
        System.out.println("\n=== Testing Withdrawal ===");
        business.withdraw("TXN002", "Sara", new BigDecimal("200.00"));

        // Test deposit
        System.out.println("\n=== Testing Deposit ===");
        business.deposit("TXN003", "Lina", new BigDecimal("300.00"));

        // Test duplicate transaction (should be rejected)
        System.out.println("\n=== Testing Duplicate Transaction ===");
        business.transfer("TXN001", "Sara", "Lina", new BigDecimal("50.00"));

        System.out.println("\n=== Final Balances ===");
        business.printBalances();
    }
}
