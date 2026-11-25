# Homework 11: Behavioral Design Patterns (State, Chain of Responsibility)

A Java vending machine that sells six snacks. It uses two behavioral design patterns:

- **State**: the machine is always in one state: `Idle`, `WaitingForMoney`, or
  `DispensingSnack`. `VendingMachine` passes every action to its current state, and the
  state decides what happens next.
- **Chain of Responsibility**: a dispense request goes through a chain of handlers, one per
  snack: Coke, Pepsi, Cheetos, Doritos, KitKat, Snickers. Each handler dispenses its own
  snack or passes the request to the next handler.
