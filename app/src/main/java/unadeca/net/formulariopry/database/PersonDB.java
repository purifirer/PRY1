package unadeca.net.formulariopry.database;


import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = PersonDB.NAME, version = PersonDB.VERSION)
public class PersonDB {
    public static final String NAME = "PersonDB";
    public static final int VERSION = 1;
}
