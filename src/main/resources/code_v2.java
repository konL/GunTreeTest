
import javax.swing.*;
import java.awt.*;

public class Srccode extends JPanel {
    protected Instance m_Instance;
    protected Panel m_BaseInstPanel;
    //...
    public static void setInstance(Instance inst){
        m_Instance=inst;
        m_BaseInstPanel.setInstance(m_Instance);
        setBaseInstanceFromFileQ();
        //.......
    }
    public  static void setCaseInstance(){
        //..............
    }
    public void setBaseInstanceFromFileQ(){
        int as=0;
        int as2=1;
        Instance i=m_BaseInstance;
        setBaseInstances(new Instances(r));
        //....
    }
    public void setBaseInstancesFromDBQ(){
        int test=0;
        //......
    }
}