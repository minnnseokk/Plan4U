package org.project.pack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "GuestsTable")
@SequenceGenerator(
    name = "GuestsSeq",
    sequenceName = "GuestsSeq",
	allocationSize = 1,
	initialValue = 0
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Guests {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GuestsSeq")
    Long id;

    @ManyToOne
    @JoinColumn(name = "guestroom", referencedColumnName = "id")
    Room room;

    @ManyToOne
    @JoinColumn(name = "guest", referencedColumnName = "id")
    User user;
    

}
