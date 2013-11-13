package fast.rocket.config;

import java.io.File;

public interface LaunchBuilder {
    
    public void load(String uri);

    public void load(int method, String uri);

    public void  load(File file);
}
