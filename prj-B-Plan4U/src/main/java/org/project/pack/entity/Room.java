package org.project.pack.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "RoomTable")
@SequenceGenerator(
    name = "RoomSeq",
    sequenceName = "RoomSeq",
	allocationSize = 1,
	initialValue = 0
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Room {

    @Id
    @GeneratedValue(
    		generator = "RoomSeq",
    		strategy = GenerationType.SEQUENCE)
    Long id;
    String title;
    String subtitle;

    @ManyToOne
    @JoinColumn(name = "host", referencedColumnName = "id")
    User host;

    @OneToMany(mappedBy = "room")
    private List<Guests> guests = new ArrayList<>();
    
    @Lob
    private String imagePath; // 이미지 파일 경로
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated; // 최신 업데이트 날짜
    
    public String getImageUrl() {
		return imagePath;
	}
    
    public void setImageUrl(String imagePath) {
		this.imagePath = imagePath;
	}
    
}

























