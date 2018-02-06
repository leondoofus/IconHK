package iconHK.experiment.model;


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import java.util.List;

public class ConditionDescriptor {

	
	public static final String[] conditionNames = {"Control","IconHK"};
	//conditionID, aka its number, aka 1 for control and 2 for IconHK 
	private int conditionID;
	//conditionOrder, aka the order it appears in the experiment (first or second)
	private int conditionOrder;
	// icon set id
	private int iconSet;
	

	ArrayList<BlockDescriptor> blocks;
	
	ArrayList<ButtonDescription> buttonsInToolbar = null;
	
	
	
	public ConditionDescriptor(int condition, int iconSet, int order){
		this.conditionID=condition;
		this.conditionOrder=order;
		this.iconSet = iconSet;
		int nbblock = IconHKXPModel.nbBlocks;

		
		initButtons();
		
		if ((nbblock%IconHKXPModel.moduloblock)!=0){
			System.err.println("problem with the number of blocks");
			System.exit(-1);
		}
		else {
			blocks = new ArrayList<BlockDescriptor>();
			for(int i=0; i<nbblock; i++){
				BlockDescriptor block = new BlockDescriptor(i,iconSet);
				blocks.add(block);
			}
		}
	}
	
	
	private void initButtons(){
		String iconSetPath = "./resources/data/-iconset"+iconSet+".txt";
		List<String> buttons = null;
		
		try {
			buttons = Files.readAllLines(Paths.get(iconSetPath), Charset.defaultCharset());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		buttonsInToolbar = new ArrayList<ButtonDescription>();
		for(String s: buttons){
			String [] split= s.split(",");
			String hotkey = null;
			String testPath=null;
			String trainPath=null;
			int targetType = -1;
//			if(conditionID==2){
//				if(split.length>1){
//					hotkey=split[1];
//					targetType = Integer.parseInt(split[2]);
//					testPath = "./resources/icons/xp/-set"+iconSet+"/"+split[0]+"2.png";
//					trainPath = "./resources/icons/xp/-set"+iconSet+"/"+split[0]+"1.png";
//				}
//				else {
//					testPath = "./resources/icons/xp/-set"+iconSet+"/"+split[0]+".png";
//					trainPath = "./resources/icons/xp/-set"+iconSet+"/"+split[0]+".png";
//				}
//			}
//			else {
//				if(split.length>1){
//					hotkey=split[1];
//					targetType = Integer.parseInt(split[2]);
//					testPath = "./resources/icons/xp/-set"+iconSet+"/"+split[0]+"2.png";
//					trainPath = "./resources/icons/xp/-set"+iconSet+"/"+split[0]+"2.png";
//				}
//				else {
//					testPath = "./resources/icons/xp/-set"+iconSet+"/"+split[0]+".png";
//					trainPath = "./resources/icons/xp/-set"+iconSet+"/"+split[0]+".png";
//				}
//			}
			if(split.length>1){
				hotkey=split[1];
				targetType = Integer.parseInt(split[2]);
				testPath = "./resources/icons/xp/-set"+iconSet+"/"+split[0]+"2.png";
				trainPath = "./resources/icons/xp/-set"+iconSet+"/"+split[0]+"1.png";
			}
			
			buttonsInToolbar.add(new ButtonDescription(split[0],hotkey,trainPath,testPath,targetType));
		}
	}
	
	
	
	public String getConditionName(){
		if(conditionID!=0){
			return conditionNames[conditionID-1];
		}
		else {
			return "problem";
		}
		
	}
	
	/**
	 * @return the condition ID, aka 1 for control and 2 for IconHK
	 */
	public int getConditionID(){
		return this.conditionID;
	}
	
	/**
	 * @return the condition order, that is if it appeared first
	 * or second during the experiment
	 */
	public int getConditionOrder(){
		return this.conditionOrder;
	}
	
	public int getIconSet(){
		return this.iconSet;
	}
	
	public ArrayList<BlockDescriptor> getBlocks(){
		return this.blocks;
	}
	
	
	public ArrayList<ButtonDescription> getButtons(){
		return this.buttonsInToolbar;
	}
	
}
