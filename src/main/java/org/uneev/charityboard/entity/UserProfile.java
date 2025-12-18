package org.uneev.charityboard.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @Column(name = "user_id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "second_name")
    private String secondName;

    @Column(name = "headline")
    private String headline;

    @Column(name = "about")
    private String about;

    @Column(name = "skills")
    private String skills;

    @Column(name = "portfolio")
    private String portfolio;

    @Column(name = "contacts")
    private String contacts;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "author")
    private List<Post> posts;


    public String getFullName() {
        return firstName + " " + secondName;
    }
}
