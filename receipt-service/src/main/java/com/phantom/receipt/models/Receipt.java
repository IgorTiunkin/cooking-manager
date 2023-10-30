package com.phantom.receipt.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "receipt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int receipt_id;

    @Column(name = "title")
    private String title;

    @ElementCollection (fetch = FetchType.EAGER)
    private List<Integer> product_ids;

    @Column(name = "actions")
    private String actions;


}
