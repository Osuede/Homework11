package vendingmachine;

/**
 * A state the vending machine can be in. Each method is one thing a
 * customer can do, and each state decides what it does right now.
 */
public interface StateOfVendingMachine {

    void selectSnack(String snackName);

    void insertMoney(double amount);

    void dispenseSnack();
}
