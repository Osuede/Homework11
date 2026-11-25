package vendingmachine;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The machine itself (the State pattern's context). It holds the snacks,
 * the first handler of the dispensing chain, and the current state, and it
 * passes every customer action to the current state.
 */
public class VendingMachine {

    private final Map<String, Snack> snacks = new LinkedHashMap<>();
    private final SnackDispenseHandler snackDispenser;

    private final StateOfVendingMachine idleState = new IdleState(this);
    private final StateOfVendingMachine waitingForMoneyState = new WaitingForMoneyState(this);
    private final StateOfVendingMachine dispensingSnackState = new DispensingSnackState(this);
    private StateOfVendingMachine currentState = idleState;

    // The current customer's selected snack and money.
    private Snack selectedSnack;
    private double insertedMoney;

    public VendingMachine(List<Snack> snacks, SnackDispenseHandler snackDispenser) {
        if (snacks == null || snacks.isEmpty()) {
            throw new IllegalArgumentException("A vending machine needs snacks");
        }
        if (snackDispenser == null) {
            throw new IllegalArgumentException("A vending machine needs a dispensing chain");
        }
        for (Snack snack : snacks) {
            this.snacks.put(snack.getName(), snack);
        }
        this.snackDispenser = snackDispenser;
    }

    // What a customer can do. Each action is passed to the current state.

    public void selectSnack(String snackName) {
        currentState.selectSnack(snackName);
    }

    public void insertMoney(double amount) {
        currentState.insertMoney(amount);
    }

    public void dispenseSnack() {
        currentState.dispenseSnack();
    }

    public StateOfVendingMachine getCurrentState() {
        return currentState;
    }

    public List<Snack> getSnacks() {
        return List.copyOf(snacks.values());
    }

    // Used by the states.

    void setState(StateOfVendingMachine newState) {
        System.out.println("[State] " + currentState + " -> " + newState);
        currentState = newState;
    }

    StateOfVendingMachine getIdleState() {
        return idleState;
    }

    StateOfVendingMachine getWaitingForMoneyState() {
        return waitingForMoneyState;
    }

    StateOfVendingMachine getDispensingSnackState() {
        return dispensingSnackState;
    }

    SnackDispenseHandler getSnackDispenser() {
        return snackDispenser;
    }

    Snack findSnack(String snackName) {
        return snacks.get(snackName);
    }

    Snack getSelectedSnack() {
        return selectedSnack;
    }

    void setSelectedSnack(Snack snack) {
        selectedSnack = snack;
    }

    double getInsertedMoney() {
        return insertedMoney;
    }

    void setInsertedMoney(double amount) {
        insertedMoney = amount;
    }

    void clearTransaction() {
        selectedSnack = null;
        insertedMoney = 0;
    }
}
