package adidasCopy.adidasCopyBackEnd.item;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ItemEntity {

	private String[] children;
	private String[] parent;
	private String[] category;
	@Id
	private String uniqueId;
	private String text;
	private String name;
	private String brand;
	private String size;
	private String colour;
	private String gender;
	private long cost;
	private byte [] img;
}
