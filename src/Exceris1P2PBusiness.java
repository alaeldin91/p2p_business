import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class Exceris1P2PBusiness {
    private static final Logger LOGGER = Logger.getLogger(Exceris1P2PBusiness.class.getName());

    private final Map<String, BigDecimal> balances = new ConcurrentHashMap<>();
    private final Set<String> processedTransactions = ConcurrentHashMap.newKeySet();
    private final ReentrantLock transferLock = new ReentrantLock();

    public Exceris1P2PBusiness() {
        balances.put("Omer", new BigDecimal("800.00"));
        balances.put("Sara", new BigDecimal("1500.00"));
        balances.put("Lina", new BigDecimal("1200.00"));
    }

    /**
     * Checks if a username exists in the system
     * @param userName the username to check
     * @return true if user exists, false otherwise
     */
    public boolean checkFromUserNameIsExist(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            return false;
        }
        return balances.containsKey(userName.trim());
    }

    /**
     * Performs atomic money transfer between users
     * @param transactionId unique transaction identifier
     * @param fromUser sender username
     * @param toUser receiver username
     * @param amount transfer amount
     * @return true if transfer successful, false otherwise
     */
    public boolean transfer(String transactionId, String fromUser, String toUser, BigDecimal amount) {
        // Input validation
        if (!isValidTransferInput(transactionId, fromUser, toUser, amount)) {
            return false;
        }

        transferLock.lock();
        try {
            // Check if transaction already processed
            if (processedTransactions.contains(transactionId)) {
                LOGGER.warning("Transaction already processed: " + transactionId);
                return false;
            }

            // Check if users exist
            if (!checkFromUserNameIsExist(fromUser)) {
                LOGGER.warning("Sender user does not exist: " + fromUser);
                return false;
            }

            if (!checkFromUserNameIsExist(toUser)) {
                LOGGER.warning("Receiver user does not exist: " + toUser);
                return false;
            }

            // Get current balances
            BigDecimal senderBalance = balances.get(fromUser);
            BigDecimal receiverBalance = balances.get(toUser);

            // Check sufficient funds
            if (senderBalance.compareTo(amount) < 0) {
                LOGGER.warning("Insufficient balance for user: " + fromUser +
                              ". Current: " + senderBalance + ", Required: " + amount);
                return false;
            }

            // Perform atomic transfer
            balances.put(fromUser, senderBalance.subtract(amount));
            balances.put(toUser, receiverBalance.add(amount));
            processedTransactions.add(transactionId);

            LOGGER.info("Successfully transferred " + amount + " from " + fromUser + " to " + toUser);
            printBalances();
            return true;

        } finally {
            transferLock.unlock();
        }
    }

    /**
     * Convenience method for transfer with double amount
     */
    public boolean transfer(String transactionId, String fromUser, String toUser, double amount) {
        BigDecimal bdAmount = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
        return transfer(transactionId, fromUser, toUser, bdAmount);
    }

    /**
     * Withdraws money from a user account
     * @param transactionId unique transaction identifier
     * @param userName user to withdraw from
     * @param amount withdrawal amount
     * @return true if withdrawal successful, false otherwise
     */
    public boolean withdraw(String transactionId, String userName, BigDecimal amount) {
        if (!isValidWithdrawInput(transactionId, userName, amount)) {
            return false;
        }

        transferLock.lock();
        try {
            if (processedTransactions.contains(transactionId)) {
                LOGGER.warning("Transaction already processed: " + transactionId);
                return false;
            }

            if (!checkFromUserNameIsExist(userName)) {
                LOGGER.warning("User does not exist: " + userName);
                return false;
            }

            BigDecimal currentBalance = balances.get(userName);
            if (currentBalance.compareTo(amount) < 0) {
                LOGGER.warning("Insufficient balance for withdrawal. User: " + userName +
                              ", Current: " + currentBalance + ", Required: " + amount);
                return false;
            }

            balances.put(userName, currentBalance.subtract(amount));
            processedTransactions.add(transactionId);

            LOGGER.info("Successfully withdrew " + amount + " from " + userName);
            return true;

        } finally {
            transferLock.unlock();
        }
    }

    /**
     * Deposits money to a user account
     * @param transactionId unique transaction identifier
     * @param userName user to deposit to
     * @param amount deposit amount
     * @return true if deposit successful, false otherwise
     */
    public boolean deposit(String transactionId, String userName, BigDecimal amount) {
        if (!isValidDepositInput(transactionId, userName, amount)) {
            return false;
        }

        transferLock.lock();
        try {
            if (processedTransactions.contains(transactionId)) {
                LOGGER.warning("Transaction already processed: " + transactionId);
                return false;
            }

            if (!checkFromUserNameIsExist(userName)) {
                LOGGER.warning("User does not exist: " + userName);
                return false;
            }

            BigDecimal currentBalance = balances.get(userName);
            balances.put(userName, currentBalance.add(amount));
            processedTransactions.add(transactionId);

            LOGGER.info("Successfully deposited " + amount + " to " + userName);
            return true;

        } finally {
            transferLock.unlock();
        }
    }

    /**
     * Gets user balance (thread-safe)
     */
    public BigDecimal getBalance(String userName) {
        if (!checkFromUserNameIsExist(userName)) {
            return BigDecimal.ZERO;
        }
        return balances.get(userName);
    }

    private boolean isValidTransferInput(String transactionId, String fromUser, String toUser, BigDecimal amount) {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            LOGGER.warning("Transaction ID cannot be null or empty");
            return false;
        }
        if (fromUser == null || fromUser.trim().isEmpty()) {
            LOGGER.warning("From user cannot be null or empty");
            return false;
        }
        if (toUser == null || toUser.trim().isEmpty()) {
            LOGGER.warning("To user cannot be null or empty");
            return false;
        }
        if (fromUser.equals(toUser)) {
            LOGGER.warning("Cannot transfer to the same user");
            return false;
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            LOGGER.warning("Amount must be greater than zero");
            return false;
        }
        return true;
    }

    private boolean isValidWithdrawInput(String transactionId, String userName, BigDecimal amount) {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            LOGGER.warning("Transaction ID cannot be null or empty");
            return false;
        }
        if (userName == null || userName.trim().isEmpty()) {
            LOGGER.warning("Username cannot be null or empty");
            return false;
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            LOGGER.warning("Amount must be greater than zero");
            return false;
        }
        return true;
    }

    private boolean isValidDepositInput(String transactionId, String userName, BigDecimal amount) {
        return isValidWithdrawInput(transactionId, userName, amount);
    }

    public void printBalances() {
        transferLock.lock();
        try {
            balances.forEach((user, balance) ->
                System.out.println(user + " has balance: " + balance));

            BigDecimal totalBalance = balances.values().stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            System.out.println("Total balance in the system: " + totalBalance);
        } finally {
            transferLock.unlock();
        }
    }
}
