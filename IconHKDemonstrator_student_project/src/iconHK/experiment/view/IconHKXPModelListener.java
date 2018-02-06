package iconHK.experiment.view;

public interface IconHKXPModelListener {

	
	/**
	 * experiment has just started
	 */
	void start(int trial, int block, int conditionID);
	
	/**
	 * experiment has just finished
	 */
	void finish();
	
	/**
	 * next trial
	 * @param trial
	 */
	void nextTrial(int trial, int block, int conditionID);
	
	/**
	 * next Block
	 */
	void nextBlock(int trial, int block, int conditionID);
	
	/**
	 * next Condition
	 */
	void nextCondition(int trial, int block, int conditionID);
	
	
	
}
