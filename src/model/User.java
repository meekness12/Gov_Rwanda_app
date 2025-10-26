package model;

public class User {
    private int userId;
    private String name, email, password, role;

    public User() {}
    public User(String name, String email, String password, String role){
        this.name=name; this.email=email; this.password=password; this.role=role;
    }

    // Getters and Setters
    public int getUserId(){ return userId; }
    public void setUserId(int id){ this.userId=id; }
    public String getName(){ return name; }
    public void setName(String name){ this.name=name; }
    public String getEmail(){ return email; }
    public void setEmail(String email){ this.email=email; }
    public String getPassword(){ return password; }
    public void setPassword(String password){ this.password=password; }
    public String getRole(){ return role; }
    public void setRole(String role){ this.role=role; }
}
