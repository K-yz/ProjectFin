package khly.codelean.project2.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Posts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postsid;
    private String name;
    private String image;
    private LocalDate postDate;
    private String description;

    // Getters and Setters
    public Long getPostsid() {
        return postsid;
    }

    public void setPostsid(Long postsid) {
        this.postsid = postsid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public LocalDate getPostDate() {
        return postDate;
    }

    public void setPostDate(LocalDate postDate) {
        this.postDate = postDate;
    }
}
