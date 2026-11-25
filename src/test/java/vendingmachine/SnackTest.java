package vendingmachine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SnackTest {

    @Test
    void storesNamePriceAndQuantity() {
        Snack coke = new Snack("Coke", 1.50, 5);
        assertEquals("Coke", coke.getName());
        assertEquals(1.50, coke.getPrice());
        assertEquals(5, coke.getQuantity());
    }

    @Test
    void decreaseQuantityTakesOneOut() {
        Snack coke = new Snack("Coke", 1.50, 5);
        coke.decreaseQuantity();
        assertEquals(4, coke.getQuantity());
    }

    @Test
    void isInStockUntilTheLastOneIsTaken() {
        Snack snickers = new Snack("Snickers", 1.00, 1);
        assertTrue(snickers.isInStock());
        snickers.decreaseQuantity();
        assertEquals(0, snickers.getQuantity());
        assertFalse(snickers.isInStock());
    }

    @Test
    void cannotTakeOneWhenSoldOut() {
        Snack snickers = new Snack("Snickers", 1.00, 0);
        assertThrows(IllegalStateException.class, snickers::decreaseQuantity);
        assertEquals(0, snickers.getQuantity(), "quantity should never go below 0");
    }

    @Test
    void needsAName() {
        assertThrows(IllegalArgumentException.class, () -> new Snack(null, 1.50, 5));
        assertThrows(IllegalArgumentException.class, () -> new Snack("  ", 1.50, 5));
    }

    @Test
    void priceMustBeMoreThanZero() {
        assertThrows(IllegalArgumentException.class, () -> new Snack("Coke", 0, 5));
        assertThrows(IllegalArgumentException.class, () -> new Snack("Coke", -1.50, 5));
    }

    @Test
    void quantityCannotBeNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Snack("Coke", 1.50, -1));
    }

    @Test
    void toStringShowsNamePriceAndQuantity() {
        assertEquals("Coke ($1.50, 5 left)", new Snack("Coke", 1.50, 5).toString());
    }
}
