package orm.enums;

public enum CreationType {
    CREATE, UPDATE;
    private String value;

    public String getValue(){
        return this.value;
    }
}
