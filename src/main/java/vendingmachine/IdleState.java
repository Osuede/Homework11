package vendingmachine;

/** No snack is selected yet. The machine starts in this state. */
public class IdleState implements StateOfVendingMachine {

    private final VendingMachine machine;

    public IdleState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void selectSnack(String snackName) {
        Snack snack = machine.findSnack(snackName);
        if (snack == null) {
            System.out.println("Sorry, this machine does not sell " + snackName);
            return;
        }
        machine.setSelectedSnack(snack);
        System.out.printf("Selected %s ($%.2f). Please insert money%n",
                snack.getName(), snack.getPrice());
        machine.setState(machine.getWaitingForMoneyState());
    }

    @Override
    public void insertMoney(double amount) {
        System.out.printf("Please select a snack first. Returning $%.2f%n", amount);
    }

    @Override
    public void dispenseSnack() {
        System.out.println("Please select a snack first");
    }

    @Override
    public String toString() {
        return "Idle";
    }
}
