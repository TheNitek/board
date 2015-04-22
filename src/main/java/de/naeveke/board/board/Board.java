package de.naeveke.board.board;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Board implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID uuid;

    @OneToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "board")
    private Set<Post> posts = new HashSet<>();

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Set<Post> getPosts() {
        return posts;
    }

    protected void setPosts(Set<Post> posts) {
        this.posts = posts;
    }
    
    public void addPost(Post post){
        posts.add(post);
        post.setBoard(this);
    }
    
    public void removePost(Post post){
        posts.remove(post);
        post.setBoard(null);
    }

}
