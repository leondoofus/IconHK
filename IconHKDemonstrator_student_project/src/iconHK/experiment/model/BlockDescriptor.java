package iconHK.experiment.model;


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockDescriptor {
	public static final String[] blockNames= {"Training","Test text", "Test icon"}; 
	public static final int TRAINING_BLOCK=0, TEXT_TEST_BLOCK=1, ICON_TEST_BLOCK=2;
	
	ArrayList<TrialDescriptor> trials;
	private int blockType;
	private int blockID;
	private int iconSet;
	
	
	public BlockDescriptor(int blockID, int icons){
		this.iconSet = icons;
		this.blockID=blockID;
		int temp = (blockID+1)%IconHKXPModel.moduloblock;
		if(temp==0){
			this.blockType=ICON_TEST_BLOCK;
		}
		else if(temp==(IconHKXPModel.moduloblock-1)){
			this.blockType=TEXT_TEST_BLOCK;
		}
		else {
			this.blockType=TRAINING_BLOCK;
		}
		initTrialsForIconSet(iconSet);
	}
	
	
	private void initTrialsForIconSet(int iconSet){
		String iconSetPath = "./resources/data/-targetset"+iconSet+".txt";
		List<String> targets = null;
		try {
			targets = Files.readAllLines(Paths.get(iconSetPath), Charset.defaultCharset());
			trials = new ArrayList<TrialDescriptor>();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Collections.shuffle(targets);
		
		for (int i=0; i<targets.size();i++){
			trials.add(new TrialDescriptor(i,targets.get(i)));
		}
	}
	
	/**
	 * @return the blocktype, that is 0 for training, 1 for text and 2 for icon
	 */
	public int getBlockType(){
		return this.blockType;
	}
	
	/**
	 * @return the absolute blockID, that is 0,1,2, etc.
	 */
	public int getBlockID(){
		return this.blockID;
	}
	
	/**
	 * @return the block type name
	 */
	public String getBlockTypeName(){
		return blockNames[this.blockType];
	}
	
	public ArrayList<TrialDescriptor> getTrials(){
		return this.trials;
	}
	
}
