package eliteapps.SOSBattery.json;

/**
 * Created by Rodrigo on 27/02/2016.
 */
public class Periods {
    private Open open;

    private Close close;

    public Open getOpen() {
        return open;
    }

    public void setOpen(Open open) {
        this.open = open;
    }

    public Close getClose() {
        return close;
    }

    public void setClose(Close close) {
        this.close = close;
    }

    @Override
    public String toString() {
        return "ClassPojo [open = " + open + ", close = " + close + "]";
    }
}

