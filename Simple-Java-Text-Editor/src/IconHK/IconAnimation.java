package IconHK;

public class IconAnimation {
    public static final int ICON_STEP=1, DEFAULT_STEP=2, HOTKEY_STEP=3, OVER_STEP=0;

    private HKButton button;
    private int[] states;

    private int animationStep;
    private int persistency; // in frames
    private int currentPersistency;


    public IconAnimation(HKButton button, int[] states, int persistency){
        this.button = button;
        this.states = states;
        this.animationStep = 0;
        this.persistency = persistency;
        this.currentPersistency = 0;
    }

    public IconAnimation(HKButton button, int[] states){
        this.button = button;
        this.states = states;
        this.animationStep = 0;
        this.persistency = 0;
        this.currentPersistency = 0;
    }

    public HKButton getButton(){
        return this.button;
    }

    public int[] getStates(){
        return this.states;
    }

    public int getPersistency(){
        return this.persistency;
    }

    public int getAnimationStep(){
        return this.animationStep;
    }

    public void nextAnimationStep(){
        this.animationStep++;
    }

    public int getCurrentObjective(){
        if(animationStep<states.length){
            return states[animationStep];
        }
        else {
            return IconAnimation.OVER_STEP;
        }
    }

    public void increaseCurrentPersistency(){
        this.currentPersistency++;
    }

    public boolean hasToPersist(){
        return (this.currentPersistency<this.persistency);
    }

}
