package auctionsniper.util;

public class Defect extends RuntimeException {
    public Defect(String reason) {
        super(reason);
    }
}
