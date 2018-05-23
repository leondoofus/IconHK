package iconHK.experiment.model;

public class TargetDescription {

	private String name;
	private String hotkey;
	private int category;
	
	
	public TargetDescription(String name, String hotkey, int cat){
		this.name = name;
		this.hotkey=hotkey;
		this.category=cat;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getHotkey() {
		return hotkey;
	}


	public void setHotkey(String hotkey) {
		this.hotkey = hotkey;
	}


	public int getCategory() {
		return category;
	}


	public void setCategory(int category) {
		this.category = category;
	}
	
}
