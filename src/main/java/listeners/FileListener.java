package listeners;

import java.io.File;

public interface FileListener {

  void fileFound(File file);

  void zipFileFound(String path);
}
