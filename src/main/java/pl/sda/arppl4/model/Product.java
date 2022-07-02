package pl.sda.arppl4.model;

/* Nazwa
        - Cena
        - Producent
        - Data przydatności
        - Ilość
        - Jednostka
*/

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor

public class Product {
    @Id     //Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY)     // auto_increment
    private Long id;

    private String name;
    private Double price;
    private String producent;
    private LocalDate expiryDate;
    private Double quantity;

    @Enumerated(EnumType.STRING)
    private ProductUnit unit;   // hibetnate czyta enumy wg kolejności i przypisuje do nich nr: 0,1,2
}
