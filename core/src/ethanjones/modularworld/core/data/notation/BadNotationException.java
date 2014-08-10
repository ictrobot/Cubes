package ethanjones.modularworld.core.data.notation;

public class BadNotationException extends RuntimeException {
  public BadNotationException(String notation) {
    super(notation);
  }

  public BadNotationException(String notation, Exception e) {
    super(notation, e);
  }
}
