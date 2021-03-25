package Do;

import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.client.Run;
import com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
import com.github.gumtreediff.io.TreeIoUtils;
import com.github.gumtreediff.matchers.*;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.TreeContext;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Pattern;


import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import model.changeContext;
import model.idObj;

import javax.swing.text.ChangedCharSetException;


public class test_GumTree2 {
    static List<changeContext> clst=null;
    static boolean isput=false;
    static List<String>  fieldsName;
    static List<String> methodName;
    static List<String> variableName;
    static List<String> callSet;
    //获取declaration
    static Map<String, FieldDeclaration> fieldMap=JavaParserUtils.fieldMap;
    static Map<String, MethodDeclaration> methodMap=JavaParserUtils.methodMap;
    static Map<String, VariableDeclarationExpr> variableMap=JavaParserUtils.variableMap;
    static Map<String, String> callMap=JavaParserUtils.nameExprMap;

    static List<String>  fieldsName_new;
    static List<String> methodName_new;
    static List<String> variableName_new;
    static List<String> callSet_new;
    //获取declaration
    static Map<String, FieldDeclaration> fieldMap_new=JavaParserUtils.fieldMap;
    static Map<String, MethodDeclaration> methodMap_new=JavaParserUtils.methodMap;
    static Map<String, VariableDeclarationExpr> variableMap_new=JavaParserUtils.variableMap;
    static Map<String, String> callMap_new=JavaParserUtils.nameExprMap;

    public static void main(String[] args) throws Exception {
        /*
        比较所有项目
       每两个版本比较一次
         */
        String[] projectName={"TestSet"};
        //1.取出项目名称
        for(String name:projectName) {

            int index=1;
            //2.取出该项目的每一个版本
            String[] version = findVer(name);
            for (int i = 0; i < version.length - 1; i++) {
                //3.每两个版本一组，并且标记上index，1+2=index1，2+3=index2 以此类推
                String v1 = version[i];
                String v2 = version[i + 1];
                //4.取出项目中的每个文件，一组版本之间传入ProjectDiff比较
                String FileIndex = "D:\\project\\IdentifierStyle\\data\\JavaFileIndex\\" + name + ".txt";
                //根据FileIndex读取
                //line:源代码文件位置,只取最后一个字段<xx.java>
                BufferedReader br = new BufferedReader(new FileReader(FileIndex));
                String line = "";
                while ((line = br.readLine()) != null) {
                    String filename= line.substring(line.indexOf("ver_final") + "ver_final".length() + 2, line.length());

//                    String srcFile = "D:\\project\\IdentifierStyle\\data\\GitProject\\"+name+"\\"+v1+"\\"+filename;
//                    String dstFile = "D:\\project\\IdentifierStyle\\data\\GitProject\\"+name+"\\"+v2+"\\"+filename;
//                    File oldf=new File(srcFile);
//                    File newf=new File(dstFile);
//                    if(!oldf.exists() || !newf.exists()){
//                        continue;
//                    }
                    System.out.println(index+" "+v1+" "+v2+" "+filename);
                    Projectdiff(name, v1, v2,filename,index);

                }
                index++;



                   br.close();


            }
        }

    }


    /*
    获取每个项目的版本号
     */
    private static String[] findVer(String projectpath) {
        List<String> list=new ArrayList<>();
        //进入到项目文件夹
        //遍历文件夹中的版本文件夹并读取
        //要遍历的路径
        File file = new File("D:\\project\\IdentifierStyle\\data\\GitProject\\"+projectpath);		//获取其file对象
        File[] fs = file.listFiles();	//遍历path下的文件和目录，放在File数组中
        for(File f:fs){					//遍历File[]数组
            if(f.isDirectory())		//若非目录(即文件)，则打印
                list.add(f.getName());

        }
        String[] version = list.toArray(new String[0]);
        return version;
    }

    public static void Projectdiff(String projectname,String oldVer,String newVer,String filename,int index) throws Exception {
        //测试源代码文件
//        String srcFile = "D:\\kon_data\\JAVA_DATA\\GunTreeTest\\src\\main\\resources\\code_v1.java";
//        String dstFile = "D:\\kon_data\\JAVA_DATA\\GunTreeTest\\src\\main\\resources\\code_v2.java";
//        String srcFile = "D:\\kon_data\\JAVA_DATA\\GunTreeTest\\src\\main\\resources\\funfile_old.java";
//        String dstFile = "D:\\kon_data\\JAVA_DATA\\GunTreeTest\\src\\main\\resources\\funcfile_new.java";
//        String srcFile = "D:\\kon_data\\JAVA_DATA\\GunTreeTest\\src\\main\\resources\\myfile.java";
//        String dstFile = "D:\\kon_data\\JAVA_DATA\\GunTreeTest\\src\\main\\resources\\myfile02.java";

        /*
        srcFile:历史文件位置
        dstFile：比较文件位置
         */
        String srcFile = "D:\\project\\IdentifierStyle\\data\\GitProject\\"+projectname+"\\"+oldVer+"\\"+filename;
        String dstFile = "D:\\project\\IdentifierStyle\\data\\GitProject\\"+projectname+"\\"+newVer+"\\"+filename;

        /*
        （一）比较oldVer和newVer的不同，存储为changeContext
         List<changeContext> Ctx
         */
        List<changeContext> Ctx = new ArrayList<>();

        ITree rootSpoonLeft = new JdtTreeGenerator().generateFromFile(srcFile).getRoot();
        ITree rootSpoonRight = new JdtTreeGenerator().generateFromFile(dstFile).getRoot();

        final MappingStore mappingsComp = new MappingStore();
        final Matcher matcher = new CompositeMatchers.ClassicGumtree(rootSpoonLeft, rootSpoonRight, mappingsComp);
        matcher.match();

        final ActionGenerator actionGenerator = new ActionGenerator(rootSpoonLeft, rootSpoonRight,
                matcher.getMappings());
        actionGenerator.generate();
        List<Action> actions = actionGenerator.getActions();

        for(Action act : actions) {
            //格式化输出方式
            //Optype,ASTNodeType,Label,content
            String Optype=act.getName();
            System.out.println(index+"】OperationType : "+Optype);
            index++;
            TreeContext tc1=new JdtTreeGenerator().generateFromFile(srcFile);
            TreeContext tc2=new JdtTreeGenerator().generateFromFile(dstFile);
            String type=null;
            Pattern pattern = Pattern.compile("[0-9]*");
            if(!pattern.matcher(tc1.getTypeLabel(act.getNode())).matches()){
                type=tc1.getTypeLabel(act.getNode());



            }else{

                type=tc2.getTypeLabel(act.getNode());

            }

            System.out.println("AST Node Type : "+ type);
            String label=null;
            if(act.getNode().getLabel()==""){
                label=type;
            }
            else{
                label=act.getNode().getLabel();

            }
//            if(type.equals("SimpleName")){
//                label=type;
//            }
            System.out.println("Label : "+label);
            String content=act.getNode().toTreeString();

            System.out.println("content : "+ content);
            changeContext c=new changeContext(Optype,type,label,content);
            Ctx.add(c);

        }

        /*
        （二）把entity与changeContext关联起来
         LinkedHashMap<String,List<changeContext>> id_Ctx_map
         */
        LinkedHashMap<String,List<changeContext>> id_Ctx_map=new LinkedHashMap<>();
        //获取所有type，用于剔除
        List<String> xType=getType(Ctx);
        //获取changeContext对应的实体 存入id_Ctx_map
        for(changeContext c:Ctx){
            ctx2entity(c,id_Ctx_map,xType);
        }
        //异常处理
        if(clst!=null && isput==false){
            for(changeContext cs:clst) {
                getTail(id_Ctx_map).getValue().add(cs);
            }
        }
        //打印id_Ctx_map
        for(String key:id_Ctx_map.keySet()){
            System.out.println("【"+key+"】");
            List<changeContext> list=id_Ctx_map.get(key);

            for(changeContext cx:list){
                System.out.println(cx.getOperaterType());
                System.out.println(cx.getASTNode_Type());
                System.out.println(cx.getContent());
            }
        }
//        /* （三）得到版本v1和v2中所有变化的标识符
//         */
//        List<String> changeId=findChange(projectname,index);
//        /*（四）
//        利用JavaParser寻找与变化标识符 c 有关的实体entity，根据这个entity获取对应的change context
//         */
//        //从Parser中获取变量名和方法名
//        Map<String,List> map=new HashMap<>();
//        map=JavaParserUtils.getData(srcFile);
//        fieldsName=map.get("fields_name");
//        methodName=map.get("method_name");
//        variableName=map.get("variable_name");
//        callSet=map.get("call_relation");
//        Map<String,List> map_new=new HashMap<>();
//        map_new=JavaParserUtils.getData(dstFile);
//        fieldsName_new=map_new.get("fields_name");
//        methodName_new=map_new.get("method_name");
//        variableName_new=map_new.get("variable_name");
//        callSet_new=map_new.get("call_relation");
//
//
//        Map<String,List<changeContext>> changePattern_corpus=findRes(changeId,id_Ctx_map);
//        System.out.println(changePattern_corpus.size());
//        for(String key:changePattern_corpus.keySet()){
//            System.out.println("【"+key+"】");
//            List<changeContext> listtest=changePattern_corpus.get(key);
//            for(changeContext c:listtest){
//                System.out.println(c.getOperaterType());
//                System.out.println(c.getASTNode_Type());
//                System.out.println(c.getLabel());
//                System.out.println(c.getContent());
//
//
//            }
//        }
//



    }

    private static Map<String, List<changeContext>> findRes(List<String> changeId, LinkedHashMap<String, List<changeContext>> id_ctx_map) {
        Map<String, List<changeContext>> codePattern=new LinkedHashMap<>();
        //entity相同
        //相关entity

        for(String id:changeId){
            //取出有变化的标识符，和chnageContext关联的标识符进行比较,line表示old_代码文件
            String old=id.split("<-")[1];
            String news =id.split("<-")[0];
            List<String> res=obtainRes(old,news);
            System.out.println("res的大小为："+res.size());
            for(String s:res) {
                System.out.println(s);
            }
            for(String resid:res){
                if(id_ctx_map.get(resid)!=null){
                    codePattern.put(resid,id_ctx_map.get(resid));
                }
            }


        }
        return codePattern;
    }

    private static List<String> obtainRes(String old,String news) {
        List<String> res=new ArrayList<>();
        //相同的加入
        res.add(old);
        //加入相关的

        Set<String> set=new HashSet<>();


        //-------------------------------方法二-----------------------------
        //1.直接包含的实体--
        //1.1如果是函数,检测调用该函数的软件实体
        if(methodName.contains(old)){
            MethodDeclaration m=methodMap.get(old);
            String[] data=JavaParserUtils.getParents(m).split("\\.");
            for(String s:data){
                if(!s.equals("")){
                    set.add(s);
                }
            }



        }
        if(methodName_new.contains(news)){
            MethodDeclaration m=methodMap.get(news);
            String[] data=JavaParserUtils.getParents(m).split("\\.");
            for(String s:data){
                if(!s.equals("")){
                    set.add(s);
                }
            }



        }
        //1.1如果是函数,检测该函数调用的的软件实体
        for(String nameExpr:callSet){
            String parent=callMap.get(nameExpr);
            if(parent.substring(parent.lastIndexOf(".")+1).equals(old)){
                if (!nameExpr.equals("")) {
                    set.add(nameExpr);
                }
            }
        }
        for(String nameExpr:callSet_new){
            String parent=callMap_new.get(nameExpr);


            if(parent.substring(parent.lastIndexOf(".")+1).equals(news)){
                if (!nameExpr.equals("")) {
                    //System.out.println("nameExprAdd to set==========");
                    set.add(nameExpr);
                }
            }
        }

        //1.2.如果是全局变量，方法中的变量包含e，或者是其他类使用
        if(fieldsName.contains(old)){

            FieldDeclaration f=fieldMap.get(old);
            String[] data=JavaParserUtils.getParents(f).split("\\.");

            for(String s:data) {
                if (!s.equals("")) {
                    set.add(s);
                }
            }

        }
        if(fieldsName_new.contains(news)){

            FieldDeclaration f=fieldMap_new.get(news);
            String[] data=JavaParserUtils.getParents(f).split("\\.");

            for(String s:data) {
                if (!s.equals("")) {
                    set.add(s);
                }
            }

        }
        //1.3.如果是局部变量，包含该变量的方法/静态方法还会被调用红
        if(variableName.contains(old.substring(1,old.length()-1))){
            VariableDeclarationExpr v=variableMap.get(old.substring(1,old.length()-1));
            String[] data=JavaParserUtils.getParents(v).split("\\.");
            for(String s:data){
                if(!s.equals("")){
                    set.add(s);
                }
            }

        }
        if(variableName_new.contains(news.substring(1,news.length()-1))){
            VariableDeclarationExpr v=variableMap_new.get(old.substring(1,old.length()-1));
            String[] data=JavaParserUtils.getParents(v).split("\\.");
            for(String s:data){
                if(!s.equals("")){
                    set.add(s);
                }
            }

        }
        //1.4 如果
        if(callSet.contains(old.substring(1,old.length()-1))){


            //特殊处理：
            //<m_Baseinstance,src1.src2>

            String[] data=callMap.get(old.substring(1,old.length()-1)).split("\\.");
            for(String s:data){
                if(!s.equals("")){
                    set.add(s);
                }
            }

        }
        if(callSet_new.contains(news.substring(1,news.length()-1))){


            //特殊处理：
            //<m_Baseinstance,src1.src2>

            String[] data=callMap_new.get(news.substring(1,news.length()-1)).split("\\.");
            for(String s:data){
                if(!s.equals("")){
                    set.add(s);
                }
            }

        }

/*NameExpr:m_BaseInstance
MethodCallExpr:setBaseInstances*/
        //2.sibling 类Srccode的所有字段和方法（先不考虑吧）
        //3.直接访问这个字段的方法
        //先生成数据
        for(String s:set){
            res.add(s);
        }


        return res;
    }

    public static List<String> findChange(String project, int index) throws IOException {
        //根据commitMessage搜索变化标识符，加上手工确定
        //现在这个函数名称随便起的，不重要
        //先假设直接赋值数据
        List<String> changeId=new ArrayList<>();
        //模板一
        changeId.add("newfunc<-oldfunc");
        //模板2
        changeId.add("func<-func1");
        changeId.add("func<-func2");
        changeId.add("m_Instancec<-m_BaseInstance");
        changeId.add("setInstance<-setBaseInstance");

        //1.记录了标识符变化的文件
        String changeFile ="D:\\project\\IdentifierStyle\\log\\dump\\"+project+".csv";
        //2.读取csv文件的第四列之后
        //代码行数，代码行数，位置，历史变化情况，之后每一列代表每两个版本的变化，无变化则（<-
        BufferedReader br = new BufferedReader(new FileReader(changeFile));
        String line="";
        while((line=br.readLine())!=null){
            String[] b = line.split(",");
            System.out.println(b[3]);
            changeId.add(b[3+index]);
        }
        br.close();
        return changeId;

    }

    private static List<String> getType(List<changeContext> ctx) {
        List<String> t=new ArrayList<>();
        for(changeContext c:ctx){
            if(c.getASTNode_Type().endsWith("Type")){
                t.add(c.getLabel());
            }
        }
        return t;
    }



    private static void ctx2entity(changeContext c, LinkedHashMap<String, List<changeContext>> map, List<String> xType) {

        //label=xxStatement 则创建一个List

        String astType=c.getASTNode_Type();
        if((astType.indexOf("Statement")!=-1 ) || astType.endsWith("Declaration")){
            //System.out.println(c.getLabel()+"===>"+c.getContent());
            if(isput || clst==null) {
                clst = new ArrayList<>();
                clst.add(c);
                isput=false;
            }else{
                //还没加进map并且clst非空，就跟着上一个
                //如果map没有最后一个
                for(changeContext cs:clst) {
                   if(map.size()!=0) {

                       getTail(map).getValue().add(cs);
                   }

                }
                //isput=true;
                clst = new ArrayList<>();
                clst.add(c);
            }


        }
        else if(astType.equals("SimpleName")){

            int nameIndex=c.getContent().trim().lastIndexOf("@")+1;
            String name=c.getContent().trim().substring(nameIndex);
            System.out.println(c.getOperaterType()+"===>"+astType+"===>"+name);
            //已经含有这个实体的changecode组，继续增加，否则创建
            if(!xType.contains(name)) {
                String id = name;
                if (map.containsKey(id)) {
//                    System.out.println("map已有"+id+clst.size());
//                    if (clst !=null) {
//                            for (changeContext cc : clst) {
//                                map.get(id).add(cc);
//                            }
//
//
//                    }
                    map.get(id).add(c);
//                    clst=null;
                }
                else {

////                    if (clst != null) {
////                        isput=true;
////                    }else{
                        isput=true;
                        if(clst==null) {
                            clst = new ArrayList<>();
                        }
                        clst.add(c);
////                    }
                    map.put(id, clst);
                }
            }
        }
        else{
            if(clst!=null && (c.getLabel()==c.getASTNode_Type())){
                clst.add(c);
            }
        }






    }



    public static <K, V> Map.Entry<K, V> getTail(LinkedHashMap<K, V> map) {
        Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
        Map.Entry<K, V> tail = null;
        while (iterator.hasNext()) {
            tail = iterator.next();
        }
        return tail;
    }
    public static void test() throws IOException {
            Run.initGenerators();
            String srcFile = "D:\\kon_data\\JAVA_DATA\\GunTreeTest\\src\\main\\resources\\myfile.java";
            String dstFile = "D:\\kon_data\\JAVA_DATA\\GunTreeTest\\src\\main\\resources\\myfile02.java";

            ITree src = new JdtTreeGenerator().generateFromFile(srcFile).getRoot();
            ITree dst = new JdtTreeGenerator().generateFromFile(dstFile).getRoot();

            Matcher defaultMatcher = Matchers.getInstance().getMatcher(src,dst);

            MappingStore mappings = defaultMatcher.getMappings();


//
        }
    }

