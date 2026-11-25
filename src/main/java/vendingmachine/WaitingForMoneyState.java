package vendingmachine;

/** A snack is selected and the machine is waiting to be paid. */
public class WaitingForMoneyState implements StateOfVendingMachine {

    private final VendingMachine machine;

    public WaitingForMoneyState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void selectSnack(String snackName) {
        System.out.println("You already selected " + machine.getSelectedSnack().getName()
                + ". Please insert money");
    }

    @Override
    public void insertMoney(double amount) {
        Snack snack = machine.getSelectedSnack();
        if (amount >= snack.getPrice()) {
            machine.setInsertedMoney(amount);
            System.out.printf("Inserted $%.2f%n", amount);
            machine.setState(machine.getDispensingSnackState());
        } else {
            System.out.printf("$%.2f is not enough for %s ($%.2f). Returning $%.2f%n",
                    amount, snack.getName(), snack.getPrice(), amount);
            machine.clearTransaction();
            machine.setState(machine.getIdleState());
        }
    }

    @Override
    public void dispenseSnack() {
        System.out.println("Please insert money first");
    }

    @Override
    public String toString() {
        return "WaitingForMoney";
    }
}
