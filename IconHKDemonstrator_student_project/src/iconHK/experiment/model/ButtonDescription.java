package iconHK.experiment.model;


public class ButtonDescription {

	private String name;
	private String hotkey;
	
	private String trainingIconPath=null;
	private String testIconPath=null;
	
	private int targetType;
	
	
	public ButtonDescription(String name, String hotkey,String trainPath, String testPath, int targetType){
		this.name = name;
		this.hotkey=hotkey;
		this.trainingIconPath= trainPath;
		this.testIconPath = testPath;
		this.targetType = targetType;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getHotkey(){
		return this.hotkey;
	}
	
	public String getTrainingIconPath(){
		return this.trainingIconPath;
	}
	
	public String getTestIconPath(){
		return this.testIconPath;
	}
	
	public int getTargetType(){
		return this.targetType;
	}
	
}
