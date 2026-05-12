package model;

/**
 * Voucher – a discount code with a percentage off.
 * Stored in Data/vouchers.csv
 *
 * Format: voucherID|name|percentOff|minOrderAmount|active
 * Example: SUMMER25|Summer Sale 25%|25|100000|true
 */
public class Voucher {

    private final String  voucherId;
    private final String  name;
    private final double  percentOff;    // 0-100
    private final double  minOrderAmount;
    private final boolean active;

    public Voucher(String voucherId, String name,
                   double percentOff, double minOrderAmount, boolean active) {
        this.voucherId      = voucherId;
        this.name           = name;
        this.percentOff     = percentOff;
        this.minOrderAmount = minOrderAmount;
        this.active         = active;
    }

    public String  getVoucherId()      { return voucherId; }
    public String  getName()           { return name; }
    public double  getPercentOff()     { return percentOff; }
    public double  getMinOrderAmount() { return minOrderAmount; }
    public boolean isActive()          { return active; }

    /**
     * Apply this voucher to a total amount.
     * @return discounted total
     */
    public double apply(double total) {
        if (!active || total < minOrderAmount) return total;
        return total * (1.0 - percentOff / 100.0);
    }

    /**
     * How much money is saved.
     */
    public double discount(double total) {
        if (!active || total < minOrderAmount) return 0;
        return total * (percentOff / 100.0);
    }

    @Override
    public String toString() {
        return voucherId + " — " + name + " (" + (int)percentOff + "% off)";
    }
}
