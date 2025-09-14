package com.telcobright.rtc.domainmodel.mysqlentity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "retailpartner")
@Data
public class RetailPartner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "FirstName", length = 255)
    private String firstName;

    @Column(name = "LastName", length = 255)
    private String lastName;

    @Column(name = "UserName", nullable = false, length = 255, unique = true)
    private String userName;

    @Column(name = "Password", nullable = false, columnDefinition = "LONGTEXT")
    private String password;

    @ManyToOne
    @JoinColumn(name = "idPartner", foreignKey = @ForeignKey(name = "retailpartner_ibfk_1"))
    private Partner partner;

//    @OneToMany
//    @JoinColumn(name = "idRetailPartner", foreignKey = @ForeignKey(name = "retailpartner_ibfk_1"))
//    private List<DidAssignment> didAssignment;

//    public RetailPartner(RetailPartnerCreateDto retailPartner, Partner partner, DidAssignment didAssignment) {
//        this.firstName = retailPartner.getFirstName();
//        this.lastName = retailPartner.getLastName();
//        this.password = retailPartner.getPassword();//-----------------------fixme: add password encoder before saving the password
//        this.userName = retailPartner.getUserName();
//        this.partner = partner;
//        this.didAssignment = didAssignment;
//    }

    public RetailPartner() {

    }
}
