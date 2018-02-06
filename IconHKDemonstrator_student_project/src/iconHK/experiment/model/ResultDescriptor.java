package iconHK.experiment.model;

public class ResultDescriptor {

	
	public static final int MOUSE_SELECTION=1, HK_SELECTION=2;
	
	
	//regarding user
	private int userNumber;
	
	// regarding condition
	private ConditionDescriptor condition;
	
	// regarding block
	private BlockDescriptor block;
	
	// regarding trial
	private TrialDescriptor trial;
	
	// regarding target
	private TargetDescription target;
	
	// regarding selection
	long time;
	int modality;
	boolean isCorrect;
	private String selectedHK;
	
	public ResultDescriptor(int usernumber, ConditionDescriptor condition, BlockDescriptor block, TrialDescriptor trial, TargetDescription target, String selectedHK,
			long time, int modality, boolean correct){
		this.userNumber=  usernumber;
		this.condition=condition;
		this.block=block;
		this.trial=trial;
		this.time=time;
		this.target=target;
		
		String[] spl=selectedHK.split(" ");
		this.selectedHK=spl[spl.length-1];
		this.modality=modality;
		this.isCorrect=correct;
	}
	
	public String toLine(){
		return System.currentTimeMillis()+","+userNumber+","+condition.getConditionID()+","+condition.getConditionOrder()+
				","+block.getBlockID()+","+block.getBlockType()+","+trial.getTrialID()+","+trial.getTarget()+","+target.getCategory()+","+selectedHK+","+time+","+modality+","+isCorrect;

	}
	
	
	public static String printHeader(){
		return "date,user,condition,conditionOrder,block,blocktype,trial,target,targetCategory,selected_hk,time,modality,isCorrect";
	}
	
}
