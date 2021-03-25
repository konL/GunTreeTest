package model;
/*
chnageContext=<operaterType , AST Node Type, label, content>
 */

public class changeContext {
    String operaterType,ASTNode_Type, label, content;
    public changeContext(String o,String n,String l,String c){
        operaterType=o;
        ASTNode_Type=n;
        label=l;
        content=c;
    }

    public String getOperaterType() {
        return operaterType;
    }

    public String getASTNode_Type() {
        return ASTNode_Type;
    }

    public String getLabel() {
        return label;
    }

    public String getContent() {
        return content;
    }
}
