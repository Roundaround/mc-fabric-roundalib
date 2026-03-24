package me.roundaround.roundalib.config.manage.store;

/**
 * A {@link FileBackedConfigStore} that never writes to disk. Intended for
 * legacy/migration stores that should only be read once to seed values into
 * a primary store via {@link FileBackedConfigStore#getLegacyStore()}.
 */
public interface ReadOnlyFileStore extends FileBackedConfigStore {
  @Override
  default void writeToStore() {
    // Read-only — intentionally no-op.
  }
}
