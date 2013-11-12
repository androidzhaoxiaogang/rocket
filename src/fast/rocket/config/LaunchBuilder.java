package fast.rocket.config;

import java.io.File;

public interface LaunchBuilder {
    
    public void launch(String uri);

    public void launch(String method, String url);

    public void  launch(File file);
}
