package vendingmachine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SnackDispenseHandlerTest {

    private final Snack coke = new Snack("Coke", 1.50, 2);
    private final Snack pepsi = new Snack("Pepsi", 1.50, 2);
    private final Snack snickers = new Snack("Snickers", 1.00, 1);

    private final SnackDispenseHandler cokeHandler = new SnackDispenseHandler(coke);
    private final SnackDispenseHandler pepsiHandler = new SnackDispenseHandler(pepsi);
    private final SnackDispenseHandler snickersHandler = new SnackDispenseHandler(snickers);

    @BeforeEach
    void linkTheChain() {
        // Coke -> Pepsi -> Snickers
        cokeHandler.setNext(pepsiHandler);
        pepsiHandler.setNext(snickersHandler);
    }

    @Test
    void firstHandlerDispensesItsOwnSnack() {
        assertTrue(cokeHandler.dispense("Coke"));
        assertEquals(1, coke.getQuantity());
        assertEquals(2, pepsi.getQuantity(), "other snacks should not change");
    }

    @Test
    void requestIsPassedDownTheChainToTheRightHandler() {
        assertTrue(cokeHandler.dispense("Snickers"));
        assertEquals(0, snickers.getQuantity());
        assertEquals(2, coke.getQuantity(), "the Coke handler should only pass the request on");
        assertEquals(2, pepsi.getQuantity(), "the Pepsi handler should only pass the request on");
    }

    @Test
    void soldOutSnackIsNotDispensed() {
        assertTrue(cokeHandler.dispense("Snickers"), "the last Snickers");
        assertFalse(cokeHandler.dispense("Snickers"), "Snickers is sold out now");
        assertEquals(0, snickers.getQuantity());
    }

    @Test
    void snackThatNoHandlerSellsIsNotDispensed() {
        assertFalse(cokeHandler.dispense("Skittles"));
    }

    @Test
    void requestsOnlyMoveForwardInTheChain() {
        // The Coke handler comes before the Pepsi handler, so a request that starts at Pepsi never reaches it.
        assertFalse(pepsiHandler.dispense("Coke"));
        assertEquals(2, coke.getQuantity());
    }

    @Test
    void printsEveryHandlerTheRequestPassesThrough() {
        String output = printedBy(() -> cokeHandler.dispense("Snickers"));
        assertEquals(List.of(
                "[Coke handler] Not my snack, passing Snickers to the next handler",
                "[Pepsi handler] Not my snack, passing Snickers to the next handler",
                "[Snickers handler] Dispensing Snickers (0 left)"),
                output.lines().toList());
    }

    @Test
    void printsWhenTheSnackIsSoldOut() {
        printedBy(() -> snickersHandler.dispense("Snickers"));
        String output = printedBy(() -> snickersHandler.dispense("Snickers"));
        assertEquals(List.of("[Snickers handler] Snickers is sold out"), output.lines().toList());
    }

    @Test
    void printsWhenTheRequestReachesTheEndOfTheChain() {
        String output = printedBy(() -> snickersHandler.dispense("Skittles"));
        assertEquals(List.of("[Snickers handler] End of the chain, nobody sells Skittles"), output.lines().toList());
    }

    @Test
    void needsASnack() {
        assertThrows(IllegalArgumentException.class, () -> new SnackDispenseHandler(null));
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
