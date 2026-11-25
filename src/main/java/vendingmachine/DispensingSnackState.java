package vendingmachine;

/** The customer paid enough, so the machine can dispense the snack. */
public class DispensingSnackState implements StateOfVendingMachine {

    private final VendingMachine machine;

    public DispensingSnackState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void selectSnack(String snackName) {
        System.out.println("Please wait, your " + machine.getSelectedSnack().getName()
                + " is being dispensed");
    }

    @Override
    public void insertMoney(double amount) {
        System.out.printf("Please wait, your %s is being dispensed. Returning $%.2f%n",
                machine.getSelectedSnack().getName(), amount);
    }

    @Override
    public void dispenseSnack() {
        Snack snack = machine.getSelectedSnack();
        double paid = machine.getInsertedMoney();

        // The chain finds this snack's handler, which checks the stock.
        boolean dispensed = machine.getSnackDispenser().dispense(snack.getName());

        if (dispensed) {
            System.out.printf("Here is your %s. Your change is $%.2f%n",
                    snack.getName(), paid - snack.getPrice());
        } else {
            System.out.printf("Could not dispense %s. Returning $%.2f%n", snack.getName(), paid);
        }
        machine.clearTransaction();
        machine.setState(machine.getIdleState());
    }

    @Override
    public String toString() {
        return "DispensingSnack";
    }
}
