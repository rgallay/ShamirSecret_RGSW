import java.math.BigInteger;

public class PartSecret {

    private int x;
    private BigInteger y;

    public PartSecret(int x, BigInteger y) {
        this.x = x;
        this.y = y;
    }

    public int getX(){
        return x;
    }

    public BigInteger getY() {
        return y;
    }

    @Override
    public String toString()
    {
        return "Part du secret: [x:" + x + ", y:" + y + "]";
    }


}
