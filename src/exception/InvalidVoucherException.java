// exception/InvalidVoucherException.java
package exception;
public class InvalidVoucherException extends Exception {
    public InvalidVoucherException(String code) {
        super("Voucher '" + code + "' is invalid or expired.");
    }
}