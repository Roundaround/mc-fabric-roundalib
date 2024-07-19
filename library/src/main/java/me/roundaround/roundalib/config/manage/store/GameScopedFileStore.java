package me.roundaround.roundalib.config.manage.store;

import me.roundaround.roundalib.util.PathAccessor;

import java.nio.file.Path;

public interface GameScopedFileStore extends FileBackedConfigStore {
  @Override
  default Path getConfigDirectory() {
    return PathAccessor.getInstance().getConfigDir();
  }
}
