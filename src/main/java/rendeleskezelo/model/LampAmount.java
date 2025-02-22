package rendeleskezelo.model;

public class LampAmount {
    Lamp lamp;
    double amount;

    public LampAmount(Lamp lamp, double amount) {
        this.lamp = lamp;
        this.amount = amount;
    }

    public Lamp getLamp() {
        return lamp;
    }

    public void setLamp(Lamp lamp) {
        this.lamp = lamp;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
