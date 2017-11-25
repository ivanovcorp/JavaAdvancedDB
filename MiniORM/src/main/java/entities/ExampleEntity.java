package entities;

import annotations.Column;
import annotations.Entity;
import annotations.Id;

@Entity(name = "example_entities")
public class ExampleEntity {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "full_name")
    private String fullName;

    public ExampleEntity(){
        super();
    }

    public ExampleEntity(String fullName){
        this.fullName = fullName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFull_name() {
        return fullName;
    }

    public void setFull_name(String full_name) {
        this.fullName = full_name;
    }
}
