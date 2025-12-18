package org.uneev.charityboard.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.net.URL;

@Entity
@Data
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url", nullable = false, unique = true)
    private URL url;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}
