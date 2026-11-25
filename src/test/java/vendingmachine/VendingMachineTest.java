package vendingmachine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VendingMachineTest {

    private final Snack coke = new Snack("Coke", 1.50, 2);
    private final Snack snickers = new Snack("Snickers", 1.00, 1);
    private VendingMachine machine;

    @BeforeEach
    void setUp() {
        SnackDispenseHandler cokeHandler = new SnackDispenseHandler(coke);
        cokeHandler.setNext(new SnackDispenseHandler(snickers));
        machine = new VendingMachine(List.of(coke, snickers), cokeHandler);
    }

    @Test
    void startsIdle() {
        assertInstanceOf(IdleState.class, machine.getCurrentState());
    }

    @Test
    void holdsAllItsSnacks() {
        assertEquals(List.of(coke, snickers), machine.getSnacks());
    }

    @Test
    void needsSnacksAndADispensingChain() {
        SnackDispenseHandler handler = new SnackDispenseHandler(coke);
        assertThrows(IllegalArgumentException.class, () -> new VendingMachine(List.of(), handler));
        assertThrows(IllegalArgumentException.class, () -> new VendingMachine(List.of(coke), null));
    }

    // A whole purchase

    @Test
    void purchasePrintsEveryStepAndStateChange() {
        String output = printedBy(() -> buy("Coke", 2.00));
        assertEquals(List.of(
                "Selected Coke ($1.50). Please insert money",
                "[State] Idle -> WaitingForMoney",
                "Inserted $2.00",
                "[State] WaitingForMoney -> DispensingSnack",
                "[Coke handler] Dispensing Coke (1 left)",
                "Here is your Coke. Your change is $0.50",
                "[State] DispensingSnack -> Idle"),
                output.lines().toList());
        assertEquals(1, coke.getQuantity());
        assertInstanceOf(IdleState.class, machine.getCurrentState());
    }

    @Test
    void exactMoneyIsEnough() {
        String output = printedBy(() -> buy("Coke", 1.50));
        assertTrue(output.contains("Here is your Coke. Your change is $0.00"));
    }

    @Test
    void soldOutSnackGivesTheMoneyBack() {
        buy("Snickers", 1.00); // the last Snickers
        String output = printedBy(() -> buy("Snickers", 1.00));
        assertTrue(output.contains("[Snickers handler] Snickers is sold out"));
        assertTrue(output.contains("Could not dispense Snickers. Returning $1.00"));
        assertEquals(0, snickers.getQuantity());
        assertInstanceOf(IdleState.class, machine.getCurrentState());
    }

    // Idle

    @Test
    void idleGoesToWaitingForMoneyWhenASnackIsSelected() {
        machine.selectSnack("Coke");
        assertInstanceOf(WaitingForMoneyState.class, machine.getCurrentState());
    }

    @Test
    void idleStaysIdleForASnackTheMachineDoesNotSell() {
        String output = printedBy(() -> machine.selectSnack("Skittles"));
        assertEquals(List.of("Sorry, this machine does not sell Skittles"), output.lines().toList());
        assertInstanceOf(IdleState.class, machine.getCurrentState());
    }

    @Test
    void idleRefusesMoneyAndDispensing() {
        String output = printedBy(() -> {
            machine.insertMoney(1.00);
            machine.dispenseSnack();
        });
        assertEquals(List.of(
                "Please select a snack first. Returning $1.00",
                "Please select a snack first"),
                output.lines().toList());
        assertInstanceOf(IdleState.class, machine.getCurrentState());
    }

    // WaitingForMoney

    @Test
    void notEnoughMoneyIsReturnedAndTheMachineGoesBackToIdle() {
        machine.selectSnack("Coke");
        String output = printedBy(() -> machine.insertMoney(1.00));
        assertEquals(List.of(
                "$1.00 is not enough for Coke ($1.50). Returning $1.00",
                "[State] WaitingForMoney -> Idle"),
                output.lines().toList());
        assertEquals(2, coke.getQuantity());
        assertInstanceOf(IdleState.class, machine.getCurrentState());
    }

    @Test
    void waitingForMoneyRefusesAnotherSelectionAndDispensing() {
        machine.selectSnack("Coke");
        String output = printedBy(() -> {
            machine.selectSnack("Snickers");
            machine.dispenseSnack();
        });
        assertEquals(List.of(
                "You already selected Coke. Please insert money",
                "Please insert money first"),
                output.lines().toList());
        assertInstanceOf(WaitingForMoneyState.class, machine.getCurrentState());
    }

    // DispensingSnack

    @Test
    void dispensingRefusesASelectionAndMoreMoney() {
        machine.selectSnack("Coke");
        machine.insertMoney(2.00);
        String output = printedBy(() -> {
            machine.selectSnack("Snickers");
            machine.insertMoney(1.00);
        });
        assertEquals(List.of(
                "Please wait, your Coke is being dispensed",
                "Please wait, your Coke is being dispensed. Returning $1.00"),
                output.lines().toList());
        assertInstanceOf(DispensingSnackState.class, machine.getCurrentState());
    }

    private void buy(String snackName, double money) {
        machine.selectSnack(snackName);
        machine.insertMoney(money);
        machine.dispenseSnack();
    }

    private static String printedBy(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream printed = new ByteArrayOutputStream();
        System.setOut(new PrintStream(printed));
        try {
            action.run();
        } finally {
            System.setOut(originalOut);
        }
        return printed.toString();
    }
}
