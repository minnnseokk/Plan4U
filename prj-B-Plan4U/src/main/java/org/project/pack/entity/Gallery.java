package org.project.pack.entity;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "GalleryTable")
@SequenceGenerator(
    name = "GallerySeq",
    sequenceName = "GallerySeq",
	allocationSize = 1,
	initialValue = 0
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Gallery {
	 	@Id
	    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GallerySeq")
	    Long id;
	 	
	    @ElementCollection
	    @CollectionTable(name = "gallery_image_paths", joinColumns = @JoinColumn(name = "gallery_id"))
	    @Column(name = "image_path")
	    private List<String> imagePaths;
	 	
		@Column(nullable = false) 
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
		private LocalDateTime lastdate; 
	    
		@ManyToOne
	    @JoinColumn(name = "invitedroom", referencedColumnName = "id")
	    Room room;
	    
	    @ManyToOne
	    @JoinColumn(name = "writer", referencedColumnName = "id")
	    User user;
	 	
}
























