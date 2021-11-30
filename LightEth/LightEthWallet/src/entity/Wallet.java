package entity;

public class Wallet {

	public String currency;
	
	public String name;
	
	public String created_at;
	
	public String updated_at;

	public String oid;

	public String current_address;

	public String password;
	
	public Wallet(String currency, String name, String created_at, String updated_at, String oid,
			String current_address) {
		this.currency = currency;
		this.name = name;
		this.created_at = created_at;
		this.updated_at = updated_at;
		this.oid = oid;
		this.current_address = current_address;
		this.password = password;
	}
	

}
