package database;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DatabaseManagerTest {
  private DatabaseManager dbManager;

  @Before
  public void setUp() throws Exception {
    dbManager = DatabaseManager.getInstance(null);
  }

  @Test
  public void testAddDatabase() {
    dbManager.addDatabase(MongoDbManager.getInstance());
    assertTrue(dbManager.hasDatabaseManager(MongoDbManager.getInstance()));
    assertFalse(dbManager.hasDatabaseManager(MySqlManager.getInstance()));
  }

  @Test
  public void testRemoveDatabase() {
    dbManager.addDatabase(MongoDbManager.getInstance());
    assertTrue(dbManager.hasDatabaseManager(MongoDbManager.getInstance()));
    dbManager.removeDatabase(MongoDbManager.getInstance());
    assertFalse(dbManager.hasDatabaseManager(MongoDbManager.getInstance()));
  }
}
