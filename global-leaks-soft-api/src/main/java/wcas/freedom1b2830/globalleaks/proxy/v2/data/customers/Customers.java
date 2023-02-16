package wcas.freedom1b2830.globalleaks.proxy.v2.data.customers;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Customers")
public class Customers {
	public @DatabaseField(id = true) Long id;
	public @DatabaseField() String FN;
	public @DatabaseField() String LN;
	public @DatabaseField() String lang;

}
