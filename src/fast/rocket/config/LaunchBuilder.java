package fast.rocket.config;

public interface LaunchBuilder {
    
    public void load(String uri);

    public void load(int method, String uri);
}
