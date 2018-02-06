package iconHK.experiment.model;


public class TrialDescriptor {
	
	public static int EMPTY_SPACE =1, NEGATIVE_SPACE =2, EDGES=3;
	
	private String target;
	private String hotkey;
	private int target_category;
	private int trialID;
	
	
	public TrialDescriptor(int id,String targ){
		this.trialID = id;
		this.target = targ;
	}
	
	public String getTarget(){
		return target;
	}
	
	public int getTrialID(){
		return trialID;
	}
}
