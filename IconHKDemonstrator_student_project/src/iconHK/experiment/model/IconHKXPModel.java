package iconHK.experiment.model;


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import iconHK.experiment.view.IconHKXPModelListener;

public class IconHKXPModel{
	
	// change for  number of block per repetition (4 = 2 training,1test of each)
	public static final int moduloblock=3;
	
	// change for number of repetitions per condition
	// the number of block is repetitions x moduloblock
	public static final int repetitions=4;
	
	// so far
	// nbBlocks has to be a multiple of moduloblock where the first before last block 
	// is text test
	// the last is icon test
	// and all before that are training
	// is initialized in the initVar method based on  moduloblock and repetitions
	public static int nbBlocks;
	
	//variable to track the current repetition count
	int currentRepetition=1;
	
	public static final int trialReturn = 1, blockReturn=2, conditionReturn=3, endOfExperimentReturn=4;
	
	
	
	
	
	public int condition;
	public static int nbConditions;

	
	public int block;
	

	
	public int trial;
	public static int nbTrials;
	
	
	public boolean experimentHasStarted = false;
	public boolean experimentIsOver = false;
	
	
	private int userNumber=-1;
	
	private ArrayList<ConditionDescriptor> conditions;
	
	
	
	
	public IconHKXPModel(int userNumber){
		this.userNumber = userNumber;
		initVars();
		loadXPFromFiles(this.userNumber);
		
		
		
	}
	
	/**
	 * init all vars loading the experiment from files
	 */
	private void loadXPFromFilesOLD(int val) {
		int order = (val - 1) % 4;
		String orderPath = "./resources/data/order.txt";
		try {
			List<String> lines = Files.readAllLines(Paths.get(orderPath), Charset.defaultCharset());
			String orderDescription =  lines.get(order);
			String[] splitTemp = orderDescription.split(",");
			String firstPart[] = splitTemp[0].split("#");
			String secondPart[] = splitTemp[1].split("#");
			int condition1 = Integer.parseInt(""+firstPart[0].charAt(1));
			int condition2 = Integer.parseInt(""+secondPart[0].charAt(1));

			int iconSet1 = Integer.parseInt(""+firstPart[1].charAt(1));
			int iconSet2 = Integer.parseInt(""+secondPart[1].charAt(1));
			
			initModelWith(condition1,iconSet1,condition2,iconSet2);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
	/**
	 * init all vars loading the experiment from files
	 */
	private void loadXPFromFiles(int val) {

		initModel();
	}
	
	
	//TOOD attention ici on a changé pour avoir un seul iconset
	private void initModel(){
		conditions = new ArrayList<ConditionDescriptor>();
		ConditionDescriptor condition1 = new ConditionDescriptor(1,1, 1);
		conditions.add(condition1);
	}
	
	
	
	//TOOD attention ici on a changé pour avoir un seul iconset
	private void initModelWith(int firstCondition, int firstIconSet, int secondCondition, int secondIconSet){
		conditions = new ArrayList<ConditionDescriptor>();
		ConditionDescriptor condition1 = new ConditionDescriptor(firstCondition, firstIconSet, 1);
		conditions.add(condition1);
		ConditionDescriptor condition2 = new ConditionDescriptor(secondCondition, secondIconSet, 2);
		conditions.add(condition2);
	}
	
	
	public void runAUTO(){
		experimentHasStarted = true;
		fireStart(trial,block,condition);
		int isOver = 0;
		while(isOver!=endOfExperimentReturn){
			isOver = doNextTrial(true);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		experimentIsOver = true; 
		fireFinish();
	}
	
	public void run(){
		experimentHasStarted = true;
		fireStart(trial,block,condition);
	}
	
	
	
	public void doFromClick(boolean bool){
		int isOver = doNextTrial(bool);
		if(isOver==endOfExperimentReturn){
			fireFinish();
		}
	}
	
	private int doNextTrial(boolean correct){
		int result = nextTrial();
		//System.out.println("condition: "+condition+" block: "+block+" trial: "+trial);
		return result;
	}
	
	
	
	
	private void initVars(){
		condition=0;
		block=0;
		trial=0;

		
		nbBlocks=repetitions*moduloblock;
	}
	
	
	
	/**
	 * move on to next condition
	 */
	public int nextCondition(){
		condition++;
		if(condition>=conditions.size()){
			return endOfExperimentReturn;
		}
		else {
			fireNextCondition(trial,block,condition);
			return conditionReturn;
		}
	}
	
	/**
	 * move on to next block
	 * @return
	 */
	public int nextBlock(){
		block++;
		ConditionDescriptor cond = conditions.get(condition);
		if(block>=cond.getBlocks().size()){
			block=0;
			return nextCondition();
		}
		else {
			fireNextBlock(trial,block,condition);
			return blockReturn;
		}
	}
	
	
	
	/**
	 * move on to next trial
	 * @return
	 */
	public int nextTrial(){
		trial++;
		BlockDescriptor currentBlock = conditions.get(condition).getBlocks().get(block);
		if(trial>=currentBlock.getTrials().size()){
			trial =0;
			return nextBlock();
		}
		else {
			fireNextTrial(trial,block,condition);
			return trialReturn;
		}
		
	}
	
	
	

	 public ArrayList<ConditionDescriptor> getConditions(){
		 return this.conditions;
	 }
	 
	 public String getCurrentTarget(){
		 return this.conditions.get(condition).getBlocks().get(block).getTrials().get(trial).getTarget();
	 }
	
	
	/**
	 * Method pour gerer les evenements
	 */
	private List<IconHKXPModelListener> listeners = new ArrayList<IconHKXPModelListener>();
	
	public void addIconHKXPModelListener(IconHKXPModelListener toAdd) {
	        listeners.add(toAdd);
	    }
	
	
	
	 public void fireStart(int trial, int block, int conditionID) {
	        // Notify everybody that may be interested.
	        for (IconHKXPModelListener list : listeners)
	           list.start(trial,block,conditionID);
	    }
	
	 public void fireFinish() {
	        // Notify everybody that may be interested.
	        for (IconHKXPModelListener list : listeners)
	           list.finish();
	    }
	
	 public void fireNextTrial(int trial, int block, int conditionID) {
	        // Notify everybody that may be interested.
	        for (IconHKXPModelListener list : listeners)
	           list.nextTrial(trial,block,conditionID);
	    }
	
	 public void fireNextBlock(int trial, int block, int conditionID) {
	        // Notify everybody that may be interested.
	        for (IconHKXPModelListener list : listeners)
	           list.nextBlock(trial,block,conditionID);
	    }
	 
	 public void fireNextCondition(int trial, int block, int conditionID) {
	        // Notify everybody that may be interested.
	        for (IconHKXPModelListener list : listeners)
	           list.nextCondition(trial,block,conditionID);
	    }
	 
	 
	 public int getUserNumber(){
		 return this.userNumber;
	 }
		
}
