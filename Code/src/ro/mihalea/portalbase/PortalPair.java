package ro.mihalea.portalbase;

/**
 * Created by Mircea on 19-Jun-14.
 */
public class PortalPair {
    private Portal overworld, nether;
    private String name;
    transient private String errorMessage = "";

    public PortalPair(String name, Portal overworld, Portal nether) {
        this.name = name;
        this.overworld = overworld;
        this.nether = nether;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Portal getOverworld() {
        return overworld;
    }

    public void setOverworld(Portal overworld) {
        this.overworld = overworld;
    }

    public Portal getNether() {
        return nether;
    }

    public void setNether(Portal nether) {
        this.nether = nether;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
