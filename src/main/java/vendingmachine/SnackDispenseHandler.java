package vendingmachine;

/**
 * One handler in the dispensing chain (Chain of Responsibility).
 *
 * Each handler is in charge of one snack. When a request comes in, the handler checks
 * whether it is for its snack. If it is, the handler dispenses one, or reports that it is
 * sold out. If it is not, the handler passes the request to the next handler.
 */
public class SnackDispenseHandler {

    private final Snack snack;
    private SnackDispenseHandler next;

    public SnackDispenseHandler(Snack snack) {
        if (snack == null) {
            throw new IllegalArgumentException("A handler needs a snack");
        }
        this.snack = snack;
    }

    /** Sets the handler that gets every request this handler is not in charge of. */
    public void setNext(SnackDispenseHandler next) {
        this.next = next;
    }

    /**
     * Dispenses one of the named snack, using whichever handler in the chain is in charge of it.
     *
     * @return true if a snack came out, false if it is sold out or no handler in the chain sells it
     */
    public boolean dispense(String snackName) {
        // Not my snack: let the next handler deal with it.
        if (!snack.getName().equals(snackName)) {
            if (next == null) {
                print("End of the chain, nobody sells " + snackName);
                return false;
            }
            print("Not my snack, passing " + snackName + " to the next handler");
            return next.dispense(snackName);
        }

        // My snack: dispense one if there is one left.
        if (!snack.isInStock()) {
            print(snackName + " is sold out");
            return false;
        }
        snack.decreaseQuantity();
        print("Dispensing " + snackName + " (" + snack.getQuantity() + " left)");
        return true;
    }

    private void print(String message) {
        System.out.println("[" + snack.getName() + " handler] " + message);
    }
}
