package iconHK.swing;

public class CommandSelection {
	public static int MENU_ITEM=1, TOOLBAR_BUTTON=2, HOTKEY=3;
	
	private String commandName;
	private int modality;
	
	public CommandSelection(String name, int mod){
		this.commandName = name;
		this.modality = mod;
	}
	
	public boolean isHotkey(){
		return (this.modality==HOTKEY);
	}
	
}
