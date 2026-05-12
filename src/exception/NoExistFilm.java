// exception/NoExistFilm.java
package exception;
public class NoExistFilm extends Exception {
    public NoExistFilm(String filmCode) {
        super("Film with code '" + filmCode + "' does not exist.");
    }
}