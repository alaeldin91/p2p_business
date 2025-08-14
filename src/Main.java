//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws InterruptedException {

        P2PBusiness service = new P2PBusiness();
        Thread t1 = new Thread(() -> service.transfer("tx-1001", "Alaeldin", "Ahamed", 100));
        Thread t2 = new Thread(() -> service.transfer("tx-1001", "Alaeldin", "Ahamed", 100)); // نفس ID → لن ينفذ
        Thread t3 = new Thread(() -> service.transfer("tx-1002", "Mohamed", "sara", 50));

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();
    }
}