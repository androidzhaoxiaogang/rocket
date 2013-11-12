package fast.rocket.config;

import java.io.File;

public interface LaunchBuilder {
    
    public void load(String uri);

    public void load(String method, String url);

    public void  load(File file);
}
