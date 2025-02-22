package rendeleskezelo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LitophaneOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Comment cannot be empty")
    @Size(max = 255, message = "Comment cannot exceed 255 characters")
    private String comment;

    @DecimalMin(value = "0", inclusive = false, message = "Revenue cannot be negative")
    private Integer revenue;  // Prices are now integers, in forints

    @DecimalMin(value = "0", inclusive = false, message = "Material cost cannot be negative")
    private Integer materialCost;  // Prices are now integers, in forints

    @Future(message = "Deadline cannot be in the past")
    private LocalDate deadline;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private LocalDateTime modifiedDate;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        modifiedDate = createdDate;
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedDate = LocalDateTime.now();
    }

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @NotNull(message = "Order must have a customer")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "lamp_id")
    @NotNull(message = "Order must have a lamp")
    private Lamp lamp;

    @ManyToOne
    @JoinColumn(name = "status_id")
    @NotNull(message = "Order must have a status")
    private Status status;
}

