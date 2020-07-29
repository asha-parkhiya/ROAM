package com.sparkle.roam.ContentProvider;

/**
 * Created by anildeshpande on 4/3/17.
 */

public class ToDo {
    /**
     * Created by anildeshpande on 4/3/17.
     */

    private String PPID;
    private String codehasformate;
    private String type;

//    public ToDo(){
//        super();
//    }
//
//    public ToDo(long id, String toDo){
//        this.id=id;
//        this.toDo=toDo;
//    }

    public ToDo(String id, String toDo, String place){
        this.PPID=id;
        this.codehasformate=toDo;
        this.type=place;
    }

    public String getPPID() {
        return PPID;
    }

    public void setPPID(String PPID) {
        this.PPID = PPID;
    }

    public String getCodehasformate() {
        return codehasformate;
    }

    public void setCodehasformate(String codehasformate) {
        this.codehasformate = codehasformate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
