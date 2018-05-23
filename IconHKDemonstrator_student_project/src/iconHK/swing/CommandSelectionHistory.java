package iconHK.swing;


import java.util.Vector;

public class CommandSelectionHistory {
	public Vector<CommandSelection> commandSelections;
	
	public CommandSelectionHistory(){
		this.commandSelections = new Vector<CommandSelection>();
	}
	
	public void addCommandSelection(CommandSelection cs){
		this.commandSelections.add(cs);
	}
}
