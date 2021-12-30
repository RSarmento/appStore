package br.roga.appStore.domain;

import br.roga.appStore.validation.PostValidation;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "app")
@Data
public class App implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "appsSequenceGenerator")
    @GenericGenerator(name = "appsSequenceGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "seq_apps_id"),
            @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
            @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    @NotBlank(groups = {PostValidation.class})
    @Column(name = "name")
    private String name;

    @NotBlank(groups = {PostValidation.class})
    @Column(name = "type")
    private String type;

    @PositiveOrZero
    @NotBlank(groups = {PostValidation.class})
    @Column(name = "price")
    private Double price;
}
