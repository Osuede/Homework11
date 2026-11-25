package vendingmachine;

/**
 * One kind of snack in the vending machine: its name, its price, and how many
 * are left.
 */
public class Snack {

    private final String name;
    private final double price;
    private int quantity;

    public Snack(String name, double price, int quantity) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("A snack needs a name");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be more than $0.00");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isInStock() {
        return quantity > 0;
    }

    /** Takes one snack out of the machine. */
    public void decreaseQuantity() {
        if (quantity == 0) {
            throw new IllegalStateException(name + " is sold out");
        }
        quantity--;
    }

    @Override
    public String toString() {
        return String.format("%s ($%.2f, %d left)", name, price, quantity);
    }
}
