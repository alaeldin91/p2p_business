import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class P2PBusiness {

    private final Map<String,Double> balances = new ConcurrentHashMap<>();
    private ReentrantLock lock = new ReentrantLock();
    private Set<String> procesTransaction = ConcurrentHashMap.newKeySet();
    public P2PBusiness()
    {
        //Initialize the balances with some dummy data
        balances.put("Alaeldin", 2000.0);
        balances.put("Ahamed", 1000.0);
        balances.put("Sara", 2000.0);
        balances.put("Mohamed", 3000.0);
    }

    public boolean transfer(String transactionId,String fromUser,String toUser,double amount) {
        lock.lock();
        try {
            if (procesTransaction.contains(transactionId)) {
                System.out.println("Transaction already processed: " + transactionId);
                return false;
            }
            if(!balances.containsKey(fromUser) || !balances.containsKey(toUser)) {

                System.out.println("Invalid users involved in the transaction.");
                return false;
            }

            if (amount <= 0)
            {
                System.out.println("Amount must be greater than zero.");
                return false;
            }

            double senderBalance = balances.get(fromUser);
            if(senderBalance < amount)
            {
                System.out.println("Insufficient balance for user: " + fromUser);
                return false;
            }
            balances.put(fromUser, senderBalance - amount);
            balances.put(toUser, balances.get(toUser) + amount);
            procesTransaction.add(transactionId);
            System.out.println("Transfred " + amount + " from " + fromUser + " to " + toUser);
            printBalances();
            return true;
        }
        finally {
            lock.unlock();
        }
    }

    public void printBalances()
    {
         balances.forEach((user,balance)->
                 System.out.println(user + " has balance: " + balance));
         double totalBalance = balances.values().stream()
                 .mapToDouble(Double::doubleValue)
                 .sum();
         System.out.println(
                    "Total balance in the system: " + totalBalance
         );
         System.out.println("---------------------------------");
    }
}
