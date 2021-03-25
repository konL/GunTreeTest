
public class myfile{
    public static void newfunc() {
        Set<TaskResult> results=new HashSet<>();
        for(Task t:tasks){
            t.execute();
            results.add(t);
        }

    }


}