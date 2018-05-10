package com.fgrlnd.dvd.propeller;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by david on 3/28/18.
 */

@Dao
public interface AppDao {
    @Query("SELECT * FROM app")
    List<App> getApps();

    @Query("SELECT * FROM app WHERE packageName LIKE :pkg")
    App getApps(String pkg);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long[] insert(App... apps);

    @Update
    void updateApp(App... app);

    @Delete
    void deleteApp(App... app);
}
